package com.management.common.repos

import com.management.common.entity.{Organization, OrganizationTable}
import jakarta.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrganizationRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val organizations = TableQuery[OrganizationTable]

  def list(): Future[Seq[Organization]] = db.run(organizations.result)

  def find(id: Int): Future[Option[Organization]] =
    db.run(organizations.filter(_.id === id).result.headOption.map(_.headOption))

  def add(organization: Organization): Future[Int] =
    db.run(organizations returning organizations.map(_.id) += organization)

  def update(organization: Organization): Future[Int] =
    db.run(organizations.filter(_.id === organization.id).update(organization))

  def delete(id: Int): Future[Int] = db.run(organizations.filter(_.id === id).delete) // todo fix this
}
