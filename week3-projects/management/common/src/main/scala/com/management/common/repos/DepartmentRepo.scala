package com.management.common.repos

import com.management.common.entity.{Department, DepartmentTable}
import jakarta.inject.Singleton
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DepartmentRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val Departments = TableQuery[DepartmentTable]

  def list(): Future[Seq[Department]] = db.run(Departments.result)

  def find(id: Int): Future[Option[Department]] =
    db.run(Departments.filter(_.id === id).result.headOption.map(_.headOption))

  def add(Department: Department): Future[Int] = db.run(Departments returning Departments.map(_.id) += Department)

  def update(Department: Department): Future[Int] =
    db.run(Departments.filter(_.id === Department.id).update(Department))

  def delete(id: Int): Future[Int] = db.run(Departments.filter(_.id === id).delete)
}
