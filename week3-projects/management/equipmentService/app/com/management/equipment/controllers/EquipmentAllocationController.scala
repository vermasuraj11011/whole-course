package com.management.equipment.controllers

import com.management.equipment.dtos.EquipmentAllocationReq
import com.management.equipment.service.EquipmentAllocationService
import play.api.libs.json.Json
import play.api.mvc._

import jakarta.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentAllocationController @Inject() (
  val controllerComponents: ControllerComponents,
  equipmentAllocationService: EquipmentAllocationService
)(implicit ec: ExecutionContext)
  extends BaseController {

  def allocateEquipment: Action[AnyContent] =
    Action.async { implicit request =>
      val equipmentReq =
        request
          .body
          .asJson
          .map(_.as[EquipmentAllocationReq])
          .getOrElse(throw new Exception("Equipment allocation request not found"))
      val deptId = request.headers.get("deptId").map(_.toInt).getOrElse(throw new Exception("Department Id not found"))
      val orgId  = request.headers.get("orgId").map(_.toInt).getOrElse(throw new Exception("Organization Id not found"))
      val userId = request.headers.get("userId").map(_.toInt).getOrElse(throw new Exception("User Id not found"))

      equipmentAllocationService
        .allocateEquipment(equipmentReq, deptId, orgId, userId)
        .map { allocationView =>
          Ok(Json.toJson(allocationView))
        }
        .recover { case ex: Exception =>
          BadRequest(Json.obj("error" -> ex.getMessage))
        }
    }

  def deallocateEquipment(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      val equipmentReq =
        request
          .body
          .asJson
          .map(_.as[EquipmentAllocationReq])
          .getOrElse(throw new Exception("Equipment allocation request not found"))
      val email = request.headers.get("email").getOrElse(throw new Exception("Email not found"))
      equipmentAllocationService
        .deallocateEquipment(id, equipmentReq, email)
        .map {
          case Some(allocationView) =>
            Ok(Json.toJson(allocationView))
          case None =>
            NotFound(Json.obj("error" -> "Equipment allocation not found"))
        }
        .recover { case ex: Exception =>
          BadRequest(Json.obj("error" -> ex.getMessage))
        }
    }

  def listNotReturnedEquipment: Action[AnyContent] =
    Action.async {
      equipmentAllocationService
        .findNotReturnedEquipment
        .map { equipments =>
          Ok(Json.toJson(equipments))
        }
        .recover { case ex: Exception =>
          BadRequest(Json.obj("error" -> ex.getMessage))
        }
    }

}
