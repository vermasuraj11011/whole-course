package com.management.notification

import org.quartz.{Job, JobExecutionContext}
import spray.json._
import com.management.notification.JsonFormats._

class Scheduler extends Job {
  override def execute(context: JobExecutionContext): Unit = {
    println("Scheduler is running...")
    val reminderJson = context.getJobDetail.getJobDataMap.getString("reminder")
    val reminder     = reminderJson.parseJson.asJsObject

    if (reminder.fields.contains("equipmentId")) {
      val equipmentReminder = reminder.convertTo[EquipmentReminder]

      val endStatement =
        if (equipmentReminder.returnDate <= System.currentTimeMillis())
          "Thank you for the timely return"
        else
          "Please return the equipment as soon as possible."

      println(s"""This is the equipment reminder which you have returned at ${equipmentReminder.returnDate}
           |The equipment details are as follows:
           |  Equipment ID: ${equipmentReminder.equipmentId}
           |  Equipment Name: ${equipmentReminder.equipmentName}
           |  User ID: ${equipmentReminder.userId}
           |  Allocated Date: ${equipmentReminder.allocatedDate}
           |  Email: ${equipmentReminder.email}
           |  Condition: ${equipmentReminder.condition}
           |  
           |  $endStatement
           |""".stripMargin)
      println(s"Equipment reminder task is completed")
    } else if (reminder.fields.contains("meetingId")) {
      val meetingReminder = reminder.convertTo[MeetingReminder]

      println(s"""Reminder for meeting ${meetingReminder.name} scheduled at ${meetingReminder.startTime}
           |The meeting details are as follows:
           |  Meeting ID: ${meetingReminder.meetingId}
           |  Meeting Name: ${meetingReminder.name}
           |  User ID: ${meetingReminder.userId}
           |  Email: ${meetingReminder.email}
           |  
           |  Please join the meeting on time.
           |  Thank you.
           |""".stripMargin)
      println(s"Meeting reminder task is completed")
    }
  }
}
