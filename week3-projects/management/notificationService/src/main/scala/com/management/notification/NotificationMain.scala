package com.management.notification

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, path, post}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import com.management.notification.JsonFormats._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import spray.json._

import scala.util.{Failure, Success, Try}
class EquipmentReminderActor(schedulerActor: ActorRef) extends Actor {
  override def receive: Receive = {
    case msg: EquipmentReminder =>
      schedulerActor ! msg
    case _ =>
      println("Invalid message")
  }
}

class MeetingReminderActor(schedulerActor: ActorRef) extends Actor {
  override def receive: Receive = {
    case msg: MeetingReminder =>
      schedulerActor ! msg
    case _ =>
      println("Invalid message")
  }
}

class ReminderSchedulerActor extends Actor {
  override def receive: Receive = {
    case reminder: EquipmentReminder =>
      SchedulerUtil.scheduleJob(Some(reminder), None)
    case reminder: MeetingReminder =>
      SchedulerUtil.scheduleJob(None, Some(reminder))
    case _ =>
      println("Invalid message")
  }
}

object NotificationMain {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("notification")

    println("Notification Service is running...")

    val schedulerActor         = system.actorOf(Props(new ReminderSchedulerActor()), "scheduler")
    val equipmentReminderActor = system.actorOf(Props(new EquipmentReminderActor(schedulerActor)), "equipmentReminder")
    val meetingReminderActor   = system.actorOf(Props(new MeetingReminderActor(schedulerActor)), "meetingReminder")

    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers("localhost:9092")
        .withGroupId("unique-group-id")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    def listeners(topic: String, listener: ActorRef): Unit =
      Consumer
        .plainSource(consumerSettings, Subscriptions.topics(topic))
        .map { record =>
          val parsedMessage =
            Try {
              val json = record.value().parseJson.asJsObject
              json.fields.get("messageType") match {
                case Some(JsString("equipment")) =>
                  json.convertTo[EquipmentReminder]
                case Some(JsString("meeting")) =>
                  json.convertTo[MeetingReminder]
                case _ =>
                  throw new Exception("Unknown message type")
              }
            }

          parsedMessage match {
            case Success(value) =>
              value
            case Failure(exception) =>
              println(s"Error processing message: ${exception.getMessage}")
              "Invalid message"
          }
        }
        .runWith(
          Sink.actorRef[Any](
            ref = listener,
            onCompleteMessage = "complete",
            onFailureMessage = (throwable: Throwable) => s"Exception encountered"
          )
        )

    listeners("equipment-returned-reminder", equipmentReminderActor)
    listeners("meeting-reminder", meetingReminderActor)

    println("Listeners are active and consuming messages...")

    val route =
      post {
        path("terminate") {
          system.terminate()
          complete(StatusCodes.OK, "System terminated")
        }
      }

    Http().newServerAt("0.0.0.0", 9004).bind(route)
    println("Server online at http://0.0.0.0:9004/")
  }
}
