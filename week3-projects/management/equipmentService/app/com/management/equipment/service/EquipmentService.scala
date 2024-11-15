package com.management.equipment.service

import com.management.common.views.{DepartmentView, OrganizationView}
import com.management.equipment.dtos.EquipmentReq
import com.management.equipment.entity.Equipment
import com.management.equipment.repos.EquipmentDetailRepo
import com.management.equipment.repositories.EquipmentRepo
import com.management.equipment.view.{EquipmentDetailView, EquipmentView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentService @Inject() (equipmentRepo: EquipmentRepo, equipmentDetailRepo: EquipmentDetailRepo)(implicit
  ec: ExecutionContext
) {

  def createEquipment(equipmentReq: EquipmentReq, deptId: Int, orgId: Int): Future[EquipmentView] =
    equipmentDetailRepo
      .findById(equipmentReq.equipmentDetailId)
      .flatMap {
        case Some(equipmentDetail) =>
          val equipment =
            Equipment(
              id = 1,
              serialNo = equipmentReq.serialNo,
              organizationId = orgId,
              departmentId = deptId,
              equipmentDetail = equipmentReq.equipmentDetailId,
              status = "Active"
            )

          equipmentRepo
            .insert(equipment)
            .map { id =>
              EquipmentView(
                id = id,
                serialNo = equipment.serialNo,
                organization = null,
                department = null,
                equipmentDetail =
                  EquipmentDetailView(
                    id = 1,
                    name = "",
                    description = "",
                    organizationId = null,
                    departmentId = null,
                    isActive = true,
                    totalQuantity = 0,
                    availableQuantity = 0
                  ),
                status = equipment.status
              )
            }
        case None =>
          Future.failed(new Exception("Equipment Detail not found"))
      }

  def findEquipmentById(id: Int): Future[Option[EquipmentView]] =
    equipmentRepo
      .findById(id)
      .flatMap {
        case Some(equipment) =>
          equipmentDetailRepo
            .findById(equipment.equipmentDetail)
            .map {
              case Some(equipmentDetail) =>
                Some(
                  EquipmentView(
                    id = equipment.id,
                    serialNo = equipment.serialNo,
                    organization = null,
                    department = null,
                    equipmentDetail =
                      EquipmentDetailView(
                        id = equipmentDetail.id,
                        name = equipmentDetail.name,
                        description = equipmentDetail.description,
                        organizationId = null,
                        departmentId = null,
                        isActive = equipmentDetail.isActive,
                        totalQuantity = equipmentDetail.totalQuantity,
                        availableQuantity = equipmentDetail.availableQuantity
                      ),
                    status = equipment.status
                  )
                )
              case None =>
                None
            }
        case None =>
          Future.successful(None)
      }

  def findAllEquipment: Future[Seq[EquipmentView]] =
    equipmentRepo
      .all()
      .flatMap { equipments =>
        Future.sequence(
          equipments.map { equipment =>
            equipmentDetailRepo
              .findById(equipment.equipmentDetail)
              .map {
                case Some(equipmentDetail) =>
                  EquipmentView(
                    id = equipment.id,
                    serialNo = equipment.serialNo,
                    organization = null,
                    department = null,
                    equipmentDetail =
                      EquipmentDetailView(
                        id = equipmentDetail.id,
                        name = equipmentDetail.name,
                        description = equipmentDetail.description,
                        organizationId = null,
                        departmentId = null,
                        isActive = equipmentDetail.isActive,
                        totalQuantity = equipmentDetail.totalQuantity,
                        availableQuantity = equipmentDetail.availableQuantity
                      ),
                    status = equipment.status
                  )
                case None =>
                  EquipmentView(
                    id = equipment.id,
                    serialNo = equipment.serialNo,
                    organization = null,
                    department = null,
                    equipmentDetail =
                      EquipmentDetailView(
                        id = 1,
                        name = "",
                        description = "",
                        organizationId = null,
                        departmentId = null,
                        isActive = true,
                        totalQuantity = 0,
                        availableQuantity = 0
                      ),
                    status = equipment.status
                  )
              }
          }
        )
      }

  def findAvailableEquipment: Future[List[EquipmentView]] =
    equipmentRepo
      .findAvailableEquipment
      .map { equipments =>
        equipments
          .map(e =>
            EquipmentView(
              id = e.id,
              serialNo = e.serialNo,
              organization = null,
              department = null,
              equipmentDetail = null,
              status = e.status
            )
          )
          .toList
      }

  def findUnAvailableEquipment: Future[List[EquipmentView]] =
    equipmentRepo
      .findUnAvailableEquipment
      .map { equipments =>
        equipments
          .map(e =>
            EquipmentView(
              id = e.id,
              serialNo = e.serialNo,
              organization = null,
              department = null,
              equipmentDetail = null,
              status = e.status
            )
          )
          .toList
      }

  def findAllEquipmentWithEquipmentDetailId(id: Int): Future[List[EquipmentView]] =
    equipmentRepo
      .findByEquipmentDetailId(id)
      .map { equipments =>
        equipments
          .map(e =>
            EquipmentView(
              id = e.id,
              serialNo = e.serialNo,
              organization = null,
              department = null,
              equipmentDetail = null,
              status = e.status
            )
          )
          .toList
      }

  def updateEquipment(equipment: Equipment): Future[EquipmentView] =
    equipmentRepo
      .update(equipment)
      .map { _ =>
        EquipmentView(
          id = equipment.id,
          serialNo = equipment.serialNo,
          organization = null,
          department = null,
          equipmentDetail = null,
          status = equipment.status
        )
      }

  def find(id: Int): Future[Option[Equipment]] = equipmentRepo.findById(id)

  def deleteEquipment(id: Int): Future[Boolean] = equipmentRepo.delete(id).map(_ > 0)
}
