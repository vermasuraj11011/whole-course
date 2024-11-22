package com.management.authentication.controllers

import com.management.common.entity.Organization
import com.management.common.repos.OrganizationRepo
import com.management.common.utils.{CreateOrganization, PermissionValidation, ReadOrganization, UpdateOrganization}
import play.api.libs.json.{Format, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import jakarta.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class OrganizationController @Inject() (organizationRepo: OrganizationRepo, cc: ControllerComponents)(implicit
  ec: ExecutionContext
) extends AbstractController(cc) {

  implicit val organizationFormat: Format[Organization] = Json.format[Organization]

  def getOrgDetail(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(ReadOrganization)) {
        organizationRepo
          .find(id)
          .map {
            case Some(organization) =>
              Ok(Json.toJson(organization))
            case None =>
              NotFound
          }
      } else {
        Future.successful(Forbidden("You do not have permission to view this organization"))
      }
    }

  def getListOfOrgs: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(ReadOrganization)) {
        organizationRepo
          .list()
          .map { organizations =>
            Ok(Json.toJson(organizations))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to view organizations"))
      }
    }

  def createOrg: Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(CreateOrganization)) {
        val organization = request.body.asJson.get.as[Organization]
        organizationRepo
          .add(organization)
          .map { id =>
            Ok(Json.toJson(id))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to create organizations"))
      }
    }

  def updateOrg(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(UpdateOrganization)) {
        val organization = request.body.asJson.get.as[Organization]
        if (organization.id != id) {
          Future.successful(BadRequest("Invalid organization id"))
        } else {
          organizationRepo
            .update(organization)
            .map { id =>
              Ok(Json.toJson(id))
            }
        }
      } else {
        Future.successful(Forbidden("You do not have permission to update organizations"))
      }
    }

  def deleteOrg(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      if (PermissionValidation.validate(UpdateOrganization)) {
        organizationRepo
          .delete(id)
          .map { id =>
            Ok(Json.toJson(id))
          }
      } else {
        Future.successful(Forbidden("You do not have permission to delete organizations"))
      }
    }
}
