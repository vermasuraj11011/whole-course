package com.management.meeting.views

import com.management.meeting.entity.MeetingRoom

import scala.language.implicitConversions

case class MeetingRoomDetailView(id: Int, roomNo: Int, name: String, capacity: Int, organizationId: Int, status: String)

object MeetingRoomDetailView {
  import play.api.libs.json.{Format, Json}
  implicit val format: Format[MeetingRoomDetailView] = Json.format[MeetingRoomDetailView]

  implicit def fromMeetingRoom(meetingRoom: MeetingRoom): MeetingRoomDetailView =
    MeetingRoomDetailView(
      meetingRoom.id,
      meetingRoom.roomNo,
      meetingRoom.name,
      meetingRoom.capacity,
      meetingRoom.organizationId,
      meetingRoom.status
    )
}
