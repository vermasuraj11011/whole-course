package com.management.meeting.controllers

import com.management.common.utils.{CreateMeeting, DeleteMeeting, PermissionValidation, ReadMeeting, UpdateMeeting}
import com.management.meeting.dtos.MeetingReq
import com.management.meeting.entity.Meeting
import com.management.meeting.repos.MeetingRepo
import com.management.meeting.service.MeetingService
import play.api.mvc._
import play.api.libs.json.{Format, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import jakarta.inject.{Inject, Singleton}
import play.filters.csrf.{CSRFAddToken, CSRFCheck}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MeetingController @Inject() (
  val cc: ControllerComponents,
  meetingRepo: MeetingRepo,
  meetingService: MeetingService
)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val personFormat: Format[Meeting] = Json.format[Meeting]

  def get_meetings: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(ReadMeeting)) {
        meetingRepo
          .list()
          .map { meetings =>
            Ok(Json.toJson(meetings))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to view meetings"))
      }
    }

  def get_meeting(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(ReadMeeting)) {
        meetingRepo
          .find(id)
          .map {
            case Some(meeting) =>
              Ok(Json.toJson(meeting))
            case None =>
              NotFound("Meeting not found")
          }
      } else {
        Future.successful(Forbidden("You do not have permission to view this meeting"))
      }
    }

  def create_meeting: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(CreateMeeting)) {
        val meeting = request.body.asJson.get.as[MeetingReq]
        val userId  = request.headers.get("user-id").get.toInt
        meetingService
          .createMeeting(userId, meeting)
          .map { meeting =>
            Ok(Json.toJson(meeting))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to create meetings"))
      }
    }

  def update_meeting(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(UpdateMeeting)) {
        val meeting = request.body.asJson.get.as[Meeting]
        meetingRepo
          .update(meeting)
          .map { id =>
            Ok(Json.toJson(meeting))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to update meetings"))
      }
    }

  def delete_meeting(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(DeleteMeeting)) {
        meetingRepo
          .delete(id)
          .map {
            case 1 =>
              Ok("Meeting deleted")
            case _ =>
              InternalServerError("Failed to delete meeting")
          }
      } else {
        Future.successful(Forbidden("You do not have permission to delete meetings"))
      }
    }
}
