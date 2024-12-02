package day18_19

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import scala.util.Random
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object Task5_KafkaProducer {
  def main(args: Array[String]): Unit = {
    val topicName = "orders"

    val producerProps = new Properties()
    producerProps.put("bootstrap.servers", "localhost:9092")
    producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val kafkaProducer = new KafkaProducer[String, String](producerProps)

    val randomGenerator = new Random()

    val userIdList = (1 to 50).map(i => f"user_$i%03d")

    try {
      while (true) {
        val randomOrderId = s"order_${randomGenerator.nextInt(10000)}"
        val randomUserId = userIdList(randomGenerator.nextInt(userIdList.length))
        val randomOrderAmount = (randomGenerator.nextDouble() * 5000).round

        val orderJson = ("orderId" -> randomOrderId) ~
          ("userId" -> randomUserId) ~
          ("orderAmount" -> randomOrderAmount)

        val orderMessage = compact(render(orderJson))

        val kafkaRecord = new ProducerRecord[String, String](topicName, randomUserId, orderMessage)
        kafkaProducer.send(kafkaRecord)

        println(s"Produced order message: $orderMessage")
        Thread.sleep(1500)
      }
    } finally {
      kafkaProducer.close()
    }
  }
}