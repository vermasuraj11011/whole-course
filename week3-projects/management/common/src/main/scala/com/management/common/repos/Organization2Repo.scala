package com.management.common.repos

import com.management.common.entity.{Organization, OrganizationTable}
import jakarta.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}



@Singleton
class Organization2Repo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val organizations = TableQuery[OrganizationTable]

  def getAll: Future[List[Organization]] = db.run(organizations.result).map(_.toList)

  def find(id: Int): Future[Option[Organization]] = db.run(organizations.filter(_.id === id).result.headOption)

  def add(organization: Organization): Future[Organization] =
    db.run(organizations returning organizations.map(_.id) += organization).map(id => organization.copy(id = id))

  def update(organization: Organization): Future[Organization] =
    db.run(organizations.filter(_.id === organization.id).update(organization)).map(_ => organization)

  def delete(id: Int): Future[Int] = db.run(organizations.filter(_.id === id).delete)

}
