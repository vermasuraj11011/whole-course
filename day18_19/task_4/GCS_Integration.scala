package day18_19

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

// 4. GCS Integration
// Question:
//  You are tasked with writing a Spark Scala job that reads a Parquet file from Google Cloud Storage (GCS),
//  processes it, and writes the result back to GCS. Write a code snippet for this task.
//
// Scenario:
//
//  Input File: gs://bucket-name/input/data.parquet
//  Processing: Filter rows where the column status = "completed".
//  Output Path: gs://bucket-name/output/processed_data.parquet
//  Instructions:
//
//    Set up the GCS credentials in your Spark application.
//    Read the Parquet file from GCS.
//  Apply the filter transformation.
//  Write the processed data back to GCS in Parquet format.

object GCS_Integration {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Filter Orders")
      .master("local[*]")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "src/main/scala/spark-gcs-key.json")
      .getOrCreate()

    val inputPath = Utils.INPUT_PATH + "input_data.parquet"
    val outputPath = Utils.OUTPUT_PATH + "final_data.parquet"

    val inputData = spark.read
      .parquet(inputPath)

    println("Input Data Schema:")
    inputData.printSchema()

    val filteredData = inputData.filter(col("order_status") === "completed")

    println("Filtered Data Schema:")
    filteredData.printSchema()

    filteredData.write
      .mode("overwrite")
      .parquet(outputPath)

    println(s"Filtered data successfully written to $outputPath")

    spark.stop()
  }
}
