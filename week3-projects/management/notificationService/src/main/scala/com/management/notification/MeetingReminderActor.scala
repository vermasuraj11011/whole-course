package com.management.notification

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.Materializer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class MeetingReminderActor(kafkaConsumerUtil: KafkaConsumerUtil, scheduler: ActorSystem) extends Actor {
  def receive: Receive = { case "listen" =>
    kafkaConsumerUtil
      .consume[MeetingReminder]("meeting-reminder")
      .foreach { messages =>
        messages.foreach { message =>
          scheduler
            .scheduler
            .scheduleOnce(0.seconds) {
              println(s"Received meeting reminder: $message")
              // Add logic to handle the meeting reminder
            }
        }
      }
  }
}
