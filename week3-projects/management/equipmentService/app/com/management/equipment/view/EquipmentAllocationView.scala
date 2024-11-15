package com.management.equipment.view

import com.management.common.entity.Organization
import com.management.common.views.{DepartmentView, OrganizationView, UserView}

case class EquipmentAllocationView(
  id: Int,
  equipmentDetail: EquipmentDetailView,
  organization: OrganizationView,
  department: DepartmentView,
  userId: UserView,
  allocatedQuantity: Int,
  isActive: Boolean,
  allocatedDate: Long,
  deallocatedDate: Option[Long],
  purpose: String,
  returnCondition: Option[String]
)

object EquipmentAllocationView {
  import play.api.libs.json.{Format, Json}
  implicit val format: Format[EquipmentAllocationView] = Json.format[EquipmentAllocationView]
}
