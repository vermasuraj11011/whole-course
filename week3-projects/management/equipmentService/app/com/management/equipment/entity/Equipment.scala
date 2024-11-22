package com.management.equipment.entity

import slick.lifted.ProvenShape
import slick.jdbc.PostgresProfile.api._

case class Equipment(
  id: Int,
  serialNo: String,
  organizationId: Int,
  departmentId: Int,
  equipmentDetail: Int,
  equipmentAllocationId: Int = 0,
  status: String
)

class EquipmentTable(tag: Tag) extends Table[Equipment](tag, "equipment") {
  def id                    = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def serialNo              = column[String]("serial_no")
  def organizationId        = column[Int]("organization_id")
  def departmentId          = column[Int]("department_id")
  def equipmentDetailId     = column[Int]("equipment_detail_id")
  def equipmentAllocationId = column[Int]("equipment_allocation_id", O.Default(0))
  def status                = column[String]("status", O.Default("available")) // available, unavailable

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
    (id, serialNo, organizationId, departmentId, equipmentDetailId, equipmentAllocationId, status) <>
      ((Equipment.apply _).tupled, Equipment.unapply)
}
