package com.management.equipment.controllers

import com.management.equipment.entity.EquipmentDetail
import com.management.equipment.service.{EquipmentAllocationService, EquipmentDetailService}
import jakarta.inject.{Inject, Singleton}
import play.api.libs.json.{Format, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class EquipmentDetailController @Inject() (
  val controllerComponents: ControllerComponents,
  equipmentDetailService: EquipmentDetailService
)(implicit ec: ExecutionContext)
  extends BaseController {

  implicit val personFormat: Format[EquipmentDetail] = Json.format[EquipmentDetail]

  def createEquipDetail: Action[AnyContent] =
    Action.async { implicit request =>
      val equipDetail = request.body.asJson.get.as[EquipmentDetail]
      equipmentDetailService.createEquipmentDetail1(equipDetail).map(equipDetail => Ok(Json.toJson(equipDetail)))
    }

  def getEquipDetails: Action[AnyContent] =
    Action.async { implicit request =>
      equipmentDetailService
        .getEquipmentDetails
        .map { equipDetails =>
          Ok(Json.toJson(equipDetails))
        }
    }

  def getEquipDetail(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      equipmentDetailService
        .getEquipmentDetailById1(id)
        .map {
          case Some(equipDetail) =>
            Ok(Json.toJson(equipDetail))
          case None =>
            NotFound("Equipment Detail not found")
        }
    }

}
