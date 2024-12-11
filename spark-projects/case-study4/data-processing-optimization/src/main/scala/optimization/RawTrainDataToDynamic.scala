package optimization

import org.apache.spark.sql.{SaveMode, SparkSession}
import util.SparkSessionFactory

object RawTrainDataToDynamic {

  def main(args: Array[String]): Unit = {

    // Step 1: Initialize Spark session
    val spark = SparkSessionFactory.getSession

    // Step 2: Define paths for data storage
    val bucketName = "scala-spark-temp"
    val basePath   = s"gs://$bucketName/walmart_data"

    val updatedTrainFilePath = s"$basePath/updated_train.csv"
    val initialTrainFilePath = s"$basePath/train.csv"

    // Step 3: Load initial training data
    val initialTrainData = SparkSessionFactory.getDfFromCsv(initialTrainFilePath)
    initialTrainData.show(10)

    // Step 4: Write initial training data to updated path
    initialTrainData.write.mode(SaveMode.Overwrite).option("header", "true").csv(updatedTrainFilePath)

    // Step 5: Load updated training data
    val updatedTrainData = SparkSessionFactory.getDfFromCsv(updatedTrainFilePath)
    updatedTrainData.show(10)
    println("Last 5 records from updated training data:")
    updatedTrainData.collect().takeRight(5).foreach(println)

    // Step 6: Load and display store-wise metrics
    val storeWiseMetricsPath = s"$basePath/aggregated_metrics/store_metrics"
    val storeMetrics         = spark.read.option("inferSchema", "true").json(storeWiseMetricsPath)
    storeMetrics.show(10)

    // Step 7: Load and display department-wise metrics
    val departmentWiseMetricsPath = s"$basePath/aggregated_metrics/department_metrics"
    val departmentMetrics         = spark.read.option("inferSchema", "true").json(departmentWiseMetricsPath)
    departmentMetrics.show(10)

    // Step 8: Load and display holiday comparison metrics
    val holidayComparisonMetricsPath = s"$basePath/aggregated_metrics/holiday_vs_non_holiday_metrics"
    val holidayComparisonMetrics     = spark.read.option("inferSchema", "true").json(holidayComparisonMetricsPath)
    holidayComparisonMetrics.show(10)

    // Step 9: Stop Spark session
    spark.stop()
  }
}