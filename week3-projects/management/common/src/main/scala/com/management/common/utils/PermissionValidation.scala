package com.management.common.utils

import play.api.mvc.Request

object PermissionValidation {
  def validate(requiredPermission: Permission)(implicit request: Request[_]): Boolean = {
    println(s"headers: ${request.headers}")
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

        val userRole = matchRole(role)
        userRole match {
          case Some(userRole) =>
            requiredPermission match {
              case _: MeetingServiceEnum if !meetingServiceEnabledFlag =>
                false
              case _: EquipmentServiceEnum if !equipmentServiceEnabledFlag =>
                false
              case _ =>
                userRole.permissions.contains(requiredPermission)
            }
          case None =>
            false
        }
      case _ =>
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
