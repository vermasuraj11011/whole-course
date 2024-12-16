package optimization

import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.protobuf.functions.from_protobuf
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.storage.StorageLevel
import util.SparkSessionFactory

object KafkaSalesConsumer {

  def main(args: Array[String]): Unit = {

    println("Application starting...")

    // Step 1: Initialize Spark session
    val spark = SparkSessionFactory.getSession
    import spark.implicits._

    // Step 2: Define Kafka and Protobuf configurations
    val kafkaBootstrapServers = "localhost:9092"
    val salesTopic            = "sales-topic"
    val protobufDescriptorPath =
      "/Users/surajverma/Documents/course/whole-course/spark-projects/case-study4/data-processing-optimization/src/main/scala/optimization/descriptor/SalesRecord.desc"
    val protobufMessageType = "optimization.proto.SalesRecord"

    // Step 3: Define paths for data storage
    val bucketName       = "scala-spark-temp"
    val basePath         = s"gs://$bucketName/walmart_data"
    val featuresFilePath = s"$basePath/features.csv"
    val storesFilePath   = s"$basePath/stores.csv"

    // Step 4: Load and prepare feature and store data
    val rawFeaturesData = SparkSessionFactory.getDfFromCsv(featuresFilePath)
//    rawFeaturesData.show(10)
    val rawStoresData = SparkSessionFactory.getDfFromCsv(storesFilePath)
//    rawStoresData.show(10)

    val featuresData = rawFeaturesData.na.drop("any", Seq("Store", "Date")).cache()
    val storesData   = broadcast(rawStoresData.na.drop("any", Seq("Store", "Type", "Size")))

    // Step 5: Read data from Kafka
    val kafkaStreamData =
      spark
        .readStream
        .format("kafka")
        .option("kafka.bootstrap.servers", kafkaBootstrapServers)
        .option("subscribe", salesTopic)
        .option("startingOffsets", "earliest")
        .load()

    // Step 6: Deserialize Kafka data using Protobuf
    val salesData =
      kafkaStreamData
        .selectExpr("CAST(value AS BINARY) as value")
        .select(from_protobuf($"value", protobufMessageType, protobufDescriptorPath).alias("salesRecord"))
        .select("salesRecord.*")
        .na
        .fill(Map("is_holiday" -> false, "weekly_sales" -> 0.0f))

    salesData.printSchema()

    // Step 7: Write stream data to CSV and update metrics
    val streamQuery =
      salesData
        .writeStream
        .trigger(Trigger.ProcessingTime("10 seconds"))
        .foreachBatch { (batchData: Dataset[Row], batchId: Long) =>
          println(s"Processing batch: $batchId")
//          batchData.show(10)
          batchData.write.mode("append").option("header", "true").csv(s"$basePath/dynamic_data/train.csv")

          updateEnrichedAndMetrics(featuresData, storesData, basePath)
          println(s"Batch $batchId processed successfully")
        }
        .start()

    streamQuery.awaitTermination()
    spark.stop()
  }

  private def updateEnrichedAndMetrics(featuresData: DataFrame, storesData: DataFrame, basePath: String): Unit = {

    // Step 8: Load and prepare training data
    val trainFilePath    = s"$basePath/dynamic_data/train.csv"
    val enrichedDataPath = s"$basePath/dynamic_data/enriched_data"

    val rawTrainData = SparkSessionFactory.getDfFromCsv(trainFilePath)
//    rawTrainData.show(10)
    val trainData =
      rawTrainData.filter("Weekly_Sales >= 0").na.drop("any", Seq("Store", "Department", "Weekly_Sales", "Date"))

    // Step 9: Enrich training data with features and store data
    val enrichedData =
      trainData
        .withColumnRenamed("is_holiday", "IsHoliday")
        .join(featuresData, Seq("Store", "Date", "IsHoliday"), "left")
        .join(storesData, Seq("Store"), "left")

//    enrichedData.show(10)

    val partitionedEnrichedData = enrichedData.repartition(col("Store"), col("Date")).cache()

    partitionedEnrichedData
      .limit(1000)
      .write
      .mode(SaveMode.Overwrite)
      .partitionBy("Store", "Date")
      .parquet(enrichedDataPath)

//     Step 10: Calculate and save store metrics
    val storeMetrics =
      partitionedEnrichedData
        .groupBy("Store")
        .agg(sum("Weekly_Sales").alias("Total_Weekly_Sales"), avg("Weekly_Sales").alias("Average_Weekly_Sales"))
        .orderBy(desc("Total_Weekly_Sales"))
        .persist(StorageLevel.MEMORY_ONLY)

//    storeMetrics.show(10)
    storeMetrics.write.mode("overwrite").json(s"$basePath/store_wise")

    // Step 11: Calculate and save department metrics
    val departmentMetrics =
      partitionedEnrichedData
        .groupBy("Store", "department")
        .agg(sum("Weekly_Sales").alias("Total_Sales"), avg("Weekly_Sales").alias("Average_Sales"))
        .persist(StorageLevel.MEMORY_AND_DISK_SER)

//    departmentMetrics.show(10)
    departmentMetrics.write.mode("overwrite").json(s"$basePath/department_wise")

    // Step 12: Calculate and save holiday comparison metrics
    val holidaySalesMetrics =
      partitionedEnrichedData
        .filter("IsHoliday = true")
        .groupBy("Store", "department")
        .agg(sum("Weekly_Sales").alias("Holiday_Sales"))
        .persist(StorageLevel.MEMORY_AND_DISK)

    val nonHolidaySalesMetrics =
      partitionedEnrichedData
        .filter("IsHoliday = false")
        .groupBy("Store", "department")
        .agg(sum("Weekly_Sales").alias("Non_Holiday_Sales"))

    val holidayComparisonMetrics =
      holidaySalesMetrics
        .join(nonHolidaySalesMetrics, Seq("Store", "department"), "outer")
        .orderBy(desc("Holiday_Sales"))

//    holidayComparisonMetrics.show(10)
    holidayComparisonMetrics.write.mode(SaveMode.Overwrite).json(s"$basePath/holiday_vs_non_holiday")
  }
}