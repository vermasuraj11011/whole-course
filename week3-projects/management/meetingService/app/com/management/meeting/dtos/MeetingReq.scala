package com.management.meeting.dtos

import com.management.meeting.entity.Meeting
import play.api.libs.json.{Format, Json}

case class MeetingReq(
  title: String,
  description: String,
  roomId: Int,
  startTime: Long,
  endTime: Long,
  location: String,
  participants: List[Int]
) {
  def toMeeting(organizerId: Int, departmentId: Int, organizationId: Int): Meeting =
    Meeting(
      id = 0,
      title = this.title,
      description = this.description,
      roomId = this.roomId,
      startTime = this.startTime,
      endTime = this.endTime,
      location = this.location,
      organizerId = organizerId,
      departmentId = departmentId,
      organizationId = organizationId,
      status = "scheduled",
      isCompleted = false,
      isCancelled = false
    )
}

object MeetingReq {
  implicit val personFormat: Format[MeetingReq] = Json.format[MeetingReq]

}
