package com.management.common.dtos

case class DepartmentDto(
  department_id: Int,
  name: String,
  organization_id: Int,
  isActive: Boolean = true,
  owner: UserDto = null
)
