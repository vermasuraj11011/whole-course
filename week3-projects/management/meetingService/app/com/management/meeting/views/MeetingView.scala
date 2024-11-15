package com.management.meeting.views

import com.management.common.views.UserView

case class MeetingView(
  id: Int,
  title: String,
  description: String,
  room: MeetingRoomDetailView,
  startTime: Long,
  endTime: Long,
  location: String,
  organizer: UserView,
  participants: List[ParticipantView]
)

object MeetingView {
  import play.api.libs.json.{Format, Json}
  import com.management.meeting.views.ParticipantView._
  implicit val format: Format[MeetingView] = Json.format[MeetingView]
}
