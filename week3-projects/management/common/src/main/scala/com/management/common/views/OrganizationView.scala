package com.management.common.views

import com.management.common.entity.{Organization, User}
import play.api.libs.json.{Format, Json}

case class OrganizationView(
  id: Int,
  name: String,
  head_id: UserView,
  address: String,
  phone: String,
  website: String,
  isActive: Boolean,
  isMeetingServiceEnabled: Boolean,
  isEquipmentServiceEnabled: Boolean
) {}

object OrganizationView {
  implicit val format: Format[OrganizationView] = Json.format[OrganizationView]

  def fromOrganization(organization: Organization, head: UserView): OrganizationView =
    OrganizationView(
      id = organization.id,
      name = organization.name,
      head_id = head,
      address = organization.address,
      phone = organization.phone,
      website = organization.website,
      isActive = organization.isActive,
      isMeetingServiceEnabled = organization.isMeetingServiceEnabled,
      isEquipmentServiceEnabled = organization.isEquipmentServiceEnabled
    )
}
