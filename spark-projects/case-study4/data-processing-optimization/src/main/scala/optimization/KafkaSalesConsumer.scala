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
    rawFeaturesData.show(10)
    val rawStoresData = SparkSessionFactory.getDfFromCsv(storesFilePath)
    rawStoresData.show(10)

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
          batchData.show(10)
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
    rawTrainData.show(10)
    val trainData =
      rawTrainData.filter("Weekly_Sales >= 0").na.drop("any", Seq("Store", "Department", "Weekly_Sales", "Date"))

    // Step 9: Enrich training data with features and store data
    val enrichedData =
      trainData
        .withColumnRenamed("is_holiday", "IsHoliday")
        .join(featuresData, Seq("Store", "Date", "IsHoliday"), "left")
        .join(storesData, Seq("Store"), "left")

    enrichedData.show(10)

    val partitionedEnrichedData = enrichedData.repartition(col("Store"), col("Date")).cache()

//    partitionedEnrichedData
//      .limit(1000)
//      .write
//      .mode(SaveMode.Overwrite)
//      .partitionBy("Store", "Date")
//      .parquet(enrichedDataPath)

    // Step 10: Calculate and save store metrics
//    val storeMetrics =
//      partitionedEnrichedData
//        .groupBy("Store")
//        .agg(sum("Weekly_Sales").alias("Total_Weekly_Sales"), avg("Weekly_Sales").alias("Average_Weekly_Sales"))
//        .orderBy(desc("Total_Weekly_Sales"))
//        .persist(StorageLevel.MEMORY_ONLY)

//    storeMetrics.show(10)
//    storeMetrics.write.mode("overwrite").json(s"$basePath/store_wise")

    println("jfgfgfghfghfghfhfhgf")

    // Step 11: Calculate and save department metrics
    val departmentMetrics =
      partitionedEnrichedData
        .groupBy("Store", "department")
        .agg(sum("Weekly_Sales").alias("Total_Sales"), avg("Weekly_Sales").alias("Average_Sales"))
        .persist(StorageLevel.MEMORY_AND_DISK_SER)

    departmentMetrics.show(10)
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

    holidayComparisonMetrics.show(10)
    holidayComparisonMetrics.write.mode(SaveMode.Overwrite).json(s"$basePath/holiday_vs_non_holiday")
  }
}

//package optimization
//
//import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.protobuf.functions.from_protobuf
//import org.apache.spark.sql.streaming.Trigger
//import org.apache.spark.storage.StorageLevel
//import util.SparkSessionFactory
//
//object KafkaSalesConsumer {
//
//  def main(args: Array[String]): Unit = {
//
//    println("application started ...")
//
//    val spark = SparkSessionFactory.getSession
//
//    import spark.implicits._
//
//    val kafkaBootstrapServers = "localhost:9092"
//    val topic                 = "sales-topic"
//
//    val descriptorFile =
//      "/Users/surajverma/Documents/course/whole-course/spark-projects/case-study4/data-processing-optimization/src/main/scala/optimization/descriptor/SalesRecord.desc"
//
//    val messageType = "optimization.proto.SalesRecord"
//
//    println("1. SparkSession initialized")
//
//    val bucketName   = "scala-spark-temp"
//    val base_path    = s"gs://$bucketName/walmart_data"
//    val featuresPath = s"$base_path/features.csv"
//    val storesPath   = s"$base_path/stores.csv"
//
//    val rawFeaturesDF = SparkSessionFactory.getDfFromCsv(featuresPath)
//
////    rawFeaturesDF.show(10)
//
//    val rawStoresDF = SparkSessionFactory.getDfFromCsv(storesPath)
//
////    rawStoresDF.show(10)
//
//    val featuresDF = rawFeaturesDF.na.drop("any", Seq("Store", "Date")).cache()
//
//    val storesDF = broadcast(rawStoresDF.na.drop("any", Seq("Store", "Type", "Size")))
//
//    println("2. Data Preparation:")
//
//    val kafkaDF =
//      spark
//        .readStream
//        .format("kafka")
//        .option("kafka.bootstrap.servers", kafkaBootstrapServers)
//        .option("subscribe", topic)
//        .option("startingOffsets", "earliest")
//        .load()
//
//    println("3. Kafka data loaded")
//
//    val salesDF =
//      kafkaDF
//        .selectExpr("CAST(value AS BINARY) as value")
//        .select(from_protobuf($"value", messageType, descriptorFile).alias("salesRecord"))
//        .select("salesRecord.*")
//        .na
//        .fill(Map("is_holiday" -> false, "weekly_sales" -> 0.0f))
//
//    println("4. Kafka data deserialized")
//
//    salesDF.printSchema()
//
//    val query =
//      salesDF
//        .writeStream
//        .trigger(Trigger.ProcessingTime("10 seconds"))
//        .foreachBatch { (batchDF: Dataset[Row], batchId: Long) =>
//          println(s"Processing batch: $batchId")
//          batchDF.show()
//          batchDF.write.mode("append").option("header", "true").csv(s"$base_path/dynamic_data/train.csv")
//          updateEnrichedAndMetrics(spark, featuresDF, storesDF, base_path)
//          println(s"Batch $batchId processed successfully")
//        }
//        .start()
//
//    query.awaitTermination()
//    spark.stop()
//  }
//
//  def updateEnrichedAndMetrics(
//    spark: SparkSession,
//    featuresDF: DataFrame,
//    storesDF: DataFrame,
//    base_path: String
//  ): Unit = {
//
//    println("Updating enriched data and metrics")
//
//    val trainPath    = s"$base_path/dynamic_data/train.csv"
//    val enrichedPath = s"$base_path/dynamic_data/enriched_data"
//
//    val rawTrainDF = SparkSessionFactory.getDfFromCsv(trainPath)
//
//    val trainDF =
//      rawTrainDF.filter("Weekly_Sales >= 0").na.drop("any", Seq("Store", "Department", "Weekly_Sales", "Date"))
//
//    println("1 - Data Validation and Enrichment:")
//
//    trainDF.show(10)
//    featuresDF.show(10)
//    storesDF.show(10)
//
//    val newEnrichedDF =
//      trainDF
//        .withColumnRenamed("is_holiday", "IsHoliday")
//        .join(featuresDF, Seq("Store", "Date", "IsHoliday"), "left")
//        .join(storesDF, Seq("Store"), "left")
//
//    newEnrichedDF.show(10)
//
//    val partitionedParquetPath = s"$base_path/enriched_data"
//    val partitionedEnrichedDF  = newEnrichedDF.repartition(col("Store"), col("Date")).cache()
//
//    partitionedEnrichedDF
//      .limit(1000)
//      .write
//      .mode(SaveMode.Overwrite)
//      .partitionBy("Store", "Date")
//      .parquet(partitionedParquetPath)
//
//    val storeMetrics =
//      partitionedEnrichedDF
//        .groupBy("Store")
//        .agg(sum("Weekly_Sales").alias("Total_Weekly_Sales"), avg("Weekly_Sales").alias("Average_Weekly_Sales"))
//        .orderBy(desc("Total_Weekly_Sales"))
//        .persist(StorageLevel.MEMORY_ONLY)
//    val storeWiseAggregatedMetricsPath = s"$base_path/store_wise"
//    storeMetrics.write.mode("overwrite").json(storeWiseAggregatedMetricsPath)
//
//    println("3 - Data Validation and Enrichment:")
//
//    val departmentMetrics =
//      partitionedEnrichedDF
//        .groupBy("Store", "Dept")
//        .agg(sum("Weekly_Sales").alias("Total_Sales"), avg("Weekly_Sales").alias("Average_Sales"))
//        .persist(StorageLevel.MEMORY_AND_DISK_SER)
//    val deptWiseAggregatedMetricsPath = s"$base_path/department_wise"
//    departmentMetrics.write.mode("overwrite").json(deptWiseAggregatedMetricsPath)
//
//    val holidaySales =
//      partitionedEnrichedDF
//        .filter("IsHoliday = true")
//        .groupBy("Store", "Dept")
//        .agg(sum("Weekly_Sales").alias("Holiday_Sales"))
//        .persist(StorageLevel.MEMORY_AND_DISK)
//
//    val nonHolidaySales =
//      partitionedEnrichedDF
//        .filter("IsHoliday = false")
//        .groupBy("Store", "Dept")
//        .agg(sum("Weekly_Sales").alias("Non_Holiday_Sales"))
//
//    val holidayComparison =
//      holidaySales.join(nonHolidaySales, Seq("Store", "Dept"), "outer").orderBy(desc("Holiday_Sales"))
//
//    val holidayVsNonHolidayMetricsPath = s"$base_path/holiday_vs_non_holiday"
//    holidayComparison.write.mode(SaveMode.Overwrite).json(holidayVsNonHolidayMetricsPath)
//
//    println(s"Updated enriched data at $enrichedPath")
//    println(s"Updated metrics")
//  }
//}
