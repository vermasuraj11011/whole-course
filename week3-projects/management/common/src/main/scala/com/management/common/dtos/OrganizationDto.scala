package com.management.common.dtos

case class OrganizationDto(
  organization_id: Int,
  name: String,
  users: List[UserDto] = List.empty,
  departments: List[DepartmentDto] = List.empty,
  isActive: Boolean = true,
  isMeetingServiceEnabled: Boolean = false,
  isEquipmentServiceEnabled: Boolean = false
)
