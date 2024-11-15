package com.management.meeting.views

import com.management.common.entity.User
import com.management.common.views.UserView

case class ParticipantView(
  id: Int,
  meetingId: Int,
  userId: UserView,
  isOrganizer: Boolean,
  isAttending: Boolean,
  isRejected: Boolean,
  creation_remainder: Boolean,
  update_remainder: Boolean
) {
  def toParticipant(user: UserView): ParticipantView =
    ParticipantView(
      id = this.id,
      meetingId = this.meetingId,
      userId = user,
      isOrganizer = this.isOrganizer,
      isAttending = this.isAttending,
      isRejected = this.isRejected,
      creation_remainder = this.creation_remainder,
      update_remainder = this.update_remainder
    )
}

object ParticipantView {
  import play.api.libs.json.{Format, Json}
  implicit val format: Format[ParticipantView] = Json.format[ParticipantView]
}
