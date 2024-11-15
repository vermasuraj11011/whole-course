package com.management.equipment.service

import com.management.equipment.dtos.{EquipmentAllocationReq, EquipmentReturnReminder}
import com.management.equipment.entity.EquipmentAllocation
import com.management.equipment.repos.{EquipmentAllocationRepo, EquipmentDetailRepo}
import com.management.equipment.repositories.EquipmentRepo
import com.management.equipment.view.{EquipmentAllocationView, EquipmentDetailView, EquipmentView}
import com.management.equipment.entity.EquipmentDetail
import com.management.common.utils.KafkaProducerUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentAllocationService @Inject() (
  equipmentAllocationRepo: EquipmentAllocationRepo,
  equipmentDetailService: EquipmentDetailService,
  equipmentService: EquipmentService,
  equipmentRepo: EquipmentRepo,
  equipmentDetailRepo: EquipmentDetailRepo,
  kafkaProducerUtil: KafkaProducerUtil
)(implicit ec: ExecutionContext) {

  def allocateEquipment(
    equipmentReq: EquipmentAllocationReq,
    deptId: Int,
    orgId: Int,
    userId: Int
  ): Future[EquipmentAllocationView] =
    equipmentDetailService
      .getEquipmentDetailById(equipmentReq.equipmentId)
      .flatMap {
        case Some(equipmentDetailView) =>
          equipmentRepo
            .findByEquipmentDetailId(equipmentDetailView.id)
            .flatMap {
              case equipments if equipments.nonEmpty =>
                val availableEquipment = equipments.filter(_.status == "available")

                if (availableEquipment.size < equipmentReq.allocatedQuantity || availableEquipment.isEmpty) {
                  Future.failed(new Exception("Equipment not available"))
                } else {
                  Future
                    .sequence(
                      availableEquipment
                        .take(equipmentReq.allocatedQuantity)
                        .map { equipment =>
                          equipmentService.updateEquipment(equipment.copy(status = "allocated"))
                        }
                    )
                    .flatMap { _ =>
                      equipmentAllocationRepo.insert(
                        EquipmentAllocation(
                          id = 1,
                          equipmentDetailId = equipmentDetailView.id,
                          organizationId = orgId,
                          departmentId = deptId,
                          userId = userId,
                          allocatedQuantity = equipmentDetailView.availableQuantity - equipmentReq.allocatedQuantity,
                          allocatedDate = System.currentTimeMillis(),
                          isActive = true,
                          deallocatedDate = None,
                          purpose = equipmentReq.purpose,
                          returnCondition = None
                        )
                      )
                    }
                    .map { id =>
                      equipmentDetailRepo.update(
                        EquipmentDetail(
                          id = equipmentDetailView.id,
                          name = equipmentDetailView.name,
                          description = equipmentDetailView.description,
                          organizationId = orgId,
                          departmentId = deptId,
                          isActive = equipmentDetailView.isActive,
                          totalQuantity = equipmentDetailView.totalQuantity,
                          availableQuantity = equipmentDetailView.availableQuantity - equipmentReq.allocatedQuantity
                        )
                      )
                      id
                    }
                    .map { id =>
                      EquipmentAllocationView(
                        id = id,
                        equipmentDetail =
                          EquipmentDetailView(
                            id = equipmentDetailView.id,
                            name = equipmentDetailView.name,
                            description = equipmentDetailView.description,
                            organizationId = null,
                            departmentId = null,
                            isActive = equipmentDetailView.isActive,
                            totalQuantity = equipmentDetailView.totalQuantity,
                            availableQuantity = equipmentDetailView.availableQuantity
                          ),
                        organization = null,
                        department = null,
                        userId = null,
                        allocatedQuantity = equipmentReq.allocatedQuantity,
                        allocatedDate = System.currentTimeMillis(),
                        isActive = true,
                        deallocatedDate = None,
                        purpose = equipmentReq.purpose,
                        returnCondition = None
                      )
                    }
                }
              case _ =>
                Future.failed(new Exception("Equipment not available"))
            }
        case None =>
          Future.failed(new Exception("Equipment Detail not found"))
      }

  def deallocateEquipment(
    id: Int,
    equipmentDeallocationReq: EquipmentAllocationReq,
    userEmail: String
  ): Future[Option[EquipmentAllocationView]] =
    equipmentAllocationRepo
      .findById(id)
      .flatMap {
        case Some(equipmentAllocated) =>
          equipmentRepo
            .findByEquipmentDetailId(equipmentAllocated.equipmentDetailId)
            .flatMap { equipments =>
              Future
                .sequence(
                  equipments
                    .filter(_.status == "allocated")
                    .map { equipment =>
                      equipmentService.updateEquipment(equipment.copy(status = "available"))
                    }
                )
                .flatMap { _ =>
                  equipmentAllocationRepo
                    .update(equipmentAllocated.copy(deallocatedDate = Some(System.currentTimeMillis())))
                    .flatMap { _ =>
                      equipmentDetailService.getEquipmentDetailById(equipmentAllocated.equipmentDetailId)
                    }
                }
                .flatMap {
                  case Some(equipmentDetailView) =>
                    val updatedEquipmentDetail =
                      EquipmentDetail(
                        id = equipmentDetailView.id,
                        name = equipmentDetailView.name,
                        description = equipmentDetailView.description,
                        organizationId = equipmentAllocated.organizationId,
                        departmentId = equipmentAllocated.departmentId,
                        isActive = equipmentDetailView.isActive,
                        totalQuantity = equipmentDetailView.totalQuantity,
                        availableQuantity = equipmentDetailView.availableQuantity + equipmentAllocated.allocatedQuantity
                      )
                    equipmentDetailRepo
                      .update(updatedEquipmentDetail)
                      .flatMap { _ =>
                        Future
                          .sequence(
                            equipments.map { equipment =>
                              equipmentReturnedReminderPushToKafka(
                                EquipmentReturnReminder(
                                  equipmentId = equipment.id,
                                  equipmentName = s"${updatedEquipmentDetail.name}${equipment.serialNo}",
                                  userId = equipmentAllocated.userId,
                                  allocatedDate = equipmentAllocated.allocatedDate,
                                  returnDate = System.currentTimeMillis(),
                                  email = userEmail,
                                  condition = equipmentDeallocationReq.returnCondition.getOrElse("Good")
                                )
                              )
                            }
                          )
                          .map { _ =>
                            Some(
                              EquipmentAllocationView(
                                id = equipmentAllocated.id,
                                equipmentDetail = equipmentDetailView,
                                organization = null,
                                department = null,
                                userId = null,
                                allocatedQuantity = equipmentAllocated.allocatedQuantity,
                                allocatedDate = equipmentAllocated.allocatedDate,
                                isActive = false,
                                deallocatedDate = equipmentAllocated.deallocatedDate,
                                purpose = equipmentAllocated.purpose,
                                returnCondition = equipmentDeallocationReq.returnCondition
                              )
                            )
                          }
                      }
                  case None =>
                    Future.successful(None)
                }
            }
        case None =>
          Future.successful(None)
      }

  def findNotReturnedEquipment: Future[List[EquipmentView]] =
    equipmentAllocationRepo
      .findNotReturnedEquipment
      .flatMap { equipmentAllocations =>
        Future.sequence(
          equipmentAllocations
            .map { equipmentAllocation =>
              equipmentDetailService
                .getEquipmentDetailById(equipmentAllocation.equipmentDetailId)
                .map {
                  case Some(equipmentDetailView) =>
                    EquipmentView(
                      id = equipmentAllocation.id,
                      serialNo = null,
                      organization = null,
                      department = null,
                      equipmentDetail = equipmentDetailView,
                      status = "allocated"
                    )
                  case None =>
                    EquipmentView(
                      id = equipmentAllocation.id,
                      serialNo = null,
                      organization = null,
                      department = null,
                      equipmentDetail = null,
                      status = "allocated"
                    )
                }
            }
            .toList
        )
      }

  private def equipmentReturnedReminderPushToKafka(reminder: EquipmentReturnReminder): Future[Unit] =
    kafkaProducerUtil
      .sendMessage[EquipmentReturnReminder](
        topic = "equipment-returned-reminder",
        key = s"equipment_reminder_${reminder.equipmentName.replaceAll(" ", "_")}",
        value = reminder
      )
      .map(_ => ())
}
