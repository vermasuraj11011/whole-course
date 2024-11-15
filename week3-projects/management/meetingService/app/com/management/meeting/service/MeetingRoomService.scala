package com.management.meeting.service

import com.management.meeting.entity.MeetingRoom
import com.management.meeting.repos.MeetingRoomDetailRepo
import com.management.meeting.views.MeetingRoomDetailView
import jakarta.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class MeetingRoomService @Inject() (meetingRoomRepo: MeetingRoomDetailRepo)(implicit ec: ExecutionContext) {

  def getMeetingRooms: Future[List[MeetingRoomDetailView]] =
    meetingRoomRepo
      .list()
      .map { meetingRooms =>
        meetingRooms
          .map { room =>
            MeetingRoomDetailView.fromMeetingRoom(room)
          }
          .toList
      }

  def getMeetingRoom(id: Int): Future[Option[MeetingRoomDetailView]] =
    meetingRoomRepo
      .find(id)
      .map {
        case Some(room) =>
          Some(MeetingRoomDetailView.fromMeetingRoom(room))
        case None =>
          None
      }

  def createMeetingRoom(meetingRoom: MeetingRoom): Future[Int] = meetingRoomRepo.add(meetingRoom)

  def updateMeetingRoom(meetingRoom: MeetingRoom): Future[Int] = meetingRoomRepo.update(meetingRoom)

  def deleteMeetingRoom(id: Int): Future[Int] = meetingRoomRepo.delete(id)
}
