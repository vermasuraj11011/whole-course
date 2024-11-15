package com.management.authentication.controllers

import com.management.common.entity.User
import com.management.common.repos.UserRepo
import com.management.common.utils.{PermissionValidation, ReadUser, UpdateUser, DeleteUser, CreateUser}
import play.api.libs.json.{Format, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import jakarta.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject() (userRepo: UserRepo, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val personFormat: Format[User] = Json.format[User]

  def getUsers: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, ReadUser)) {
        userRepo
          .list()
          .map { users =>
            Ok(Json.toJson(users))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to view users"))
      }
    }

  def getUser(sno: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, ReadUser)) {
        userRepo
          .find(sno)
          .map {
            case Some(user) =>
              Ok(Json.toJson(user))
            case None =>
              NotFound
          }
      } else {
        Future.successful(Forbidden("You do not have permission to view this user"))
      }
    }

  def createUser: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, CreateUser)) {
        println("Creating user")
        val user = request.body.asJson.get.as[User]
        userRepo
          .add(user.copy(isActive = false, token = ""))
          .map { id =>
            Ok(Json.toJson(id))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to create users"))
      }
    }

  def updateUser(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, UpdateUser)) {
        val user = request.body.asJson.get.as[User]
        userRepo
          .update(user)
          .map { id =>
            Ok(Json.toJson(id))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to update users"))
      }
    }

  def deleteUser(sno: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, DeleteUser)) {
        userRepo
          .delete(sno)
          .map { id =>
            Ok(Json.toJson(id))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to delete users"))
      }
    }
}
