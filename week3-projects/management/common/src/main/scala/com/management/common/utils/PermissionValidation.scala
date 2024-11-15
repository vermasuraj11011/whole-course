package com.management.common.utils

import play.api.mvc.Request

object PermissionValidation {
  def validate(request: Request[_], requiredPermission: Permission): Boolean = {
    println(s"headers: ${request.headers}")
    // Extract relevant fields from the request headers
    val roleHeader                    = request.headers.get("role")
    val userIdHeader                  = request.headers.get("user-id")
    val meetingServiceEnabledHeader   = request.headers.get("is-meeting-service-enabled")
    val equipmentServiceEnabledHeader = request.headers.get("is-equipment-service-enabled")

    println(s"roleHeader: $roleHeader")
    println(s"userIdHeader: $userIdHeader")
    println(s"meetingServiceEnabledHeader: $meetingServiceEnabledHeader")
    println(s"equipmentServiceEnabledHeader: $equipmentServiceEnabledHeader")

    (roleHeader, userIdHeader, meetingServiceEnabledHeader, equipmentServiceEnabledHeader) match {
      case (Some(role), Some(_), Some(meetingServiceEnabled), Some(equipmentServiceEnabled)) =>
        // Check if the organization has access to the meeting or equipment services as needed
        val meetingServiceEnabledFlag   = meetingServiceEnabled.toBoolean
        val equipmentServiceEnabledFlag = equipmentServiceEnabled.toBoolean

        // Match the role
        val userRole = matchRole(role)
        userRole match {
          case Some(userRole) =>
            // Check if the organization is authorized for the modules and if the role has the required permission
            requiredPermission match {
              case _: MeetingServiceEnum if !meetingServiceEnabledFlag =>
                false // Organization does not have access to the meeting service
              case _: EquipmentServiceEnum if !equipmentServiceEnabledFlag =>
                false // Organization does not have access to the equipment service
              case _ =>
                userRole.permissions.contains(requiredPermission) // Standard permission check
            }
          case None =>
            // If the role doesn't match any predefined roles, return false
            false
        }
      case _ =>
        // If any necessary headers are missing, validation fails
        false
    }
  }

  private def matchRole(roleStr: String): Option[Role] =
    roleStr.toLowerCase match {
      case "user" =>
        Some(UserRole)
      case "department_head" =>
        Some(DepartmentHead)
      case "admin" =>
        Some(Admin)
      case "super_admin" =>
        Some(SuperAdmin)
      case _ =>
        None // Return None if the role is not recognized
    }
}
