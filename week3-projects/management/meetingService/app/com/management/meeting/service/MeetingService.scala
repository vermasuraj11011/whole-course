package com.management.meeting.service

import com.management.common.service.UserService
import com.management.common.utils.{FutureUtil, KafkaProducerUtil}
import com.management.meeting.dtos.{MeetingReminder, MeetingReq}
import com.management.meeting.entity.{Meeting, MeetingRoom, Participant}
import com.management.meeting.repos.{MeetingRepo, MeetingRoomDetailRepo, ParticipantRepo}
import com.management.meeting.views.{MeetingRoomDetailView, MeetingView, ParticipantView}
import jakarta.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MeetingService @Inject() (
  meetingRepo: MeetingRepo,
  userService: UserService,
  meetingRoomDetailRepo: MeetingRoomDetailRepo,
  participantRepo: ParticipantRepo,
  kafkaProducerUtil: KafkaProducerUtil
)(implicit ec: ExecutionContext) {

  def getMeetingRoomDetails(roomId: Int): Future[Option[MeetingRoomDetailView]] =
    meetingRoomDetailRepo
      .find(roomId)
      .map {
        _.map(MeetingRoomDetailView.fromMeetingRoom)
      }

  def getMeetingRoomById(id: Int): Future[Option[MeetingRoom]] = meetingRoomDetailRepo.find(id)

  def createMeeting(userId: Int, meeting: MeetingReq): Future[Meeting] =
    FutureUtil
      .join(userService.getUser(userId), getMeetingRoomById(meeting.roomId))
      .flatMap {
        case (Some(owner), Some(meetingRoom)) if meetingRoom.status == "available" =>
          FutureUtil
            .join(
              meetingRepo.add(meeting.toMeeting(userId, owner.department.id, owner.organization.id)),
              meetingRoomDetailRepo.update(meetingRoom.copy(status = "unavailable"))
            )
            .flatMap { case (meetingId, _) =>
              userService
                .getUsersEntity(meeting.participants)
                .flatMap { users =>
                  val participants =
                    users.map { user =>
                      Participant(
                        id = 0,
                        meetingId = meetingId,
                        userId = user.id,
                        isOrganizer = userId == user.id,
                        isAttending = true,
                        isRejected = false,
                        creation_remainder = false,
                        update_remainder = false
                      )
                    }

                  val owner = users.find(_.id == userId).get

                  Future
                    .sequence(participants.map(participantRepo.add))
                    .map { _ =>
                      pushMeetingReminderToKafka(
                        MeetingReminder(
                          meetingId = meetingId,
                          name = meeting.title,
                          userId = owner.id,
                          startTime = meeting.startTime,
                          endTime = meeting.endTime,
                          isReminderSent = false,
                          email = users.map(_.email).mkString(",")
                        )
                      )
                    }
                    .map { _ =>
                      Meeting(
                        id = meetingId,
                        title = meeting.title,
                        description = meeting.description,
                        roomId = meetingRoom.id,
                        startTime = meeting.startTime,
                        endTime = meeting.endTime,
                        location = meeting.location,
                        organizerId = owner.id,
                        departmentId = owner.departmentId.get,
                        organizationId = owner.organizationId,
                        status = "scheduled",
                        isCompleted = false,
                        isCancelled = false
                      )
                    }
                }
            }
        case (Some(_), Some(meetingRoom)) if meetingRoom.status != "available" =>
          Future.failed(new Exception("Meeting Room not available"))
        case _ =>
          Future.failed(new Exception("User or meeting room not found"))
      }

  def createMeeting1(userId: Int, meeting: MeetingReq): Future[MeetingView] =
    userService
      .getUser(userId)
      .flatMap {
        case Some(user) =>
          getMeetingRoomDetails(meeting.roomId).flatMap {
            case Some(meetingRoomView) if meetingRoomView.status == "available" =>
              meetingRepo
                .add(meeting.toMeeting(user.id, user.department.id, user.organization.id))
                .flatMap { meetingId =>
                  userService
                    .getUsers(meeting.participants.toList)
                    .flatMap { userViews =>
                      val participants =
                        userViews.map { userView =>
                          Participant(
                            id = 0,
                            meetingId = meetingId,
                            userId = userView.id,
                            isOrganizer = userId == userView.id,
                            isAttending = userId == userView.id,
                            isRejected = !(userId == userView.id),
                            creation_remainder = false,
                            update_remainder = false
                          )
                        }

                      Future
                        .sequence(participants.map(participantRepo.add))
                        .flatMap { _ =>
                          val participantViews =
                            participants.map { participant =>
                              ParticipantView(
                                participant.id,
                                participant.userId,
                                userViews.find(_.id == participant.userId).get,
                                participant.isOrganizer,
                                participant.isAttending,
                                participant.isRejected,
                                participant.creation_remainder,
                                participant.update_remainder
                              )
                            }

                          val messageFutures =
                            participantViews.map { participantView =>
                              pushMeetingReminderToKafka(
                                MeetingReminder(
                                  meetingId = meetingId,
                                  name = meeting.title,
                                  userId = userId,
                                  startTime = meeting.startTime,
                                  endTime = meeting.endTime,
                                  isReminderSent = true,
                                  email = user.email
                                )
                              )
                            }

                          Future
                            .sequence(messageFutures)
                            .map { _ =>
                              MeetingView(
                                id = meetingId,
                                title = meeting.title,
                                description = meeting.description,
                                room = meetingRoomView,
                                startTime = meeting.startTime,
                                endTime = meeting.endTime,
                                location = meeting.location,
                                organizer = user,
                                participants = participantViews.toList
                              )
                            }
                        }
                    }
                }

            case Some(_) =>
              Future.failed(new Exception("Meeting room is not available"))

            case None =>
              Future.failed(new Exception("Meeting room not found"))
          }

        case None =>
          Future.failed(new Exception("User not found"))
      }

  def startMeeting(meetingId: Int): Future[MeetingView] =
    meetingRepo
      .find(meetingId)
      .flatMap {
        case Some(meeting) =>
          meetingRepo
            .update(meeting.copy(status = "ongoing"))
            .flatMap { _ =>
              for {
                userOpt        <- userService.getUser(meeting.organizerId)
                meetingRoomOpt <- getMeetingRoomDetails(meeting.roomId)
                result <-
                  (userOpt, meetingRoomOpt) match {
                    case (Some(user), Some(meetingRoomView)) =>
                      getListOfParticipants(meetingId).map { participants =>
                        MeetingView(
                          id = meetingId,
                          title = meeting.title,
                          description = meeting.description,
                          room = meetingRoomView,
                          startTime = meeting.startTime,
                          endTime = meeting.endTime,
                          location = meeting.location,
                          organizer = user,
                          participants = participants
                        )
                      }
                    case _ =>
                      Future.failed(new Exception("User or meeting room not found"))
                  }
              } yield result
            }

        case None =>
          Future.failed(new Exception("Meeting not found"))
      }

  def getListOfParticipants(meetingId: Int): Future[List[ParticipantView]] =
    participantRepo
      .findMeetingParticipants(meetingId)
      .flatMap { participants =>
        userService
          .getUsers(participants.map(_.userId).toList)
          .map { userViews =>
            participants
              .map { participant =>
                ParticipantView(
                  id = participant.id,
                  meetingId = participant.meetingId,
                  userId = userViews.find(_.id == participant.userId).get,
                  isOrganizer = participant.isOrganizer,
                  isAttending = participant.isAttending,
                  isRejected = participant.isRejected,
                  creation_remainder = participant.creation_remainder,
                  update_remainder = participant.update_remainder
                )
              }
              .toList
          }
      }

  private def pushMeetingReminderToKafka(reminder: MeetingReminder): Future[Unit] =
    kafkaProducerUtil
      .sendMessage[MeetingReminder](
        topic = "meeting-reminder",
        key = s"meeting_reminder_${reminder.name.replaceAll(" ", "_")}",
        value = reminder
      )
      .map(_ => ())
}
