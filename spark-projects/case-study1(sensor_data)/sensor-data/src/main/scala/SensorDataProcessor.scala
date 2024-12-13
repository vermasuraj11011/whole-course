import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.protobuf.functions.{from_protobuf, to_protobuf}
import org.apache.spark.sql.streaming.Trigger

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object SensorDataProcessor {

  // Path to the Google Cloud Storage credentials file
  val gcs_creds =
    "/Users/surajverma/Documents/course/whole-course/spark-projects/case-study1(sensor_data)/sensor-data/src/main/resources/gcs-key.json"

  val

  def main(args: Array[String]): Unit = {
    // Initialize Spark session
    val spark                 = initializeSparkSession()
    val kafkaBootstrapServers = "localhost:9092"
    val sensorReadTopic       = "sensor-readings"

    // Paths for project and Protobuf descriptor
    val project_path =
      "/Users/surajverma/Documents/course/whole-course/spark-projects/case-study1(sensor_data)/sensor-data"
    val package_path           = s"$project_path/src/main/scala"
    val protobufDescriptorPath = s"$package_path/descriptor/SensorReading.desc"
    val protobufMessageType    = "proto.SensorReadingProto"

    val base_gcs_path = "gs://scala-spark-temp/sensor-reading"

    // Read data from Kafka stream
    val kafkaStreamData = readKafkaStream(spark, kafkaBootstrapServers, sensorReadTopic)
    // Validate and parse sensor data
    val validatedSensorData = validateSensorData(kafkaStreamData, protobufDescriptorPath, protobufMessageType)

    // Process the stream data in batches
    val streamQuery =
      validatedSensorData
        .writeStream
        .trigger(Trigger.ProcessingTime("10 seconds"))
        .foreachBatch { (batchData: Dataset[Row], batchId: Long) =>
          processBatch(batchData, batchId, base_gcs_path, protobufDescriptorPath, protobufMessageType, spark)
        }
        .start()

    // Await termination of the stream query
    streamQuery.awaitTermination()
    spark.stop()
  }

  // Initialize Spark session with necessary configurations
  def initializeSparkSession(): SparkSession =
    SparkSession
      .builder()
      .master("local[*]")
      .appName("Streaming Sensor Data Processor")
      .config("spark.hadoop.fs.defaultFS", "gs://scala-spark-temp/")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", gcs_creds)
      .getOrCreate()

  // Read data from Kafka stream
  private def readKafkaStream(spark: SparkSession, bootstrapServers: String, topic: String): DataFrame =
    spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", bootstrapServers)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .load()

  // Validate and parse sensor data from Protobuf format
  private def validateSensorData(df: DataFrame, descriptorPath: String, messageType: String): DataFrame =
    df.selectExpr("CAST(value AS BINARY) as value")
      .select(from_protobuf(col("value"), messageType, descriptorPath).alias("sensorReading"))
      .select("sensorReading.*")
      .filter(col("temperature").between(-50, 150) && col("humidity").between(0, 100))
      .na
      .fill(Map("temperature" -> 0.0f, "humidity" -> 0.0f))

  // Process each batch of sensor data
  private def processBatch(
    batchData: Dataset[Row],
    batchId: Long,
    basePath: String,
    descriptorPath: String,
    messageType: String,
    spark: SparkSession
  ): Unit = {
    val currentTimestamp = LocalDateTime.now()
    val formatter        = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH")

    // Define paths for raw and aggregated data
    val rawDataFolder        = s"$basePath/raw-data/${currentTimestamp.format(formatter)}/"
    val aggregatedDataFolder = s"$basePath/aggregate/protobuf/${currentTimestamp.format(formatter)}/"
    val previousAggregatedDataFolder =
      s"$basePath/aggregate/protobuf/${currentTimestamp.minusHours(1).format(formatter)}/"
    val jsonAggregatedDataPath = s"$basePath/aggregate/json/${currentTimestamp.format(formatter)}/"
    println(s"Processing batch $batchId, saving raw data to $rawDataFolder")

    // Serialize data to Protobuf format
    val serializedData =
      batchData
        .withColumn("value", to_protobuf(struct(batchData.columns.map(col): _*), messageType, descriptorPath))
        .select("value")

    serializedData.write.mode(SaveMode.Append).format("parquet").save(rawDataFolder)
    incrementalAggregation(batchData, aggregatedDataFolder, previousAggregatedDataFolder, jsonAggregatedDataPath, spark)

  }

  // Perform incremental aggregation of sensor data
  private def incrementalAggregation(
    newRawData: DataFrame,
    currentAggregatedFolder: String,
    previousAggregatedFolder: String,
    jsonAggregatedPath: String,
    spark: SparkSession
  ): Unit = {
    val hdfs = FileSystem.get(spark.sparkContext.hadoopConfiguration)

    // Read existing aggregated data if available
    val existingAggregatedData =
      if (hdfs.exists(new Path(currentAggregatedFolder))) {
        readAggregatedData(currentAggregatedFolder, spark)
      } else if (hdfs.exists(new Path(previousAggregatedFolder))) {
        readAggregatedData(previousAggregatedFolder, spark)
      } else {
        null
      }

    // Compute new aggregated metrics
    val newAggregatedMetrics = computeAggregatedMetrics(newRawData, existingAggregatedData)

    // Save aggregated metrics to Protobuf and JSON formats
    saveAggregatedSensorMetrics(newAggregatedMetrics, currentAggregatedFolder, jsonAggregatedPath)
  }

  // Read aggregated data from a given folder
  private def readAggregatedData(folderPath: String, spark: SparkSession): DataFrame =
    spark.read.format("parquet").load(folderPath).cache()

  /** Compute aggregated metrics by combining new and existing data.
    * @param newRawData
    *   DataFrame containing new raw data.
    * @param existingAggregatedData
    *   DataFrame containing existing aggregated data.
    * @return
    *   DataFrame with updated aggregated metrics.
    */
  private def computeAggregatedMetrics(newRawData: DataFrame, existingAggregatedData: DataFrame): DataFrame = {
    // Compute new aggregated metrics from raw data
    val newAggregatedMetrics =
      newRawData
        .groupBy("sensorId")
        .agg(
          avg("temperature").alias("avgTemperature_new"),
          avg("humidity").alias("avgHumidity_new"),
          min("temperature").alias("minTemperature_new"),
          max("temperature").alias("maxTemperature_new"),
          min("humidity").alias("minHumidity_new"),
          max("humidity").alias("maxHumidity_new"),
          count("sensorId").alias("weight_new")
        )

    // Merge with existing aggregated data if available
    if (existingAggregatedData != null) {
      newAggregatedMetrics
        .join(existingAggregatedData, Seq("sensorId"), "outer")
        .select(
          coalesce(col("sensorId"), col("sensorId")).alias("sensorId"),
          calculateWeightedAverage("avgTemperature", "avgTemperature_new", "weight", "weight_new")
            .alias("avgTemperature"),
          calculateWeightedAverage("avgHumidity", "avgHumidity_new", "weight", "weight_new").alias("avgHumidity"),
          least(col("minTemperature"), col("minTemperature_new")).alias("minTemperature"),
          greatest(col("maxTemperature"), col("maxTemperature_new")).alias("maxTemperature"),
          least(col("minHumidity"), col("minHumidity_new")).alias("minHumidity"),
          greatest(col("maxHumidity"), col("maxHumidity_new")).alias("maxHumidity"),
          (coalesce(col("weight"), lit(0)) + coalesce(col("weight_new"), lit(0))).alias("weight")
        )
    } else {
      newAggregatedMetrics.selectExpr(
        "sensorId",
        "avgTemperature_new as avgTemperature",
        "avgHumidity_new as avgHumidity",
        "minTemperature_new as minTemperature",
        "maxTemperature_new as maxTemperature",
        "minHumidity_new as minHumidity",
        "maxHumidity_new as maxHumidity",
        "weight_new as weight"
      )
    }
  }

  // Calculate weighted average for a given column
  private def calculateWeightedAverage(oldValue: String, newValue: String, oldWeight: String, newWeight: String) =
    ((coalesce(col(oldValue), lit(0.0)) * coalesce(col(oldWeight), lit(0))) +
      (coalesce(col(newValue), lit(0.0)) * coalesce(col(newWeight), lit(0)))) /
      (coalesce(col(oldWeight), lit(0)) + coalesce(col(newWeight), lit(0)))

  // Save aggregated sensor metrics to Protobuf and JSON formats
  private def saveAggregatedSensorMetrics(newData: DataFrame, protobufPath: String, jsonPath: String): Unit = {
    newData.write.mode(SaveMode.Overwrite).parquet(protobufPath)
    newData.write.mode(SaveMode.Overwrite).json(jsonPath)
  }
}
