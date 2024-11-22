package com.management.equipment.entity

import slick.lifted.ProvenShape
import slick.jdbc.PostgresProfile.api._

case class EquipmentDetail(
  id: Int,
  name: String,
  description: String,
  organizationId: Int,
  departmentId: Int,
  isActive: Boolean,
  totalQuantity: Int,
  availableQuantity: Int
)

class EquipmentDetailTable(tag: Tag) extends Table[EquipmentDetail](tag, "equipment_detail") {
  def id                = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name              = column[String]("name")
  def description       = column[String]("description")
  def organizationId    = column[Int]("organization_id")
  def departmentId      = column[Int]("department_id")
  def isActive          = column[Boolean]("is_active", O.Default(true))
  def totalQuantity     = column[Int]("total_quantity", O.Default(1))
  def availableQuantity = column[Int]("available_quantity", O.Default(1))

  def organizationFK =
    foreignKey("organization_fk", organizationId, TableQuery[com.management.common.entity.OrganizationTable])(_.id)
  def departmentFK =
    foreignKey("department_fk", departmentId, TableQuery[com.management.common.entity.DepartmentTable])(_.id)

  def * =
    (id, name, description, organizationId, departmentId, isActive, totalQuantity, availableQuantity) <>
      ((EquipmentDetail.apply _).tupled, EquipmentDetail.unapply)
}
