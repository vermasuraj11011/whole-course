package com.management.authentication.controllers

import com.management.authentication.dtos.{AuthDetail, LoginSuccess}
import com.management.authentication.service.AuthService
import com.management.authentication.views.LogoutView
import com.management.common.repos.UserRepo
import com.management.common.utils.{PermissionValidation, ReadUser, TokenGenerator}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import play.api.libs.json.{Format, Json}
import jakarta.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class AuthController @Inject() (userRepo: UserRepo, authService: AuthService, cc: ControllerComponents)(implicit
  ec: ExecutionContext
) extends AbstractController(cc) {

  def login: Action[AnyContent] =
    Action.async { implicit request =>
      val authDetail: AuthDetail = request.body.asJson.get.as[AuthDetail]
      println(s"AuthDetail: $authDetail")

      authService
        .verifyCreds(authDetail.email, authDetail.password)
        .flatMap {
          case Some(user) =>
            val token       = TokenGenerator.generateToken(user.name)
            val updatedUser = user.copy(token = token, isActive = true)
            authService
              .updateUser(updatedUser)
              .map {
                case 1 =>
                  val loginSuccess = LoginSuccess(token, user.email, user.name, user.role)
                  Ok(Json.toJson(loginSuccess))
                case _ =>
                  InternalServerError("Failed to update token")
              }
          case None =>
            Future.successful(Unauthorized("Invalid credentials"))
        }
    }

  def logout: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, ReadUser)) {
        val userId = request.headers.get("user-id").get.toInt
        userRepo
          .find(userId)
          .flatMap {
            case Some(user) =>
              val updatedUser = user.copy(token = "", isActive = false)
              userRepo
                .update(updatedUser)
                .map {
                  case 1 =>
                    Ok(
                      Json.toJson(
                        LogoutView(
                          message = "You have been successfully logged out",
                          name = user.name,
                          email = user.email,
                          organizationId = userId
                        )
                      )
                    )
                  case _ =>
                    InternalServerError("Failed to logout")
                }
                .recover { case _ =>
                  InternalServerError("Failed to logout")
                }
            case None =>
              Future.successful(NotFound("User not found"))
          }
          .recover { case _ =>
            InternalServerError("User not found")
          }
      } else
        Future
          .successful(Forbidden("You do not have permission to view users"))
          .recover { case _ =>
            Forbidden("You do not have permission to view users")
          }
    }
}
