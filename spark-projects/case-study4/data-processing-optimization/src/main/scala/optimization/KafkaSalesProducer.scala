package optimization

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import scalapb.GeneratedMessage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

object KafkaSalesProducer {

  implicit val system: ActorSystem = ActorSystem("KafkaProtobufSalesProducer")

  private val bootstrapServers = "localhost:9092"
  private val topic            = "sales-topic"
  private val producerSettings =
    ProducerSettings(system, new StringSerializer, new ByteArraySerializer).withBootstrapServers(bootstrapServers)

  // Step 1: Serialize Protobuf message to byte array
  private def serializeProtobuf[T <: GeneratedMessage](message: T): Array[Byte] = message.toByteArray

  // Step 2: Create a SalesRecord Protobuf message
  private def createSalesRecord(
    store: String,
    department: String,
    date: String,
    weeklySales: Float,
    isHoliday: Boolean
  ): SalesRecord.SalesRecord =
    SalesRecord.SalesRecord(
      store = store,
      department = department,
      date = date,
      weeklySales = weeklySales,
      isHoliday = isHoliday
    )

  // Step 3: Build a Kafka ProducerRecord with serialized SalesRecord
  private def buildRecord(): ProducerRecord[String, Array[Byte]] = {
    val store       = Random.nextInt(10).toString
    val department  = Random.nextInt(20).toString
    val date        = java.time.LocalDate.now().toString
    val weeklySales = 1000 + Random.nextFloat() * 1000
    val isHoliday   = Random.nextBoolean()

    val salesRecord = createSalesRecord(store, department, date, weeklySales, isHoliday)
    println(s"Generated SalesRecord: $salesRecord")

    new ProducerRecord[String, Array[Byte]](topic, salesRecord.store, serializeProtobuf(salesRecord))
  }

  def main(args: Array[String]): Unit = {
    println("Kafka Sales Producer application starting...")

    // Step 4: Create a stream of SalesRecord messages and send to Kafka
    val salesRecordStream = Source.tick(0.seconds, 2.seconds, ()).map(_ => buildRecord())

    salesRecordStream
      .runWith(Producer.plainSink(producerSettings))
      .onComplete { result =>
        println(s"Kafka Producer completed with result: $result")
        system.terminate()
      }
  }
}
