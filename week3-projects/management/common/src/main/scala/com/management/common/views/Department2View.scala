package com.management.common.views
import com.management.common.entity.Department
import play.api.libs.json.{Json, Writes}

case class Department2View(
  id: Int,
  name: String,
  users: List[User2View] = List.empty,
  organization: Option[Organization2View],
  isActive: Boolean,
  head: Option[User2View] = None
) extends BaseView

object Department2View extends BaseViewCompanionObject[Department2View] {

  implicit lazy val listDepartment2ViewWrites: Writes[List[Department2View]] = Writes.list(writes)

  implicit lazy val optionDepartment2ViewWrites: Writes[Option[Department2View]] = Writes.OptionWrites(writes)

  override implicit lazy val writes: Writes[Department2View] =
    Writes { dept =>
      Json.obj(
        "id"   -> dept.id,
        "name" -> dept.name,
        "users"        -> Json.toJson(dept.users.map(_.copy(department = None))), // Break circular reference
        "organization" -> dept.organization,
        "isActive"     -> dept.isActive,
        "head"         -> Json.toJson(dept.head.map(_.copy(department = None)))   // Break circular reference
      )
    }

  def fromDepartmentDefault(department: Department): Department2View =
    try {
      println(s"Department: $department")
      val view =
        Department2View(
          id = department.id,
          name = department.name,
          users = List.empty,
          organization = None,
          isActive = department.isActive,
          head = None
        )
      println(s"Department2View: $view")
      view
    } catch {
      case e: Exception =>
        println(s"Error in converting Department to Department2View: ${e.getMessage}")
        throw e
    }
}
