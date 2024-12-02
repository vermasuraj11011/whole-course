package day18_19

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types._

// 5. Combining Broadcasting and Streaming with GCS
// Question:
//  Create a Spark Structured Streaming application that consumes data from a Kafka topic, performs
//  a lookup using a broadcast variable (from GCS), and writes the enriched data back to GCS.
//
// Scenario:
//
//  Kafka Topic: orders
//  Broadcast Dataset: A CSV file in GCS (gs://bucket-name/config/user_details.csv) containing user details.
//    Task: Enrich the streaming data from Kafka with user details from the broadcast variable and write the enriched data back to GCS in JSON format.
//    Output Path: gs://bucket-name/output/enriched_orders/
//  Instructions:
//
//    Broadcast the user details dataset after loading it from GCS.
//  Join the streaming Kafka data with the broadcast dataset.
//    Write the enriched streaming data back to GCS.

object Task5_SparkConsumer {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Order Consumer with Broadcast Join")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "src/main/resources/spark-gcs-key.json")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val userDetailsPath = Utils.USER_DATA_PATH
    val outputPath = Utils.OUTPUT_PATH

    val userDetails = spark.read
      .option("header", "true")
      .csv(userDetailsPath)

    println("Broadcasted User Details Schema:")
    userDetails.printSchema()

    val userDetailsBroadcast = broadcast(userDetails)

    val kafkaStream = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "orders")
      .load()

    val orderSchema = new StructType()
      .add("orderId", StringType)
      .add("userId", StringType)
      .add("orderAmount", DoubleType)

    val ordersStream = kafkaStream.selectExpr("CAST(value AS STRING) AS jsonString")
      .select(from_json(col("jsonString"), orderSchema).as("data"))
      .select("data.*")

    val enrichedOrders = ordersStream
      .join(userDetailsBroadcast, Seq("userId"), "left")
      .select(
        col("orderId"),
        col("userId"),
        col("orderAmount"),
        col("name").alias("userName"),
        col("email").alias("userEmail")
      )

    val query = enrichedOrders.writeStream
      .outputMode("append")
      .format("json")
      .option("path", outputPath)
      .option("checkpointLocation", Utils.CHECK_POINT_PATH)
      .trigger(Trigger.ProcessingTime("30 seconds"))
      .start()

    query.awaitTermination()
  }
}