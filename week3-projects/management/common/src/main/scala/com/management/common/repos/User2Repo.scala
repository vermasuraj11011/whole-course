package com.management.common.repos

import com.management.common.entity.{User, UserTable}
import jakarta.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted

import scala.concurrent.{ExecutionContext, Future}
//getAll:List(entity), getById(id):entity, add(entity):entity, update(id):entity, delete(id):id

@Singleton
class User2Repo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val users = lifted.TableQuery[UserTable]

  def getAll: Future[List[User]] = db.run(users.result).map(_.toList)

  def getById(id: Int): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  def add(user: User): Future[User] = db.run(users returning users.map(_.id) += user).map(id => user.copy(id = id))

  def update(user: User): Future[User] = db.run(users.filter(_.id === user.id).update(user)).map(_ => user)

  def delete(id: Int): Future[Int] = db.run(users.filter(_.id === id).delete)

  def verifyCreds(email: String, password: String): Future[Option[User]] =
    db.run(users.filter(user => user.email === email && user.password === password).result.headOption)

  def getByIds(ids: List[Int]): Future[List[User]] = db.run(users.filter(_.id inSet ids).result).map(_.toList)

  def getByToken(token: String): Future[Option[User]] = db.run(users.filter(_.token === token).result.headOption)
}
