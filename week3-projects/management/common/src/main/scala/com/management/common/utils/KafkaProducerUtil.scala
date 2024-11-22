package com.management.common.utils

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import java.util.Properties
import jakarta.inject.Singleton
import play.api.libs.json.Json

import scala.concurrent.Future

@Singleton
class KafkaProducerUtil {

  val KAFKA_IP = sys.env.getOrElse("KAFKA_IP", "localhost")

  private val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, s"$KAFKA_IP:9092")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  private val producer = new KafkaProducer[String, String](props)

  def sendMessage[T](topic: String, key: String, value: T)(implicit
    writes: play.api.libs.json.Writes[T]
  ): Future[Unit] = {
    val jsonString = Json.toJson(value).toString()
    val record     = new ProducerRecord[String, String](topic, key, jsonString)
    Future.successful(producer.send(record))
  }
  def close(): Unit = producer.close()
}
