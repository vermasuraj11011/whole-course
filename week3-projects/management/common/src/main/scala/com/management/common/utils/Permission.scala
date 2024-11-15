package com.management.common.utils

sealed trait Permission {
  def perStr: String
}

sealed trait Module extends Permission {
  def moduleStr: String
}

sealed trait UserAuthEnum extends Module {
  override def moduleStr: String = "user_auth_service"
}

sealed trait MeetingServiceEnum extends Module {
  override def moduleStr: String = "meeting_service"
}

sealed trait EquipmentServiceEnum extends Module {
  override def moduleStr: String = "equipment_service"
}

case object CreateUser extends UserAuthEnum {
  override def perStr: String = "create_user"
}

case object ReadUser extends UserAuthEnum {
  override def perStr: String = "read_user"
}

case object UpdateUser extends UserAuthEnum {
  override def perStr: String = "update_user"
}

case object DeleteUser extends UserAuthEnum {
  override def perStr: String = "delete_user"
}

case object CreateOrganization extends UserAuthEnum {
  override def perStr: String = "create_organization"
}

case object ReadOrganization extends UserAuthEnum {
  override def perStr: String = "read_organization"
}

case object UpdateOrganization extends UserAuthEnum {
  override def perStr: String = "update_organization"
}

case object DeleteOrganization extends UserAuthEnum {
  override def perStr: String = "delete_organization"
}

case object CreateDepartment extends UserAuthEnum {
  override def perStr: String = "create_department"
}

case object ReadDepartment extends UserAuthEnum {
  override def perStr: String = "read_department"
}

case object UpdateDepartment extends UserAuthEnum {
  override def perStr: String = "update_department"
}

case object DeleteDepartment extends UserAuthEnum {
  override def perStr: String = "delete_department"
}

case object ReadMeeting extends MeetingServiceEnum {
  override def perStr: String = "read_meeting"
}

case object CreateMeeting extends MeetingServiceEnum {
  override def perStr: String = "create_meeting"
}

case object UpdateMeeting extends MeetingServiceEnum {
  override def perStr: String = "update_meeting"
}

case object DeleteMeeting extends MeetingServiceEnum {
  override def perStr: String = "delete_meeting"
}

case object CreateEquipment extends EquipmentServiceEnum {
  override def perStr: String = "create_equipment"
}

case object ReadEquipment extends EquipmentServiceEnum {
  override def perStr: String = "read_equipment"
}

case object UpdateEquipment extends EquipmentServiceEnum {
  override def perStr: String = "update_equipment"
}

case object DeleteEquipment extends EquipmentServiceEnum {
  override def perStr: String = "delete_equipment"
}

sealed trait Role {
  def roleStr: String
  def permissions: Set[Permission]
}

case object UserRole extends Role {
  override def roleStr: String = "user"
  override def permissions: Set[Permission] =
    Set(ReadUser, ReadOrganization, ReadDepartment, ReadMeeting, ReadEquipment, CreateMeeting)
}

case object DepartmentHead extends Role {
  override def roleStr: String = "department_head"
  override def permissions: Set[Permission] =
    Set(
      CreateUser,
      UpdateUser,
      DeleteUser,
      CreateMeeting,
      UpdateMeeting,
      DeleteMeeting,
      CreateEquipment,
      UpdateEquipment,
      DeleteEquipment
    ) ++ UserRole.permissions
}

case object Admin extends Role {
  override def roleStr: String = "admin"
  override def permissions: Set[Permission] =
    Set(CreateOrganization, UpdateOrganization, CreateDepartment, UpdateDepartment, DeleteDepartment) ++
      DepartmentHead.permissions
}

case object SuperAdmin extends Role {
  override def roleStr: String              = "super_admin"
  override def permissions: Set[Permission] = Set(DeleteOrganization) ++ Admin.permissions
}
