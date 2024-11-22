package com.management.meeting.controllers

import com.management.meeting.entity.MeetingRoom
import com.management.meeting.service.MeetingRoomService
import com.management.common.utils.{CreateDepartment, PermissionValidation}
import play.api.libs.json.{Format, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import jakarta.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MeetingRoomController @Inject() (meetingRoomService: MeetingRoomService, cc: ControllerComponents)(implicit
  ec: ExecutionContext
) extends AbstractController(cc) {

  implicit val meetingRoomFormat: Format[MeetingRoom] = Json.format[MeetingRoom]

  def getMeetingRooms: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(CreateDepartment)) {
        meetingRoomService
          .getMeetingRooms
          .map { meetingRooms =>
            Ok(Json.toJson(meetingRooms))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to view meeting rooms"))
      }
    }

  def getMeetingRoom(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(CreateDepartment)) {
        meetingRoomService
          .getMeetingRoom(id)
          .map {
            case Some(meetingRoom) =>
              Ok(Json.toJson(meetingRoom))
            case None =>
              NotFound("Meeting room not found")
          }
      } else {
        Future.successful(Forbidden("You do not have permission to view this meeting room"))
      }
    }

  def createMeetingRoom: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(CreateDepartment)) {
        val meetingRoom = request.body.asJson.get.as[MeetingRoom]
        meetingRoomService
          .createMeetingRoom(meetingRoom)
          .map { id =>
            Ok(Json.toJson(id))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to create meeting rooms"))
      }
    }

  def updateMeetingRoom(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(CreateDepartment)) {
        val meetingRoom = request.body.asJson.get.as[MeetingRoom]
        meetingRoomService
          .updateMeetingRoom(meetingRoom)
          .map { id =>
            Ok(Json.toJson(id))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to update meeting rooms"))
      }
    }

  def deleteMeetingRoom(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(CreateDepartment)) {
        meetingRoomService
          .deleteMeetingRoom(id)
          .map {
            case 1 =>
              Ok("Meeting room deleted")
            case _ =>
              InternalServerError("Failed to delete meeting room")
          }
      } else {
        Future.successful(Forbidden("You do not have permission to delete meeting rooms"))
      }
    }
}
