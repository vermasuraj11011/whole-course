package com.management.notification

import com.management.common.utils.DateUtils

object TeamUpdateUtil {

  def meetingScheduled(meetingReminder: MeetingReminder): Unit =
    println(
      s"""Reminder for meeting ${meetingReminder.name} scheduled at ${DateUtils
          .convertTimestampToHumanReadableDate(meetingReminder.startTime)}
         |The meeting details are as follows:
         |  Meeting ID: ${meetingReminder.meetingId}
         |  Meeting Name: ${meetingReminder.name}
         |  Email: ${meetingReminder.email}
         |  
         |  Please join the meeting on time.
         |  Thank you.
         |""".stripMargin
    )

  def meetingStarted(meetingReminder: MeetingReminder): Unit =
    println(s"""Meeting ${meetingReminder.name} has started
         |The meeting details are as follows:
         |  Meeting ID: ${meetingReminder.meetingId}
         |  Meeting Name: ${meetingReminder.name}
         |  Email: ${meetingReminder.email}
         |  
         |  Please join the meeting.
         |  Thank you.
         |""".stripMargin)
}
