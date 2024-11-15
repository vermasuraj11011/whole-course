package com.management.notification

import org.apache.kafka.clients.consumer.KafkaConsumer
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

class KafkaConsumerUtil(implicit ec: ExecutionContext) {
  def consume[T](topic: String): Future[List[T]] = {
    val props = new java.util.Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("group.id", "reminder-consumer-group")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    val consumer = new KafkaConsumer[String, T](props)
    consumer.subscribe(List(topic).asJava)

    Future {
      val records = consumer.poll(java.time.Duration.ofSeconds(5)).asScala.toList
      records.map(_.value())
    }
  }
}
