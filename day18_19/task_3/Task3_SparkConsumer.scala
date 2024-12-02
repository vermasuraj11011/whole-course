package day18_19

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object Task3_SparkConsumer {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder()
      .appName("Kafka Streaming Application")
      .master("local[*]")
      .getOrCreate()

    sparkSession.sparkContext.setLogLevel("ERROR")

    val transactionSchema = new StructType()
      .add("transactionId", StringType)
      .add("userId", StringType)
      .add("amount", IntegerType)

    val kafkaSource = sparkSession.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "transactions")
      .option("startingOffsets", "latest")
      .load()

    val transactionData = kafkaSource
      .selectExpr("CAST(value AS STRING) AS jsonString")
      .select(from_json(col("jsonString"), transactionSchema).as("parsed"))
      .select("parsed.transactionId", "parsed.amount", "parsed.userId")

    val enrichedTransactions = transactionData.withColumn("eventTime", current_timestamp())

    val aggregatedData = enrichedTransactions
      .withWatermark("eventTime", "10 seconds")
      .groupBy(window(col("eventTime"), "10 seconds"))
      .agg(sum("amount").as("totalTransactionAmount"))

    val consoleOutput = aggregatedData.writeStream
      .outputMode("update")
      .format("console")
      .option("truncate", "false")
      .start()

    consoleOutput.awaitTermination()
  }
}
