package com.management.equipment.entity

import slick.lifted.ProvenShape
import slick.jdbc.PostgresProfile.api._

case class EquipmentAllocation(
  id: Int,
  equipmentDetailId: Int,
  organizationId: Int,
  departmentId: Int,
  userId: Int,
  allocatedQuantity: Int,
  allocatedDate: Long,
  isActive: Boolean,
  deallocatedDate: Option[Long],
  purpose: String,
  returnCondition: Option[String]
)

class EquipmentAllocationTable(tag: Tag) extends Table[EquipmentAllocation](tag, "EquipmentAllocation") {
  def id                = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def equipmentDetailId = column[Int]("equipmentDetailId")
  def organizationId    = column[Int]("organizationId")
  def departmentId      = column[Int]("departmentId")
  def userId            = column[Int]("userId")
  def allocatedQuantity = column[Int]("allocatedQuantity")
  def allocatedDate     = column[Long]("allocatedDate")
  def isActive          = column[Boolean]("isActive")
  def deallocatedDate   = column[Option[Long]]("deallocatedDate")
  def purpose           = column[String]("purpose")
  def returnCondition   = column[Option[String]]("returnCondition")

  def equipmentFK = foreignKey("equipment_fk", equipmentDetailId, TableQuery[EquipmentDetailTable])(_.id)
  def organizationFK =
    foreignKey("organization_fk", organizationId, TableQuery[com.management.common.entity.OrganizationTable])(_.id)
  def departmentFK =
    foreignKey("department_fk", departmentId, TableQuery[com.management.common.entity.DepartmentTable])(_.id)
  def userFK = foreignKey("user_fk", userId, TableQuery[com.management.common.entity.UserTable])(_.id)

  def * =
    (
      id,
      equipmentDetailId,
      organizationId,
      departmentId,
      userId,
      allocatedQuantity,
      allocatedDate,
      isActive,
      deallocatedDate,
      purpose,
      returnCondition
    ) <> ((EquipmentAllocation.apply _).tupled, EquipmentAllocation.unapply)
}
