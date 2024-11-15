package com.management.meeting.repos

import com.management.meeting.entity.{Meeting, MeetingTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import jakarta.inject.{Inject, Singleton}
import slick.lifted

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MeetingRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val meetings = lifted.TableQuery[MeetingTable]

  def list(): Future[Seq[Meeting]] = db.run(meetings.result)

  def find(id: Int): Future[Option[Meeting]] = db.run(meetings.filter(_.id === id).result.headOption)

  def add(meeting: Meeting): Future[Int] = db.run(meetings returning meetings.map(_.id) += meeting)

  def update(meeting: Meeting): Future[Int] = db.run(meetings.filter(_.id === meeting.id).update(meeting))

  def delete(id: Int): Future[Int] = db.run(meetings.filter(_.id === id).delete)


}
