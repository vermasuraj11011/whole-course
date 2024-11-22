package com.management.common.views
import com.management.common.entity.User
import play.api.libs.json.{Json, Writes}

case class User2View(
  id: Int,
  email: String,
  password: String,
  name: String,
  role: String,
  department: Option[Department2View],
  organization: Option[Organization2View],
  isActive: Boolean
) extends BaseView {}

object User2View extends BaseViewCompanionObject[User2View] {
  implicit val writes: Writes[User2View] = Json.writes[User2View]

  implicit lazy val listUser2ViewWrites: Writes[List[User2View]] = Writes.list(writes)

  implicit lazy val optionUser2ViewWrites: Writes[Option[User2View]] = Writes.OptionWrites(writes)

  implicit val writes2: Writes[User2View] =
    Writes { user =>
      Json.obj(
        "id"       -> user.id,
        "email"    -> user.email,
        "password" -> user.password,
        "name"     -> user.name,
        "role"     -> user.role,
        "department" -> List.empty[Department2View],
        "organization" -> None,
        "isActive"     -> user.isActive
      )
    }

  def fromUserDefault(user: User): User2View =
    try {
      println(s"User: $user")
      val view =
        User2View(
          id = user.id,
          email = user.email,
          password = user.password,
          name = user.name,
          role = user.role,
          department = None,
          organization = None,
          isActive = user.isActive
        )
      println(s"User2View: $view")
      view
    } catch {
      case e: Exception =>
        println(s"Error in converting User to User2View: ${e.getMessage}")
        throw e
    }
}
