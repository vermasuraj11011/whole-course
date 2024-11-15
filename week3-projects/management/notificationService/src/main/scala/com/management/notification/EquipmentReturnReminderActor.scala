package com.management.notification

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.Materializer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class EquipmentReturnReminderActor(kafkaConsumerUtil: KafkaConsumerUtil, scheduler: ActorSystem) extends Actor {
  def receive: Receive = { case "listen" =>
    kafkaConsumerUtil
      .consume[EquipmentReturnReminder]("equipment-returned-reminder")
      .foreach { messages =>
        messages.foreach { message =>
          scheduler
            .scheduler
            .scheduleOnce(0.seconds) {
              println(s"Received equipment return reminder: $message")
              // Add logic to handle the equipment return reminder
            }
        }
      }
  }
}
