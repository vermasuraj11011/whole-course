package day18_19

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import scala.util.Random

//  Task 3
//  3. Streaming (Kafka Source)
//  Question:
//    Write a Spark Structured Streaming application in Scala that consumes messages from a Kafka topic,
//    processes the messages to extract specific fields, and writes the output to the console.
//
//    Scenario:
//
//    Kafka Topic: transactions
//  Message Format: JSON with fields transactionId, userId, and amount.
//  Required Processing: Extract transactionId and amount fields and calculate the total amount in a 10-second window.
//  Instructions:
//
//    Set up the Kafka source in the Spark application.
//    Perform the necessary transformations on the streaming data.
//  Use windowing to calculate the total amount.
//    Output the results to the console.

object Task3_KafkaProducer{
  def main(args: Array[String]): Unit = {
    val kafkaTopic = "transactions"

    val producerProperties = new Properties()
    producerProperties.put("bootstrap.servers", "localhost:9092")
    producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val kafkaProducer = new KafkaProducer[String, String](producerProperties)

    val randomGenerator = new Random()

    val userList = (1 to 10).map(i => f"user_${i}%02d")

    try {
      while (true) {
        val transactionId = randomGenerator.alphanumeric.take(10).mkString
        val userId = userList(randomGenerator.nextInt(userList.length))
        val transactionAmount = randomGenerator.nextInt(5000) + 100
        val transactionMessage =
          s"""
             |{
             |  "transactionId": "$transactionId",
             |  "userId": "$userId",
             |  "amount": $transactionAmount
             |}
             |""".stripMargin

        val kafkaRecord = new ProducerRecord[String, String](kafkaTopic, userId, transactionMessage)
        kafkaProducer.send(kafkaRecord)

        println(s"Message sent: $transactionMessage")
        Thread.sleep(100)
      }
    } finally {
      kafkaProducer.close()
    }
  }
}