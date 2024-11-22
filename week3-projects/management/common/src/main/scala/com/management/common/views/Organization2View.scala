package com.management.common.views

import com.management.common.entity.Organization
import play.api.libs.json.{Json, Writes}

case class Organization2View(
  id: Int,
  name: String,
  head: Option[User2View],
  departments: List[Department2View],
  address: String,
  phone: String,
  website: Option[String],
  isActive: Boolean,
  isMeetingServiceEnabled: Boolean,
  isEquipmentServiceEnabled: Boolean
) extends BaseView {}

case object Organization2View extends BaseViewCompanionObject[Organization2View] {


  implicit lazy val optionOrganization2ViewWrites: Writes[Option[Organization2View]] = Writes.OptionWrites(writes)

  override implicit lazy val writes: Writes[Organization2View] =
    Writes { org =>
      import com.management.common.views.User2View._
      import com.management.common.views.Department2View._
      Json.obj(
        "id"          -> org.id,
        "name"        -> org.name,
        "head"        -> Json.toJson(org.head.map(_.copy(organization = None))),        // Break circular reference
        "departments" -> Json.toJson(org.departments.map(_.copy(organization = None))), // Break circular reference
        "address"     -> org.address,
        "phone"       -> org.phone,
        "website"     -> org.website,
        "isActive"    -> org.isActive,
        "isMeetingServiceEnabled"   -> org.isMeetingServiceEnabled,
        "isEquipmentServiceEnabled" -> org.isEquipmentServiceEnabled
      )
    }

  def fromOrganizationDefault(organization: Organization): Organization2View =
    try {
      println(s"Organization: $organization")
      val view =
        Organization2View(
          id = organization.id,
          name = organization.name,
          head = None,
          departments = List.empty,
          address = organization.address,
          phone = organization.phone,
          website = Some(organization.website),
          isActive = organization.isActive,
          isMeetingServiceEnabled = organization.isMeetingServiceEnabled,
          isEquipmentServiceEnabled = organization.isEquipmentServiceEnabled
        )

      println(s"Organization2View: $view")
      view
    } catch {
      case e: Exception =>
        println(s"Error in converting Organization to Organization2View: ${e.getMessage}")
        throw e
    }
}
