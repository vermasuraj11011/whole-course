package com.management.common.entity

import slick.lifted.ProvenShape
import slick.jdbc.PostgresProfile.api._

case class User(
  id: Int,
  email: String,
  password: String,
  name: String,
  role: String,
  departmentId: Int,
  organizationId: Int,
  isActive: Boolean,
  token: String
)

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id: Rep[Int]             = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def email: Rep[String]       = column[String]("email")
  def password: Rep[String]    = column[String]("password")
  def name: Rep[String]        = column[String]("name")
  def role: Rep[String]        = column[String]("role", O.Unique)
  def departmentId: Rep[Int]   = column[Int]("department_id")
  def organizationId: Rep[Int] = column[Int]("organization_id")
  def isActive: Rep[Boolean]   = column[Boolean]("is_active")
  def token: Rep[String]       = column[String]("token")
  def * : ProvenShape[User] =
    (id, email, password, name, role, departmentId, organizationId, isActive, token) <>
      ((User.apply _).tupled, User.unapply)
}
