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

class EquipmentDetailTable(tag: Tag) extends Table[EquipmentDetail](tag, "EquipmentDetail") {
  def id                = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name              = column[String]("name")
  def description       = column[String]("description")
  def organizationId    = column[Int]("organizationId")
  def departmentId      = column[Int]("departmentId")
  def isActive          = column[Boolean]("isActive", O.Default(true))
  def totalQuantity     = column[Int]("totalQuantity", O.Default(1))
  def availableQuantity = column[Int]("availableQuantity", O.Default(1))

  def organizationFK =
    foreignKey("organization_fk", organizationId, TableQuery[com.management.common.entity.OrganizationTable])(_.id)
  def departmentFK =
    foreignKey("department_fk", departmentId, TableQuery[com.management.common.entity.DepartmentTable])(_.id)

  def * =
    (id, name, description, organizationId, departmentId, isActive, totalQuantity, availableQuantity) <>
      ((EquipmentDetail.apply _).tupled, EquipmentDetail.unapply)
}
