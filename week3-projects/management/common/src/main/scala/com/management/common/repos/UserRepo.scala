package com.management.common.repos

import com.management.common.entity.{User, UserTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import jakarta.inject.{Inject, Singleton}
import slick.lifted

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val users = lifted.TableQuery[UserTable]

  def list(): Future[Seq[User]] = db.run(users.result)

  def find(id: Int): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  def add(user: User): Future[Int] = db.run(users returning users.map(_.id) += user)

  def update(user: User): Future[Int] = db.run(users.filter(_.id === user.id).update(user))

  def delete(id: Int): Future[Int] = db.run(users.filter(_.id === id).delete)

  def verifyCreds(email: String, password: String): Future[Option[User]] =
    db.run(users.filter(user => user.email === email && user.password === password).result.headOption)

  def findByIds(ids: List[Int]): Future[Seq[User]] = db.run(users.filter(_.id inSet ids).result)

  def findByToken(token: String): Future[Option[User]] = db.run(users.filter(_.token === token).result.headOption)
}
