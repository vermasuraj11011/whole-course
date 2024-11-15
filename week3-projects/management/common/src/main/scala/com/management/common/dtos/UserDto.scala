package com.management.common.dtos

case class UserDto(
  user_id: Int,
  email: String,
  name: String,
  role: String,
  department_id: DepartmentDto,
  organization_id: OrganizationDto
)


