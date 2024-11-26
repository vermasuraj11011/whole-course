import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

import java.io.{FileWriter, PrintWriter}
import java.time.LocalTime
import scala.io.Source
import scala.util.Random

object Spark_day15 {

  val logger = LoggerFactory.getLogger("Spark_day15")

//  create word between 3 to 7 characters
  def createRandomWord: String = {
    val random = new scala.util.Random
    val length = 3 + random.nextInt(5)
    random.alphanumeric.take(length).mkString
  }

  def readFromFile(path: String): Seq[String] = {
    val source = Source.fromFile(path)
    try source.getLines().toSeq
    finally source.close()
  }

  def writeToFile(path: String, data: String): Unit = {
//    val writer = new PrintWriter(path)
    val writer = new PrintWriter(new FileWriter(path, true))
    try writer.write(data)
    finally writer.close()
  }

  def writeTwoMillionsRandomWord(path: String): Unit = {
    val writeHundredLinesAtOnce = 1000
    val numberOfWrites          = 2000
    var index                   = 0

    Range(0, numberOfWrites).foreach { i =>
      val oneTimeWrite: String =
        Range(0, writeHundredLinesAtOnce)
          .map { _ =>
            index += 1
            index + " " + createRandomWord
          }
          .mkString("\n")

      writeToFile(path, oneTimeWrite.concat("\n"))
    }
  }

  def createWord(): Unit = {
    val path = "/Users/surajverma/Documents/course/whole-course/day11/spark_project/src/main/scala/data.txt"
//    val path = "/Users/surajverma/Documents/course/whole-course/day11/spark_project/src/main/scala/large_data.txt"
    //    adding words to the file
    println(s"Start ${LocalTime.now()}")
    writeTwoMillionsRandomWord(path)
    println(s"End   ${LocalTime.now()}")
  }

  //  Exercise 1: Understanding RDD and Partitioning
  //    Objective: Create and manipulate an RDD while understanding its partitions.
  //    Task:
  //
  //    Load a large text file (or create one programmatically with millions of random numbers).
  //    Perform the following:
  //    Check the number of partitions for the RDD.
  //    Repartition the RDD into 4 partitions and analyze how the data is distributed.
  //    Coalesce the RDD back into 2 partitions.
  //    Print the first 5 elements from each partition.
  //    Expected Analysis:

  def task1(sc: SparkContext, path: String): Unit = {
    val rdd = sc.textFile(path)
    println(s"Number of partitions: ${rdd.getNumPartitions}")
    val rdd1 =
      rdd.mapPartitionsWithIndex { (index, iterator) =>
        iterator.map((index, _)).take(5)
      //        iterator.map((index, _))
      }

    val repartitionedRdd = rdd.repartition(4)
    println(s"Number of partitions after repartitioning : ${rdd.getNumPartitions}")

    val rdd2 =
      repartitionedRdd.mapPartitionsWithIndex { (index, iterator) =>
        iterator.map((index, _)).take(5)
      //        iterator.map((index, _))
      }

    val coalescedRdd = repartitionedRdd.coalesce(2)
    println(s"Number of partitions after coalescing : ${coalescedRdd.getNumPartitions}")

    val rdd3 =
      coalescedRdd.mapPartitionsWithIndex { (index, iterator) =>
        iterator.map((index, _)).take(5)
      //        iterator.map((index, _))
      }

    rdd1
      .collect()
      .foreach { case (index, data) =>
        println(s"Partition: $index, Data: $data")
      }

    println()

    rdd2
      .collect()
      .foreach { case (index, data) =>
        println(s"Partition: $index, Data: $data")
      }

    println()

    rdd3
      .collect()
      .foreach { case (index, data) =>
        println(s"Partition: $index, Data: $data")
      }
  }

//  Exercise 2: Narrow vs Wide Transformations
//    Objective: Differentiate between narrow and wide transformations in Spark.
//    Task:
//
//    Create an RDD of numbers from 1 to 1000.
//    Apply narrow transformations: map, filter.
//    Apply a wide transformation: groupByKey or reduceByKey (simulate by mapping numbers into key-value pairs, e.g., (number % 10, number)).
//    Save the results to a text file.
//    Expected Analysis:
//
//    Identify how narrow transformations execute within a single partition, while wide transformations cause shuffles.
//    Observe the DAG in the Spark UI, focusing on stages and shuffle operations.

  def task2(sc: SparkContext): Unit = {
    val numbersRDD       = sc.parallelize(1 to 1000)
    val filteredRDD      = numbersRDD.filter(_ % 2 == 0) // narrow transformation
    val keyValueRDD      = filteredRDD.map(num => (num % 10, num))
    val groupedRDD       = keyValueRDD.groupByKey()      // wide transformation
    val addingOneToValue = groupedRDD.mapValues(_.map(_ + 1))
    addingOneToValue
      .saveAsTextFile("/Users/surajverma/Documents/course/whole-course/day11/spark_project/src/main/scala/output")
  }

//    Exercise 3: Analyzing Tasks and Executors
//    Objective: Understand how tasks are distributed across executors in local mode.
//    Task:
//
//    Create an RDD of strings with at least 1 million lines (e.g., lorem ipsum or repetitive text).
//    Perform a transformation pipeline:
//    Split each string into words.
//    Map each word to (word, 1).
//    Reduce by key to count word occurrences.
//    Set spark.executor.instances to 2 and observe task distribution in the Spark UI.
//    Expected Analysis:
//
//    Compare task execution times across partitions and stages in the UI.
//    Understand executor and task allocation for a local mode Spark job.

  def task3(sc: SparkContext): Unit = {

    val textRDD       = sc.parallelize(Seq.fill(10000)("lorem ipsum dolor sit amet"), numSlices = 4)
    val wordsRDD      = textRDD.flatMap(line => line.split(" "))
    val wordPairsRDD  = wordsRDD.map(word => (word, 1))
    val wordCountsRDD = wordPairsRDD.reduceByKey(_ + _)

    wordCountsRDD
      .saveAsTextFile("/Users/surajverma/Documents/course/whole-course/day11/spark_project/src/main/scala/output/task3")
  }

//  Exercise 4: Exploring DAG and Spark UI
//  Objective: Analyze the DAG and understand the stages involved in a complex Spark job.
//  Task:
//
//  Create an RDD of integers from 1 to 10,000.
//  Perform a series of transformations:
//    filter: Keep only even numbers.
//  map: Multiply each number by 10.
//  flatMap: Generate tuples (x, x+1) for each number.
//  reduceByKey: Reduce by summing keys.
//  Perform an action: Collect the results.
//  Expected Analysis:
//
//  Analyze the DAG generated for the job and how Spark breaks it into stages.
//    Compare execution times of stages and tasks in the Spark UI.

  def task4(sc: SparkContext): Unit = {
    val numbersRDD     = sc.parallelize(1 to 10000)
    val evenNumbersRDD = numbersRDD.filter(_ % 2 == 0)
    val multipliedRDD  = evenNumbersRDD.map(_ * 10)
    val tuplesRDD      = multipliedRDD.flatMap(x => Seq((x, 1), (x + 1, 1)))
    val reducedRDD     = tuplesRDD.reduceByKey(_ + _)
    val results        = reducedRDD.collect()
    results.take(10).foreach(println)
  }

//  Exercise 5: Partitioning Impact on Performance
//    Objective: Understand the impact of partitioning on performance and data shuffling.
//  Task:
//
//    Load a large dataset (e.g., a CSV or JSON file) into an RDD.
//  Partition the RDD into 2, 4, and 8 partitions separately and perform the following tasks:
//    Count the number of rows in the RDD.
//  Sort the data using a wide transformation.
//    Write the output back to disk.
//  Compare execution times for different partition sizes.
//    Expected Analysis:
//
//    Observe how partition sizes affect shuffle size and task distribution in the Spark UI.
//  Understand the trade-off between too many and too few partitions.

  def task5(sc: SparkContext) = {
//    val outputPath = "/Users/surajverma/Documents/datasets/large_dataset.csv"
//    val writer     = new PrintWriter(outputPath)
//
//    for (i <- 1 to 1000000)
//      writer.write(s"${Random.nextInt(100000)},${Random.nextDouble()}\n")
//    writer.close()

    val datasetRDD = sc.textFile("/Users/surajverma/Documents/datasets/large_dataset.csv")

    def processWithPartitions(partitions: Int): Unit = {
      println(s"Processing with $partitions partitions...")

      val partitionedRDD = datasetRDD.repartition(partitions)

      val rowCount = partitionedRDD.count()
      println(s"Row Count: $rowCount")

      val sortedRDD =
        partitionedRDD.map(line => line.split(",")).map(cols => (cols(0).toInt, cols(1).toDouble)).sortByKey()

      sortedRDD.saveAsTextFile(s"output_partitions_$partitions")
    }

    processWithPartitions(2)
    processWithPartitions(4)
    processWithPartitions(8)
  }

  def startHistoryServer(logDir: String): Unit = {
    val historyServerCommand =
      Seq(
        "java",
        "-cp",
        System.getProperty("java.class.path"),
        "org.apache.spark.deploy.history.HistoryServer",
        "--properties-file"
      )
    val processBuilder = new ProcessBuilder(historyServerCommand: _*)
    processBuilder.environment().put("SPARK_HISTORY_OPTS", s"-Dspark.history.fs.logDirectory=$logDir")
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()

    val reader       = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream))
    var line: String = null
    while ({
      line = reader.readLine();
      line != null
    })
      println(line)
  }

  def main(args: Array[String]): Unit = {

    val path   = "/Users/surajverma/Documents/course/whole-course/day11/spark_project/src/main/scala/large_data.txt"
    val path1  = "/Users/surajverma/Documents/course/whole-course/day11/spark_project/src/main/scala/data.txt"
    val logDir = "/Users/surajverma/Documents/course/whole-course/day11/spark_project/event_log"
    val spark =
      SparkSession
        .builder()
        .appName("Day 15 task")
        .master("local[16]")
        .config("spark.eventLog.enabled", "true")
        .config("spark.eventLog.dir", logDir)
//        .config("spark.executor.cores", "1") // Set the number of cores per executor
//        .config("spark.cores.max", "4")      // Set the maximum number of cores to use
//        .config("spark.history.fs.logDirectory", logDir)
        .getOrCreate()
    val sc = spark.sparkContext

//     Start the history server
//    println("Starting Spark History Server...")
//    startHistoryServer(logDir)

//    writeTwoMillionsRandomWord(path1)

//    task 1
    task1(sc, path)

//    task 2
    task2(sc)

//    task 3
    task3(sc)

//    task 4
    task4(sc)

//    task 5
    task5(sc)

//    Thread.sleep(300000)

    // take input to quit
    println("Enter any key to quit")
    scala.io.StdIn.readLine()

    spark.stop()
  }
}
