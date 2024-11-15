package com.management.meeting.repos

import com.management.meeting.entity.{MeetingRoom, MeetingRoomTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import jakarta.inject.{Inject, Singleton}
import slick.lifted

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MeetingRoomDetailRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val meetingRoomDetails = lifted.TableQuery[MeetingRoomTable]

  def list(): Future[Seq[MeetingRoom]] = db.run(meetingRoomDetails.result)

  def find(id: Int): Future[Option[MeetingRoom]] = db.run(meetingRoomDetails.filter(_.id === id).result.headOption)

  def add(meetingRoomDetail: MeetingRoom): Future[Int] =
    db.run(meetingRoomDetails returning meetingRoomDetails.map(_.id) += meetingRoomDetail)

  def update(meetingRoomDetail: MeetingRoom): Future[Int] =
    db.run(meetingRoomDetails.filter(_.id === meetingRoomDetail.id).update(meetingRoomDetail))

  def delete(id: Int): Future[Int] = db.run(meetingRoomDetails.filter(_.id === id).delete)
}
