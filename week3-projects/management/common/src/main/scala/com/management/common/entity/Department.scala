package com.management.common.entity

import slick.jdbc.PostgresProfile.api._

case class Department(id: Int, name: String, organizationId: Int, isActive: Boolean, head: Int)

class DepartmentTable(tag: Tag) extends Table[Department](tag, "department") {
  def id             = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name           = column[String]("name")
  def organizationId = column[Int]("organization_id")
  def isActive       = column[Boolean]("is_active", O.Default(true))
  def head           = column[Int]("head_id")
  def *              = (id, name, organizationId, isActive, head) <> ((Department.apply _).tupled, Department.unapply)

  def organizationFK     = foreignKey("organization_fk", organizationId, TableQuery[OrganizationTable])(_.id)
  def headOfDepartmentFK = foreignKey("head_fk", head, TableQuery[UserTable])(_.id)
}
