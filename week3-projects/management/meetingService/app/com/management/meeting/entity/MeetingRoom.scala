package com.management.meeting.entity
import slick.lifted.ProvenShape
import slick.jdbc.PostgresProfile.api._
import com.management.common.entity.OrganizationTable

case class MeetingRoom(
  id: Int,
  roomNo: Int,
  name: String,
  capacity: Int,
  organizationId: Int,
  status: String = "available"
)

class MeetingRoomTable(tag: Tag) extends Table[MeetingRoom](tag, "meeting_room") {
  def id: Rep[Int]             = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def roomNo: Rep[Int]         = column[Int]("room_no")
  def name: Rep[String]        = column[String]("name")
  def capacity: Rep[Int]       = column[Int]("capacity")
  def organizationId: Rep[Int] = column[Int]("organization_id")
  def status: Rep[String]      = column[String]("status", O.Default("available")) // available, unavailable

  def * : ProvenShape[MeetingRoom] =
    (id, roomNo, name, capacity, organizationId, status) <> (MeetingRoom.tupled, MeetingRoom.unapply)

  def organizationIdFk = foreignKey("organization_id_fk", organizationId, TableQuery[OrganizationTable])(_.id)
}
