package com.management.meeting.entity

import slick.lifted.ProvenShape
import slick.jdbc.PostgresProfile.api._
import com.management.common.entity.UserTable
import com.management.common.entity.DepartmentTable

case class Meeting(
  id: Int,
  title: String,
  description: String,
  roomId: Int,
  startTime: Long,
  endTime: Long,
  location: String,
  organizerId: Int,
  departmentId: Int,
  organizationId: Int,
  status: String,
  isCompleted: Boolean,
  isCancelled: Boolean
)

class MeetingTable(tag: Tag) extends Table[Meeting](tag, "meeting") {
  def id             = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def title          = column[String]("title")
  def description    = column[String]("description")
  def roomId         = column[Int]("room_id")
  def startTime      = column[Long]("start_time")
  def endTime        = column[Long]("end_time")
  def location       = column[String]("location")
  def organizerId    = column[Int]("organizer_id")
  def departmentId   = column[Int]("department_id")
  def organizationId = column[Int]("organization_id")
  def status         = column[String]("status", O.Default("scheduled")) // scheduled, ongoing, completed, cancelled
  def isCompleted    = column[Boolean]("is_completed")
  def isCancelled    = column[Boolean]("is_cancelled")

  def roomFK       = foreignKey("room_fk", roomId, TableQuery[MeetingRoomTable])(_.id)
  def organizerFK  = foreignKey("organizer_fk", organizerId, TableQuery[UserTable])(_.id)
  def departmentFK = foreignKey("department_fk", departmentId, TableQuery[DepartmentTable])(_.id)
  def organizationFK =
    foreignKey("organization_fk", organizationId, TableQuery[com.management.common.entity.OrganizationTable])(_.id)

  def * : ProvenShape[Meeting] =
    (
      id,
      title,
      description,
      roomId,
      startTime,
      endTime,
      location,
      organizerId,
      departmentId,
      organizationId,
      status,
      isCompleted,
      isCancelled
    ) <> (Meeting.tupled, Meeting.unapply)
}
