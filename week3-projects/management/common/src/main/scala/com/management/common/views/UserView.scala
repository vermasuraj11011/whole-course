package com.management.common.views

import com.management.common.entity.User

case class UserView(
  id: Int,
  email: String,
  name: String,
  role: String,
  department: DepartmentView,
  organization: OrganizationView,
  isActive: Boolean,
  token: String
) {

}

object UserView {
  import play.api.libs.json.{Format, Json}
  implicit val format: Format[UserView] = Json.format[UserView]

  def fromUser(user: User, department: DepartmentView, org: OrganizationView): UserView =
    UserView(
      id = user.id,
      email = user.email,
      name = user.name,
      role = user.role,
      department = department,
      organization = org,
      isActive = user.isActive,
      token = user.token
    )

}
