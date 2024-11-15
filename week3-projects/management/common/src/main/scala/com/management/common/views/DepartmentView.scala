package com.management.common.views

import com.management.common.entity.Department

case class DepartmentView(id: Int, name: String, organizationId: Int, isActive: Boolean, head: Int) {}
object DepartmentView {
  import play.api.libs.json.{Format, Json}
  implicit val format: Format[DepartmentView] = Json.format[DepartmentView]

  def fromDepartment(department: Department): DepartmentView =
    DepartmentView(
      id = department.id,
      name = department.name,
      organizationId = department.organizationId,
      isActive = department.isActive,
      head = department.head
    )
}
