package com.management.equipment.service

import com.management.equipment.dtos.{EquipmentDetailReq, EquipmentReq}
import com.management.equipment.entity.{Equipment, EquipmentDetail}
import com.management.equipment.repos.EquipmentDetailRepo
import com.management.equipment.view.{EquipmentDetailView, EquipmentView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentDetailService @Inject() (equipmentDetailRepo: EquipmentDetailRepo)(implicit ec: ExecutionContext) {

  def createEquipmentDetail(equipmentReq: EquipmentDetailReq, deptId: Int, orgId: Int): Future[EquipmentDetailView] = {
    val equipment =
      EquipmentDetail(
        id = 1,
        name = equipmentReq.name,
        description = equipmentReq.description,
        organizationId = orgId,
        departmentId = deptId,
        isActive = true,
        totalQuantity = equipmentReq.totalQuantity,
        availableQuantity = equipmentReq.availableQuantity
      )

    equipmentDetailRepo
      .add(equipment)
      .map { id =>
        EquipmentDetailView(
          id = id,
          name = equipment.name,
          description = equipment.description,
          organizationId = null,
          departmentId = null,
          isActive = equipment.isActive,
          totalQuantity = equipment.totalQuantity,
          availableQuantity = equipment.availableQuantity
        )
      }
  }

  def getEquipmentDetailById(id: Int): Future[Option[EquipmentDetailView]] =
    equipmentDetailRepo
      .findById(id)
      .flatMap {
        case Some(equipmentDetail) =>
          Future.successful(
            Some(
              EquipmentDetailView(
                id = equipmentDetail.id,
                name = equipmentDetail.name,
                description = equipmentDetail.description,
                organizationId = null,
                departmentId = null,
                isActive = equipmentDetail.isActive,
                totalQuantity = equipmentDetail.totalQuantity,
                availableQuantity = equipmentDetail.availableQuantity
              )
            )
          )
        case None =>
          Future.successful(None)
      }

  def updateEquipmentDetail(equipmentReq: EquipmentDetailReq, id: Int): Future[Option[EquipmentDetailView]] =
    equipmentDetailRepo
      .findById(id)
      .flatMap {
        case Some(equipmentDetail) =>
          val updatedEquipmentDetail =
            equipmentDetail.copy(
              name = equipmentReq.name,
              description = equipmentReq.description,
              isActive = equipmentReq.isActive,
              totalQuantity = equipmentReq.totalQuantity,
              availableQuantity = equipmentReq.availableQuantity
            )
          equipmentDetailRepo
            .update(updatedEquipmentDetail)
            .map { _ =>
              Some(
                EquipmentDetailView(
                  id = updatedEquipmentDetail.id,
                  name = updatedEquipmentDetail.name,
                  description = updatedEquipmentDetail.description,
                  organizationId = null,
                  departmentId = null,
                  isActive = updatedEquipmentDetail.isActive,
                  totalQuantity = updatedEquipmentDetail.totalQuantity,
                  availableQuantity = updatedEquipmentDetail.availableQuantity
                )
              )
            }
        case None =>
          Future.successful(None)
      }
}
