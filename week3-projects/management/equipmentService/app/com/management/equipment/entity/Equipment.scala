package com.management.equipment.entity

import slick.lifted.ProvenShape
import slick.jdbc.PostgresProfile.api._

case class Equipment(
  id: Int,
  serialNo: String,
  organizationId: Int,
  departmentId: Int,
  equipmentDetail: Int,
  status: String
)

class EquipmentTable(tag: Tag) extends Table[Equipment](tag, "Equipment") {
  def id                = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def serialNo          = column[String]("serialNo")
  def organizationId    = column[Int]("organizationId")
  def departmentId      = column[Int]("departmentId")
  def equipmentDetailId = column[Int]("equipmentDetailId")
  def status            = column[String]("status", O.Default("available")) // available, unavailable

  def organizationFK =
    foreignKey("organization_fk", organizationId, TableQuery[com.management.common.entity.OrganizationTable])(_.id)
  def departmentFK =
    foreignKey("department_fk", departmentId, TableQuery[com.management.common.entity.DepartmentTable])(_.id)
  def equipmentDetailFK =
    foreignKey(
      "equipment_detail_fk",
      equipmentDetailId,
      TableQuery[com.management.equipment.entity.EquipmentDetailTable]
    )(_.id)

  def * =
    (id, serialNo, organizationId, departmentId, equipmentDetailId, status) <>
      ((Equipment.apply _).tupled, Equipment.unapply)
}
