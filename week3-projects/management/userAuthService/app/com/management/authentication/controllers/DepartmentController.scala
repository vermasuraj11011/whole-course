package com.management.authentication.controllers

import com.management.common.entity.Department
import com.management.common.exceptions.PermissionDeniedException
import com.management.common.repos.DepartmentRepo
import com.management.common.utils.{DeleteDepartment, PermissionValidation, ReadDepartment, UpdateDepartment}
import play.api.libs.json.{Format, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import jakarta.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class DepartmentController @Inject() (departmentRepo: DepartmentRepo, cc: ControllerComponents)(implicit
  ec: ExecutionContext
) extends AbstractController(cc) {

  implicit val departmentFormat: Format[Department] = Json.format[Department]

  def getDepartments: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, ReadDepartment)) {
        departmentRepo
          .list()
          .map { departments =>
            Ok(Json.toJson(departments))
          }
      } else {
        Future.successful(Forbidden("you are not allowed to view departments"))
      }
    }

  def getDepartment(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, ReadDepartment)) {
        departmentRepo
          .find(id)
          .map {
            case Some(department) =>
              Ok(Json.toJson(department))
            case None =>
              NotFound
          }
      } else {
        Future.successful(Forbidden("you are not allowed to view this department"))
      }
    }

  def createDepartment: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, ReadDepartment)) {
        val department = request.body.asJson.get.as[Department]
        departmentRepo
          .add(department)
          .map { id =>
            Ok(Json.toJson(id))
          }
      } else {
        Future.successful(Forbidden("you are not allowed to create departments"))
      }
    }

  def updateDepartment(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, UpdateDepartment)) {
        val department = request.body.asJson.get.as[Department]
        if (department.id == id) {
          departmentRepo
            .update(department)
            .map { id =>
              Ok(Json.toJson(id))
            }
        } else {
          Future.successful(BadRequest("Id in the request body does not match the id in the path"))
        }
      } else {
        Future.successful(Forbidden("you are not allowed to update departments"))
      }
    }

  def deleteDepartment(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(request, DeleteDepartment)) {
        departmentRepo
          .delete(id)
          .map { id =>
            Ok(Json.toJson(id))
          }
      } else {
        Future.successful(Forbidden("you are not allowed to delete departments"))
      }
    }
}
