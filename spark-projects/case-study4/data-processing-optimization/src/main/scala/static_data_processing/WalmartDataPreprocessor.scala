package static_data_processing

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.storage.StorageLevel
import util.SparkSessionFactory

object WalmartDataPreprocessor {

  def main(args: Array[String]): Unit = {

    // Step 1: Initialize Spark session
    val sparkSession = SparkSessionFactory.getSession

    // Step 2: Define paths for data storage
    val bucketName       = "scala-spark-temp"
    val basePath         = s"gs://$bucketName/walmart_data"
    val featuresFilePath = s"$basePath/features.csv"
    val trainFilePath    = s"$basePath/train.csv"
    val storesFilePath   = s"$basePath/stores.csv"

    // Step 3: Load data from CSV files
    val featuresDataFrame = SparkSessionFactory.getDfFromCsv(featuresFilePath)
    val salesDataFrame    = SparkSessionFactory.getDfFromCsv(trainFilePath)
    val storesDataFrame   = SparkSessionFactory.getDfFromCsv(storesFilePath)

    // Step 4: Validate data
    val validatedSalesDataFrame =
      salesDataFrame.filter(col("Weekly_Sales") >= 0).na.drop("any", Seq("Store", "Dept", "Weekly_Sales", "Date"))
    val validatedFeaturesDataFrame = featuresDataFrame.na.drop("any", Seq("Store", "Date"))
    val validatedStoresDataFrame   = storesDataFrame.na.drop("any", Seq("Store", "Type", "Size"))

    println("Validated DataFrames:")
    validatedSalesDataFrame.show(10)
    validatedFeaturesDataFrame.show(10)
    validatedStoresDataFrame.show(10)

    // Step 5: Cache and broadcast data
    val cachedFeaturesDataFrame    = validatedFeaturesDataFrame.cache()
    val broadcastedStoresDataFrame = broadcast(validatedStoresDataFrame)

    // Step 6: Enrich sales data
    val enrichedDataFrame =
      validatedSalesDataFrame
        .join(cachedFeaturesDataFrame, Seq("Store", "Date", "IsHoliday"), "left")
        .join(broadcastedStoresDataFrame, Seq("Store"), "left")

    println("Enriched DataFrame:")
    enrichedDataFrame.show(10)

    // Step 7: Write enriched data to Parquet
    val enrichedDataOutputPath       = s"$basePath/enriched_data"
    val partitionedEnrichedDataFrame = enrichedDataFrame.repartition(col("Store"), col("Date")).cache()

    partitionedEnrichedDataFrame
      .limit(1000)
      .write
      .mode(SaveMode.Overwrite)
      .partitionBy("Store", "Date")
      .parquet(enrichedDataOutputPath)

    // Step 8: Calculate and save metrics
    calculateMetrics(partitionedEnrichedDataFrame, basePath)
    sparkSession.stop()
  }

  def calculateMetrics(enrichedDataFrame: DataFrame, basePath: String): Unit = {

    // Step 9: Calculate store-level metrics
    val storeLevelMetrics =
      enrichedDataFrame
        .groupBy("Store")
        .agg(
          sum("Weekly_Sales").alias("Total_Weekly_Sales_Millions"),
          avg("Weekly_Sales").alias("Average_Weekly_Sales")
        )
        .orderBy(desc("Total_Weekly_Sales_Millions"))
        .cache()

    val formattedStoreMetrics =
      storeLevelMetrics
        .withColumn("Total_Weekly_Sales_Millions", format_number(col("Total_Weekly_Sales_Millions") / 1e6, 2))
        .withColumn("Average_Weekly_Sales", format_number(col("Average_Weekly_Sales"), 2))
        .persist(StorageLevel.MEMORY_ONLY)

    val storeMetricsPath = s"$basePath/aggregated_metrics/store_metrics"
    formattedStoreMetrics.write.mode(SaveMode.Overwrite).json(storeMetricsPath)

    println("Store-Level Metrics:")
    formattedStoreMetrics.show(10)

    // Step 10: Calculate department-level metrics
    val departmentLevelMetrics =
      enrichedDataFrame
        .groupBy("Store", "Dept")
        .agg(sum("Weekly_Sales").alias("Total_Department_Sales"), avg("Weekly_Sales").alias("Average_Department_Sales"))
        .orderBy(desc("Total_Department_Sales"))
        .cache()

    val formattedDepartmentMetrics =
      departmentLevelMetrics
        .withColumn("Total_Department_Sales", format_number(col("Total_Department_Sales"), 2))
        .withColumn("Average_Department_Sales", format_number(col("Average_Department_Sales"), 2))
        .persist(StorageLevel.MEMORY_ONLY)

    val departmentMetricsPath = s"$basePath/aggregated_metrics/department_metrics"
    formattedDepartmentMetrics.write.mode(SaveMode.Overwrite).json(departmentMetricsPath)

    println("Department-Level Metrics:")
    formattedDepartmentMetrics.show(10)

    // Step 11: Calculate weekly trends
    val weeklyTrendWindowSpec = Window.partitionBy("Store", "Dept").orderBy("Date")

    val weeklyTrendsDataFrame =
      enrichedDataFrame
        .withColumn("Previous_Weekly_Sales", lag("Weekly_Sales", 1).over(weeklyTrendWindowSpec))
        .withColumn("Weekly_Trend", col("Weekly_Sales") - col("Previous_Weekly_Sales"))
        .select("Store", "Dept", "Date", "Weekly_Sales", "IsHoliday", "Previous_Weekly_Sales", "Weekly_Trend")

    println("Weekly Trends:")
    weeklyTrendsDataFrame.show(10)

    // Step 12: Calculate holiday sales
    val holidaySales =
      enrichedDataFrame
        .filter("IsHoliday = true")
        .groupBy("Store", "Dept")
        .agg(sum("Weekly_Sales").alias("Holiday_Sales"))
        .persist(StorageLevel.MEMORY_AND_DISK)

    println("Holiday Sales:")
    holidaySales.show(10)

    // Step 13: Calculate non-holiday sales
    val nonHolidaySales =
      enrichedDataFrame
        .filter("IsHoliday = false")
        .groupBy("Store", "Dept")
        .agg(sum("Weekly_Sales").alias("Non_Holiday_Sales"))
        .withColumn("Non_Holiday_Sales", format_number(col("Non_Holiday_Sales"), 2))

    println("Non-Holiday Sales:")
    nonHolidaySales.show(10)

    // Step 14: Compare holiday and non-holiday sales
    val holidayComparison =
      holidaySales
        .join(nonHolidaySales, Seq("Store", "Dept"), "outer")
        .orderBy(desc("Holiday_Sales"))
        .withColumn("Holiday_Sales", format_number(col("Holiday_Sales"), 2))

    val holidayComparisonMetricsPath = s"$basePath/aggregated_metrics/holiday_vs_non_holiday_metrics"
    holidayComparison.write.mode(SaveMode.Overwrite).json(holidayComparisonMetricsPath)

    println("Holiday vs Non-Holiday Sales Comparison:")
    holidayComparison.show(10)
  }
}