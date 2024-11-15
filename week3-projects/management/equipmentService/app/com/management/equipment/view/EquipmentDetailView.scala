package com.management.equipment.view

import com.management.common.views.{DepartmentView, OrganizationView}

case class EquipmentDetailView(
  id: Int,
  name: String,
  description: String,
  organizationId: OrganizationView,
  departmentId: DepartmentView,
  isActive: Boolean,
  totalQuantity: Int,
  availableQuantity: Int
)

object EquipmentDetailView {
  import play.api.libs.json.{Format, Json}
  implicit val format: Format[EquipmentDetailView] = Json.format[EquipmentDetailView]
}
