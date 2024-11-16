package com.management.notification

import org.quartz.{JobBuilder, JobDataMap, JobDetail, SchedulerFactory, TriggerBuilder}
import org.quartz.impl.StdSchedulerFactory
import spray.json._
import com.management.notification.JsonFormats._

import java.util.Date

object SchedulerUtil {

  def scheduleJob(equipmentReminder: Option[EquipmentReminder], meetingReminder: Option[MeetingReminder]): Unit = {
    val schedulerFactory: SchedulerFactory = new StdSchedulerFactory()
    val scheduler                          = schedulerFactory.getScheduler
    scheduler.start()

    if (equipmentReminder.isDefined) {
      equipmentReminder.foreach { reminder =>
        println(s"Equipment reminder is scheduled: $equipmentReminder")
        val equipmentJobData = new JobDataMap()
        equipmentJobData.put("reminder", reminder.toJson.compactPrint)

        val equipmentJob: JobDetail =
          JobBuilder
            .newJob(classOf[Scheduler])
            .withIdentity("equipmentJob", "group1")
            .usingJobData(equipmentJobData)
            .build()

        val equipmentTrigger =
          TriggerBuilder
            .newTrigger()
            .withIdentity("equipmentTrigger", "group1")
            .startAt(new Date(reminder.returnDate))
            .build()

        scheduler.scheduleJob(equipmentJob, equipmentTrigger)
      }
    } else {
      meetingReminder.foreach { reminder =>
        println(s"Meeting reminder is scheduled: $meetingReminder")
        val meetingJobData = new JobDataMap()
        meetingJobData.put("reminder", reminder.toJson.compactPrint)

        val meetingJob: JobDetail =
          JobBuilder
            .newJob(classOf[Scheduler])
            .withIdentity("meetingJob", "group1")
            .usingJobData(meetingJobData)
            .build()

        val meetingTrigger =
          TriggerBuilder
            .newTrigger()
            .withIdentity("meetingTrigger", "group1")
            .startAt(new Date(reminder.startTime + 60000))
            .build()

        scheduler.scheduleJob(meetingJob, meetingTrigger)
      }
    }
  }
}
