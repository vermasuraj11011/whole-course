package com.management.common.repos

import com.management.common.entity.{Department, DepartmentTable}
import jakarta.inject.Singleton
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import jakarta.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Department2Repo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val Departments = TableQuery[DepartmentTable]

  def getAll: Future[List[Department]] = db.run(Departments.result).map(_.toList)

  def getById(id: Int): Future[Option[Department]] = db.run(Departments.filter(_.id === id).result.headOption)

  def add(Department: Department): Future[Department] =
    db.run(Departments returning Departments.map(_.id) += Department).map(id => Department.copy(id = id))

  def update(Department: Department): Future[Department] =
    db.run(Departments.filter(_.id === Department.id).update(Department)).map(_ => Department)

  def delete(id: Int): Future[Int] = db.run(Departments.filter(_.id === id).delete)
}
