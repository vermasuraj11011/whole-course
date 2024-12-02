package day18_19

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{broadcast, col}

object Day18And19 {
  def main(args: Array[String]): Unit = {

    val spark =
      SparkSession
        .builder()
        .appName("scala-spark")
        .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
        .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
        .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
        .config(
          "spark.hadoop.google.cloud.auth.service.account.json.keyfile",
          "src/main/resources/spark-gcs-key.json"
        )
        .master("local[*]")
        .getOrCreate()

    val bucketName          = Utils.BUCKET_NAME
    val user_data_path      = Utils.USER_DATA_PATH
    val tranction_data_path = Utils.TRANSACTION_DATA_PATH

    task_1_broadcastin_and_join(spark, user_data_path, tranction_data_path)
    task_2_caching(spark, user_data_path, tranction_data_path)

    spark.stop()
  }

  // task 1
  // 1. Broadcasting
  //  Question:
  //  You are working on a Spark application that needs to join a small dataset (user details) with a large dataset
  //  (transaction logs). The user details dataset fits in memory. How can you use broadcasting in Spark Scala to
  //  optimize this join operation? Write a code snippet to demonstrate this.
  //
  //  Scenario:
  //
  //  Dataset 1 (User Details): Contains user ID and name. Small enough to fit in memory.
  //  Dataset 2 (Transaction Logs): Large dataset with user ID and transaction details.
  //  Instructions:
  //
  //  Load both datasets as DataFrames.
  //  Use broadcasting to join the datasets efficiently.
  //  Explain why broadcasting is beneficial in this scenario.

  def task_1_broadcastin_and_join(spark: SparkSession, user_path: String, transction_path: String): Unit = {

    val userDetails =
      spark.read.option("header", "true").option("inferSchema", "true").csv(user_path).toDF("user_id", "name")

    val transactionLogs =
      spark
        .read
        .option("header", "true")
        .option("inferSchema", "true")
        .csv(transction_path)
        .toDF("transaction_id", "product_id", "customer_id", "transaction_date", "list_price")

    val joinedDF = transactionLogs.join(broadcast(userDetails), Seq("user_id"))

    joinedDF.show(10)
  }

//  Task 2
//  2. Caching
//    Question:
//    You have a Spark job that involves processing a DataFrame multiple times in different stages.
  //    Describe how caching can improve performance in this scenario. Write a code snippet to
  //    demonstrate caching a DataFrame.
//
//    Scenario:
//
//    A DataFrame representing sales data is used multiple times in transformations.
//    The job becomes slower as the DataFrame is recomputed repeatedly.
//  Instructions:
//
//    Load a sample dataset as a DataFrame.
//    Apply caching to improve the performance.
//  Measure and compare the execution time with and without caching.

  def task_2_caching(spark: SparkSession, user_path: String, transction_path: String): Unit = {
    val transactionLogs =
      spark
        .read
        .option("header", "true")
        .option("inferSchema", "true")
        .csv(transction_path)
        .toDF("transaction_id", "product_id", "customer_id", "transaction_date", "list_price")

    val userDetails =
      spark.read.option("header", "true").option("inferSchema", "true").csv(user_path).toDF("user_id", "name")

    val joinedDF = transactionLogs.join(broadcast(userDetails), Seq("user_id"))

//    before caching
    val startTime = System.currentTimeMillis()

    //   find the user who has done the maximum transaction
    joinedDF.groupBy("user_id").count().orderBy(col("count").desc).show(1)

    //    find the user who has done the minimum transaction
    joinedDF.groupBy("user_id").count().orderBy(col("count").asc).show(1)

    //    find the list of users who has done more than 10 transaction
    joinedDF.groupBy("user_id").count().filter("count > 10").show()

    val endTime = System.currentTimeMillis()

    println(s"Time taken before caching: ${endTime - startTime} ms")

//    After caching
    joinedDF.cache()

    val startTimeAfterCaching = System.currentTimeMillis()

//   find the user who has done the maximum transaction
    joinedDF.groupBy("user_id").count().orderBy(col("count").desc).show(1)

//    find the user who has done the minimum transaction
    joinedDF.groupBy("user_id").count().orderBy(col("count").asc).show(1)

//    find the list of users who has done more than 10 transaction
    joinedDF.groupBy("user_id").count().filter("count > 10").show()

    val endTimeAfterCaching = System.currentTimeMillis()

    println(s"Time taken after caching: ${endTimeAfterCaching - startTimeAfterCaching} ms")
  }
}
