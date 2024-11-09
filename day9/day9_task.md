## Part1

Create a Microservice that has end point process-Message

create a case Class <br>

- Message (messageType string, message string, messageType string)<br>
- process-Message should receive Message <br>
- There must be Three actors
    - NetworkMessageProcessor that process the message of type NetworkMessage
    - CloudMessageProcessor that process the message of type CloudMessage
    - AppMessageProcessor that process the message of type AppMessage
    - NetworkMessages are written to topic network-message
    - CloudMessages are written to topic cloud-message
    - AppMessages are written to topic app-message

> May be You think about creating an Actor Named MessageHandler to pass the message to the right actor

#### Creating case class for Message

```scala
// Message.scala

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Message(messageType: String, message: String, messageKey: String)

object JsonFormats extends DefaultJsonProtocol {
  implicit val toJsonFmt: RootJsonFormat[Message] = jsonFormat3(Message)
}
```

#### Creating producer factory for Kafka

```scala
// KafkaProducerFactory.scala

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, ProducerConfig}
import java.util.Properties

object KafkaProducerFactory {
  def createProducer(): KafkaProducer[String, String] = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, sys.env.getOrElse("BROKER_HOST", "localhost") + ":9092")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    new KafkaProducer[String, String](props)
  }
}
```

#### Creating actor's

```scala
import akka.actor.{Actor, ActorRef}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import spray.json.enrichAny
import JsonFormats._

// MsgActors.scala

trait MsgActors {
  def actorName: String

  def topic: String

  def getMessage(message: Message) = new ProducerRecord[String, String](topic, message.messageKey, message.toJson.toString())
}

case class ProcessMessage(msg: Message)

class NetworkMessageProcessor(producer: KafkaProducer[String, String]) extends Actor with MsgActors {

  override def actorName: String = "NetworkMessageProcessor"

  override def topic: String = "network-message"

  def receive: Receive = {
    case ProcessMessage(message) =>
      producer.send(getMessage(message))
      println(s"$actorName sent message: $message")
  }
}

class CloudMessageProcessor(producer: KafkaProducer[String, String]) extends Actor with MsgActors {
  override def actorName: String = "CloudMessageProcessor"

  override def topic: String = "cloud-message"

  def receive: Receive = {
    case ProcessMessage(message) =>
      producer.send(getMessage(message))
      println(s"$actorName sent message: $message")
  }
}

class AppMessageProcessor(producer: KafkaProducer[String, String]) extends Actor with MsgActors {
  override def actorName: String = "AppMessageProcessor"

  override def topic: String = "app-message"

  def receive: Receive = {
    case ProcessMessage(message) =>
      producer.send(getMessage(message))
      println(s"$actorName sent message: $message")
  }
}

class MessageHandler(networkProcessor: ActorRef, cloudProcessor: ActorRef, appProcessor: ActorRef) extends Actor {

  def receive: Receive = {
    case msg: Message =>
      msg.messageType match {
        case "NetworkMessage" => networkProcessor ! ProcessMessage(msg)
        case "CloudMessage" => cloudProcessor ! ProcessMessage(msg)
        case "AppMessage" => appProcessor ! ProcessMessage(msg)
      }
  }
}
```

#### Creating microservice

```scala
// MicroService.scala

import JsonFormats._
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

object MicroService {

  implicit val system = ActorSystem("ProcessMessageMicroService")

  val producer = KafkaProducerFactory.createProducer()

  def main(args: Array[String]): Unit = {
    println("Starting MicroService...")

    val networkProcessor = system.actorOf(Props(new NetworkMessageProcessor(producer)), "NetworkMessageProcessor")
    val cloudProcessor = system.actorOf(Props(new CloudMessageProcessor(producer)), "CloudMessageProcessor")
    val appProcessor = system.actorOf(Props(new AppMessageProcessor(producer)), "AppMessageProcessor")

    val messageHandler = system.actorOf(Props(new MessageHandler(networkProcessor, cloudProcessor, appProcessor)), "messageHandler")

    val route =
      post {
        path("process-Message") {
          entity(as[Message]) { message =>
            messageHandler ! message
            complete(StatusCodes.OK, s"Message processed: $message")
          }
        } ~
          path("terminate") {
            system.terminate()
            complete(StatusCodes.OK, "System terminated")
          }
      } ~
        get {
          path("status") {
            complete(StatusCodes.OK, "Service is running")
          }
        }

    Http().newServerAt("0.0.0.0", 8080).bind(route)
    println("Server online at http://0.0.0.0:8080/")
  }
}
```

## Part2:

create another microservice that creates three actors

> CloudListener<br>
> NetworkListener<br>
> AppListener<br>

- Each of them should have an associated consumer correspond to the topic that stores the message
- no sooner any of these actor gets a message  they need to pass the message to the MessageGatherer Actor
- This actor has a kafka producer that stores the received message to a topic name consilated-messages