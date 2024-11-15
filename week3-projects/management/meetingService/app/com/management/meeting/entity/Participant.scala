package com.management.meeting.entity
import slick.lifted.ProvenShape
import slick.jdbc.PostgresProfile.api._
import com.management.common.entity.UserTable

case class Participant(
  id: Int,
  meetingId: Int,
  userId: Int,
  isOrganizer: Boolean,
  isAttending: Boolean,
  isRejected: Boolean,
  creation_remainder: Boolean,
  update_remainder: Boolean
)

class ParticipantTable(tag: Tag) extends Table[Participant](tag, "participant") {
  def id: Rep[Int]                    = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def meetingId: Rep[Int]             = column[Int]("meeting_id")
  def userId: Rep[Int]                = column[Int]("user_id")
  def isOrganizer: Rep[Boolean]       = column[Boolean]("is_organizer", O.Default(false))
  def isAttending: Rep[Boolean]       = column[Boolean]("is_attending", O.Default(false))
  def isRejected: Rep[Boolean]        = column[Boolean]("is_rejected", O.Default(false))
  def creationRemainder: Rep[Boolean] = column[Boolean]("creation_remainder", O.Default(false))
  def updateRemainder: Rep[Boolean]   = column[Boolean]("update_remainder", O.Default(false))

  def meetingFK = foreignKey("meeting_fk", meetingId, TableQuery[MeetingTable])(_.id)
  def userFK    = foreignKey("user_fk", userId, TableQuery[UserTable])(_.id)

  def * : ProvenShape[Participant] =
    (id, meetingId, userId, isOrganizer, isAttending, isRejected, creationRemainder, updateRemainder) <>
      (Participant.tupled, Participant.unapply)
}
