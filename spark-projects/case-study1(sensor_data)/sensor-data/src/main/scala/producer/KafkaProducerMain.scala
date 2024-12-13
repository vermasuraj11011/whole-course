package producer

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import proto.SensorReadingProto.SensorReadingProto

import scala.concurrent.duration._

object KafkaProducerMain {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem        = ActorSystem("SensorDataProducerSystem")
    implicit val materializer: Materializer = Materializer(system)
    import system.dispatcher

    // Kafka producer settings
    val producerSettings =
      ProducerSettings(system, new StringSerializer, new ByteArraySerializer).withBootstrapServers("localhost:9092")

    // Generate a stream of sensor readings-
    val sensorReadingSource =
      Source
        .tick(0.milliseconds, 1000.milliseconds, ())
        .map(_ => TemperatureSensorEntity.generateTempSensorReading)
        .map { sensorReading =>
          println(s"Generated SensorReading: $sensorReading")

          // Converting the sensor reading to Protobuf
          val proto =
            SensorReadingProto(
              sensorId = sensorReading.sensorId,
              timestamp = sensorReading.timestamp,
              temperature = sensorReading.temperature,
              humidity = sensorReading.humidity
            )
          new ProducerRecord[String, Array[Byte]]("sensor-readings", sensorReading.sensorId, proto.toByteArray)
        }

    // Publishing sensor readings to Kafka
    sensorReadingSource
      .runWith(Producer.plainSink(producerSettings))
      .onComplete {
        case scala.util.Success(_) =>
          println("Stream completed successfully.")
          system.terminate()
        case scala.util.Failure(exception) =>
          println(s"Stream failed with exception: $exception")
          system.terminate()
      }
  }
}
