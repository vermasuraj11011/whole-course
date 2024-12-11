package util

import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkSessionFactory {

  def getSession: SparkSession =
    SparkSession
      .builder()
      .appName("Walmart Data Processing")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config(
        "spark.hadoop.google.cloud.auth.service.account.json.keyfile",
        "/Users/surajverma/Documents/course/whole-course/spark-projects/case-study4/data-processing-optimization/src/main/resources/gcs-key.json"
      )
      .master("local[*]")
      .getOrCreate()

  def getDfFromCsv(path: String): DataFrame = {
    val spark = getSession
    spark.read.option("header", "true").option("inferSchema", "true").csv(path)
  }
}
