package com.management.common.entity

import slick.jdbc.PostgresProfile.api._

case class Organization(
  id: Int,
  name: String,
  head_id: Option[Int],
  address: String,
  phone: String,
  website: String,
  isActive: Boolean,
  isMeetingServiceEnabled: Boolean,
  isEquipmentServiceEnabled: Boolean
)

class OrganizationTable(tag: Tag) extends Table[Organization](tag, "organization") {
  def id                        = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name                      = column[String]("name")
  def head_id                   = column[Option[Int]]("head_id", O.Default(None))
  def address                   = column[String]("address")
  def phone                     = column[String]("phone")
  def website                   = column[String]("website")
  def isActive                  = column[Boolean]("is_active")
  def isMeetingServiceEnabled   = column[Boolean]("is_meeting_service_enabled")
  def isEquipmentServiceEnabled = column[Boolean]("is_equipment_service_enabled")

  def * =
    (id, name, head_id, address, phone, website, isActive, isMeetingServiceEnabled, isEquipmentServiceEnabled) <>
      ((Organization.apply _).tupled, Organization.unapply)

  def head = foreignKey("head_fk", head_id, TableQuery[UserTable])(_.id.?)
}
