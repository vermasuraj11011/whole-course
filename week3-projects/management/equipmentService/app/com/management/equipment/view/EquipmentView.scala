package com.management.equipment.view

import com.management.common.views.{DepartmentView, OrganizationView}

case class EquipmentView(
  id: Int,
  serialNo: String,
  organization: OrganizationView,
  department: DepartmentView,
  equipmentDetail: EquipmentDetailView,
  status: String
)

object EquipmentView {
  import play.api.libs.json.{Format, Json}
  implicit val format: Format[EquipmentView] = Json.format[EquipmentView]
}
