package com.management.notification

import com.management.common.utils.DateUtils
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

      println(
        s"""This is the equipment reminder which you have returned at ${DateUtils
            .convertTimestampToHumanReadableDate(equipmentReminder.returnDate)}
           |The equipment details are as follows:
           |  Equipment ID: ${equipmentReminder.equipmentId}
           |  Equipment Name: ${equipmentReminder.equipmentName}
           |  User ID: ${equipmentReminder.userId}
           |  Allocated Date: ${equipmentReminder.allocatedDate}
           |  Email: ${equipmentReminder.email}
           |  Condition: ${equipmentReminder.condition}
           |  
           |  $endStatement
           |""".stripMargin
      )
      println(s"Equipment reminder task is completed")
    } else if (reminder.fields.contains("meetingId")) {
      val meetingReminder = reminder.convertTo[MeetingReminder]
      TeamUpdateUtil.meetingStarted(meetingReminder)
      println(s"Meeting reminder task is completed")
    }
  }
}
