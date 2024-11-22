package com.management.authentication.controllers

import com.management.common.repos.Organization2Repo
import com.management.common.service.Organization2Service
import com.management.common.utils.{DeleteOrganization, PermissionValidation}
import com.management.common.views.{BaseView, Organization2View, ResponseData, ResponseDataList}
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import play.api.libs.json.{Format, Json}

import scala.concurrent.{ExecutionContext, Future}

class Organization2Controller @Inject() (organizationService: Organization2Service, cc: ControllerComponents)(implicit
  ec: ExecutionContext
) extends AbstractController(cc) {

  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  def getAllOrganizations: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(DeleteOrganization)) {
        organizationService
          .getAllOrganizations_admin
          .map { organizations =>
            println(s"Organizations: $organizations")
            val res =
              ResponseDataList[Organization2View](
                data = organizations.toList,
                message = Some("Organizations fetched successfully"),
                status = 200,
                success = true
              )

            println(s"Response: $res")
            println("\n" * 4)

            try {
              res
                .data
                .asInstanceOf[List[Organization2View]]
                .map { organization =>
                  try {
                    println(s"-----------------------------------")
                    println(s"\n" * 4)
                    println(s"Organization: $organization")

                    val j1 = Json.toJson(organization.id)
                    println(s"Serialized Organization ID 1: $j1")
                    val j2 = Json.toJson(organization.head)
                    println(s"Serialized Organization Head 2: $j2")
                    val j3 = Json.toJson(organization.name)
                    println(s"Serialized Organization Name 3: $j3")
                    val j4 = Json.toJson(organization.departments)
                    println(s"Serialized Organization Departments 4: $j4")
                    val j5 = Json.toJson(organization.address)
                    println(s"Serialized Organization Address 5: $j5")
                    val j6 = Json.toJson(organization.phone)
                    println(s"Serialized Organization Phone 6: $j6")
                    val j7 = Json.toJson(organization.website)
                    println(s"Serialized Organization Website 7: $j7")
                    val j8 = Json.toJson(organization.isActive)
                    println(s"Serialized Organization IsActive 8: $j8")
                    val j9 = Json.toJson(organization.isMeetingServiceEnabled)
                    println(s"Serialized Organization IsMeetingServiceEnabled 9: $j9")
                    val j10 = Json.toJson(organization.isEquipmentServiceEnabled)
                    println(s"Serialized Organization IsEquipmentServiceEnabled 10: $j10")

                    import com.management.common.views.Organization2View._

                    val json = Json.toJson(organization)
                    println(s"Serialized Organization: $json")
                  } catch {
                    case e: Exception =>
                      println(s"Error serializing organization: $organization, error: ${e.getMessage}")
                      throw e
                  }
                }

              Ok(Json.toJson(res))
            } catch {
              case e: Exception =>
                println(s"Error: ${e.getMessage}")
                throw e
            }

          }
      } else {
        Future.successful(Forbidden("You do not have permission to view organizations"))
      }
    }
}
