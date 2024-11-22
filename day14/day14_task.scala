import org.apache.spark.SparkContext.jarOfObject
import org.apache.spark.sql.SparkSession

object Main {

  // task 1
  private def word_count(spark: SparkSession): Int = {
    val lines   = Seq("this is task", "need to calculate the number of words in the collection", "and the ans is 15")
    val lineRdd = spark.sparkContext.parallelize(lines)
    lineRdd.flatMap(line => line.split(" ")).count().toInt
  }

  // task 2 (cartesian product)
  private def find_cartesian_product_of_two_seq(spark: SparkSession): Seq[(Int, Int)] = {
    val seq1 = Seq(1, 2, 3)
    val seq2 = Seq(4, 5, 6)
    val rdd1 = spark.sparkContext.parallelize(seq1)
    val rdd2 = spark.sparkContext.parallelize(seq2)
    rdd1.cartesian(rdd2).collect()
  }

  //  task 3 (filer out even numbers)
  private def filter_out_even_numbers(spark: SparkSession): Seq[Int] = {
    val seq = Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val rdd = spark.sparkContext.parallelize(seq)
    rdd.filter(num => num % 2 == 0).collect()
  }

  //  task 4 (find the frequency of each character in a given seq of string)
  private def find_frequency_of_each_character(spark: SparkSession): Map[Char, Int] = {
    val seq = Seq("hello", "world", "this is a task 4")
    val rdd = spark.sparkContext.parallelize(seq)
    rdd.flatMap(word => word.toCharArray).map(char => (char, 1)).reduceByKey(_ + _).collect().toMap
  }

  // task 5 (calculate the average score of all the records)
  private def calculate_average_score(spark: SparkSession): Double = {
    val scores    = Seq((1, 66), (2, 73), (3, 99), (4, 33))
    val scoresRdd = spark.sparkContext.parallelize(scores)
    val total     = scoresRdd.map(_._2).fold(0)(_ + _)
    total.toDouble / scores.size
  }

  //  task 6 (join two rdds on the id)
  private def join_two_rdds(spark: SparkSession): Seq[(Int, String, Int)] = {
    val rdd1_id_name  = spark.sparkContext.parallelize(Seq((1, "a"), (2, "b"), (3, "c")))
    val rdd2_id_score = spark.sparkContext.parallelize(Seq((1, 70), (2, 45), (3, 90)))
    rdd1_id_name
      .join(rdd2_id_score)
      .map { case (id, (name, score)) =>
        (id, name, score)
      }
      .collect()
  }

  //  task 7 (union operation and remove duplicates)
  private def union_two_rdds_unique(spark: SparkSession): Seq[Int] = {
    val rdd1 = spark.sparkContext.parallelize(Seq(1, 2, 3, 4, 5))
    val rdd2 = spark.sparkContext.parallelize(Seq(4, 5, 6, 7, 8))
    rdd1.union(rdd2).distinct().collect()
  }

  //  task 8 (filter out age less than 18 from the csv string seq)
  private def filter_out_age_less_than_18(spark: SparkSession): Seq[String] = {
    val seq = Seq("1,suraj,20", "2,shyam,15", "3,ram,25", "4,mohan,17")
    val rdd = spark.sparkContext.parallelize(seq)
    rdd.filter(line => line.split(",")(2).toInt < 18).collect()
  }

  //  task 9 ( calculate the sum of integer of 1 to 100 using rdds)
  private def calculate_sum_of_integers(spark: SparkSession): Int = {
    val rdd = spark.sparkContext.parallelize(1 to 100)
    rdd.reduce(_ + _)
  }

  //  task 10 (calculate the group sum of key pair)
  private def calculate_group_sum_of_key_pair(spark: SparkSession): Map[Int, Int] = {
    val seq = Seq((1, 10), (2, 20), (1, 30), (2, 40), (1, 50))
    val rdd = spark.sparkContext.parallelize(seq)
    rdd
      .groupByKey()
      .map { case (key, values) =>
        (key, values.sum)
      }
      .collect()
      .toMap
  }

  def main(args: Array[String]): Unit = {
    println("staring spark application ...")

    val spark = SparkSession.builder().appName("Spark Project").master("local[*]").getOrCreate()

    println(s"output of the task 1 (word count) is ${word_count(spark)}")
    println(s"output of the task 2 (cartesian product) is ${find_cartesian_product_of_two_seq(spark).mkString(", ")}")
    println(s"output of the task 3 (filter out even numbers) is ${filter_out_even_numbers(spark).mkString(", ")}")
    println(s"output of the task 4 (find frequency of each character) is ${find_frequency_of_each_character(spark)}")
    println(s"output of the task 5 (calculate average score) is ${calculate_average_score(spark)}")
    println(s"output of the task 6 (join two rdds) is ${join_two_rdds(spark).mkString(", ")}")
    println(
      s"output of the task 7 (union two rdds and remove duplicates) is ${union_two_rdds_unique(spark).mkString(", ")}"
    )
    println(
      s"output of the task 8 (filter out age less than 18) is ${filter_out_age_less_than_18(spark).mkString(", ")}"
    )
    println(s"output of the task 9 (calculate sum of integers) is ${calculate_sum_of_integers(spark)}")
    println(s"output of the task 10 (calculate group sum of key pair) is ${calculate_group_sum_of_key_pair(spark)}")
    spark.stop()
  }
}
