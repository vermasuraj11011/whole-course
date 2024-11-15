package com.management.notification

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.Materializer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class MeetingReminderActor1(kafkaConsumerUtil: KafkaConsumerUtil) extends Actor {
  def receive: Receive = { case "listen" =>
    kafkaConsumerUtil
      .consume[MeetingReminder]("meeting-reminder")
      .foreach { message =>
        println(s"Received meeting reminder: $message")
      }
  }
}

class EquipmentReturnReminderActor1(kafkaConsumerUtil: KafkaConsumerUtil) extends Actor {
  def receive: Receive = { case "listen" =>
    kafkaConsumerUtil
      .consume[EquipmentReturnReminder]("equipment-returned-reminder")
      .foreach { message =>
        println(s"Received equipment return reminder: $message")
      }
  }
}

object StartNotificationService extends App {
  implicit val system: ActorSystem        = ActorSystem("ReminderSystem")
  implicit val materializer: Materializer = Materializer(system)

  val kafkaConsumerUtil = new KafkaConsumerUtil()

  val meetingReminderActor = system.actorOf(Props(new MeetingReminderActor1(kafkaConsumerUtil)), "MeetingReminderActor")
  val equipmentReminderActor =
    system.actorOf(Props(new EquipmentReturnReminderActor1(kafkaConsumerUtil)), "EquipmentReminderActor")

  system
    .scheduler
    .scheduleAtFixedRate(initialDelay = 0.seconds, interval = 1.hour) {
      new Runnable {
        def run(): Unit = println("Scheduled task triggered to send reminders")

      }
    }

  meetingReminderActor ! "listen"
  equipmentReminderActor ! "listen"
}
