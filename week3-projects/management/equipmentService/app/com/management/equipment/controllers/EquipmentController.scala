package com.management.equipment.controllers

import com.management.equipment.dtos.EquipmentReq
import com.management.equipment.service.EquipmentService
import play.api.libs.json.Json
import play.api.mvc._
import jakarta.inject._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentController @Inject() (
  val controllerComponents: ControllerComponents,
  equipmentService: EquipmentService
)(implicit ec: ExecutionContext)
  extends BaseController {

  def findEquipment(id: Int): Action[AnyContent] =
    Action.async {
      equipmentService
        .findEquipmentById(id)
        .map {
          case Some(equipment) =>
            Ok(Json.toJson(equipment))
          case None =>
            NotFound(Json.obj("error" -> "Equipment not found"))
        }
        .recover { case ex: Exception =>
          BadRequest(Json.obj("error" -> ex.getMessage))
        }
    }

  def listEquipment: Action[AnyContent] =
    Action.async {
      equipmentService
        .findAllEquipment
        .map { equipments =>
          Ok(Json.toJson(equipments))
        }
        .recover { case ex: Exception =>
          BadRequest(Json.obj("error" -> ex.getMessage))
        }
    }

  def listAvailableEquipment: Action[AnyContent] =
    Action.async {
      equipmentService
        .findUnAvailableEquipment
        .map { equipments =>
          Ok(Json.toJson(equipments))
        }
    }

  def listUnAvailableEquipment: Action[AnyContent] =
    Action.async {
      equipmentService
        .findUnAvailableEquipment
        .map { equipments =>
          Ok(Json.toJson(equipments))
        }
        .recover { case ex: Exception =>
          BadRequest(Json.obj("error" -> ex.getMessage))
        }
    }

  def create: Action[AnyContent] =
    Action.async { implicit request =>
      val equipmentReq =
        request.body.asJson.map(_.as[EquipmentReq]).getOrElse(throw new Exception("Equipment request not found"))
      val deptId = request.headers.get("deptId").map(_.toInt).getOrElse(throw new Exception("Department Id not found"))
      val orgId  = request.headers.get("orgId").map(_.toInt).getOrElse(throw new Exception("Organization Id not found"))
      equipmentService
        .createEquipment(equipmentReq, deptId, orgId)
        .map { equipment =>
          Ok(Json.toJson(equipment))
        }
        .recover { case ex: Exception =>
          BadRequest(Json.obj("error" -> ex.getMessage))
        }
    }

  def update(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      val equipmentReq =
        request.body.asJson.map(_.as[EquipmentReq]).getOrElse(throw new Exception("Equipment request not found"))

      equipmentService
        .find(id)
        .flatMap {
          case Some(equipment) =>
            equipmentService
              .updateEquipment(equipment.copy(status = equipmentReq.status))
              .map { equipment =>
                Ok(Json.toJson(equipment))
              }
          case None =>
            Future.successful(NotFound(Json.obj("error" -> "Equipment not found")))
        }
    }

}
