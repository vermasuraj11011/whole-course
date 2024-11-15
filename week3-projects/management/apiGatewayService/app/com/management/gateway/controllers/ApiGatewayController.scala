package com.management.gateway.controllers

import com.management.common.repos.{OrganizationRepo, UserRepo}
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.util.ByteString
import play.api.mvc._
import play.api.libs.ws._
import play.api.http.HttpEntity
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiGatewayController @Inject() (
  val controllerComponents: ControllerComponents,
  wsClient: WSClient,
  config: Configuration,
  userRepo: UserRepo,
  organizationRepo: OrganizationRepo
)(implicit ec: ExecutionContext)
  extends BaseController {

  private val serviceUrls: Map[String, String] =
    Map(
      "auth"         -> config.get[String]("services.authService"),
      "meeting"      -> config.get[String]("services.meetingService"),
      "equipment"    -> config.get[String]("services.equipmentService"),
      "notification" -> config.get[String]("services.notificationService")
    )

  def forwardRequest(path: String): Action[AnyContent] =
    Action.async { request =>
      println(s"gateway request: $request")
      println(s"gateway request body: ${request.body}")
      val serviceName = path.split("/").headOption.getOrElse("")
      println(s"Service name: $serviceName")

      serviceUrls.get(serviceName) match {
        case Some(serviceUrl) =>
          val newUrl = s"$serviceUrl/$path"
          println(s"New URL: $newUrl")

          if (path == "auth/login") {
            println("Processing request without headers")
            processRequestWithoutHeaders(request, newUrl)
          } else {
            println("Processing request with headers")
            processRequest(request, newUrl)
          }

        case None =>
          Future.successful(NotFound(s"Service not found for path: $path"))
      }
    }

  private def processRequest(request: Request[AnyContent], url: String): Future[Result] =
    extractHeaders(request).flatMap { headers =>
      println("Processing request 1")

      val wsRequest =
        wsClient
          .url(url)
          .withMethod(request.method)
          .withHttpHeaders(headers: _*)
          .withQueryStringParameters(
            request
              .queryString
              .flatMap { case (key, values) =>
                println(s"Key: $key, Values: $values")
                values.map(key -> _)
              }
              .toSeq: _*
          )

      // Handle the body if present
      val futureResponse =
        request.body match {
          case AnyContentAsJson(json) =>
            println("Forwarding JSON body")
            wsRequest.withBody(json).stream()

          case AnyContentAsFormUrlEncoded(form) =>
            println("Forwarding FormUrlEncoded body")
            wsRequest.withBody(form).stream()

          case AnyContentAsText(text) =>
            println("Forwarding Text body")
            wsRequest.withBody(text).stream()

          case AnyContentAsRaw(raw) =>
            println("Forwarding Raw body")
            wsRequest.withBody(raw.asBytes().getOrElse(ByteString.empty)).stream()

          case AnyContentAsXml(xml) =>
            println("Forwarding XML body")
            wsRequest.withBody(xml).stream()

//          case AnyContentAsMultipartFormData(data) =>
//            println("Forwarding MultipartFormData body")
//            // Convert multipart data as needed
//            val parts =
//              data
//                .files
//                .map { file =>
//                  // Handle file processing if needed
//                  WSBodyPart(file.filename.getOrElse("file"), file.ref, contentType = file.contentType)
//                }
//            wsRequest.withBody(Source(parts)).stream()

          case _ =>
            println("No body or unsupported body type")
            wsRequest.stream()
        }

      futureResponse
        .map { response =>
          println("Processing request 2: response " + response)
          val s = Status(response.status).sendEntity(HttpEntity.Streamed(response.bodyAsSource, None, None))
          println("Processing request 3 s: " + s)
          s
        }
        .recover { case e: Exception =>
          InternalServerError(s"Failed to forward request: ${e.getMessage}")
        }
    }

  private def processRequestWithoutHeaders(request: Request[AnyContent], url: String): Future[Result] =
    wsClient
      .url(url)
      .withMethod(request.method)
      .withHttpHeaders(request.headers.toSimpleMap.toSeq: _*)
      .withQueryStringParameters(
        request
          .queryString
          .flatMap { case (key, values) =>
            values.map(key -> _)
          }
          .toSeq: _*
      )
      .stream()
      .map { response =>
        println("Processing request without headers 2")
        val s = Status(response.status).sendEntity(HttpEntity.Streamed(response.bodyAsSource, None, None))
        println("Processing request without headers 3")
        s
      }
      .recover { case e: Exception =>
        InternalServerError(s"Failed to forward request: ${e.getMessage}")
      }

  private def extractHeaders(request: Request[AnyContent]): Future[Seq[(String, String)]] =
    request.headers.get("key") match {
      case Some(token) =>
        println(s"Token: $token")
        userRepo
          .findByToken(token)
          .flatMap {
            case Some(user) =>
              println(s"User: $user")
              organizationRepo
                .find(user.organizationId)
                .map {
                  case Some(organization) =>
                    println(s"Organization: $organization")
                    Seq(
                      "user-id"                      -> user.id.toString,
                      "department-id"                -> user.departmentId.toString,
                      "organization-id"              -> user.organizationId.toString,
                      "is-meeting-service-enabled"   -> organization.isMeetingServiceEnabled.toString,
                      "is-equipment-service-enabled" -> organization.isEquipmentServiceEnabled.toString,
                      "role"                         -> user.role
                    )
                  case None =>
                    println("Organization not found")
                    throw new RuntimeException("Organization not found")
                }
            case None =>
              println("User not found")
              throw new RuntimeException("User not found")
          }
          .recover { case e: Throwable =>
            println(s"Failed to extract headers: ${e.getMessage}")
            Seq("error" -> s"Failed to extract headers: ${e.getMessage}")
          }
      case None =>
        println("Token not found")
        Future.successful(Seq.empty)
    }
}
