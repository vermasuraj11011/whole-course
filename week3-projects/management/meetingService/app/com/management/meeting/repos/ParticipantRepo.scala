package com.management.meeting.repos

import com.management.meeting.entity.{Participant, ParticipantTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import jakarta.inject.{Inject, Singleton}
import slick.lifted

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ParticipantRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val participants = lifted.TableQuery[ParticipantTable]

  def list(): Future[Seq[Participant]] = db.run(participants.result)

  def find(id: Int): Future[Option[Participant]] = db.run(participants.filter(_.id === id).result.headOption)

  def add(participant: Participant): Future[Int] = db.run(participants returning participants.map(_.id) += participant)

  def update(participant: Participant): Future[Int] =
    db.run(participants.filter(_.id === participant.id).update(participant))

  def delete(id: Int): Future[Int] = db.run(participants.filter(_.id === id).delete)

  def findMeetingParticipants(meetingId: Int): Future[Seq[Participant]] =
    db.run(participants.filter(_.meetingId === meetingId).result)
}
