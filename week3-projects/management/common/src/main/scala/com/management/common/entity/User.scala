package com.management.common.entity

import slick.lifted.ProvenShape
import slick.jdbc.PostgresProfile.api._

case class User(
  id: Int,
  email: String,
  password: String,
  name: String,
  role: String,
  departmentId: Option[Int],
  organizationId: Int,
  isActive: Boolean,
  token: String
)

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id             = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def email          = column[String]("email")
  def password       = column[String]("password")
  def name           = column[String]("name")
  def role           = column[String]("role", O.Unique)
  def departmentId   = column[Option[Int]]("department_id", O.Default(None))
  def organizationId = column[Int]("organization_id")
  def isActive       = column[Boolean]("is_active")
  def token          = column[String]("token")
  def * : ProvenShape[User] =
    (id, email, password, name, role, departmentId, organizationId, isActive, token) <>
      ((User.apply _).tupled, User.unapply)

  def departmentFK   = foreignKey("department_fk", departmentId, TableQuery[DepartmentTable])(_.id.?)
  def organizationFK = foreignKey("organization_fk", organizationId, TableQuery[OrganizationTable])(_.id)

}
