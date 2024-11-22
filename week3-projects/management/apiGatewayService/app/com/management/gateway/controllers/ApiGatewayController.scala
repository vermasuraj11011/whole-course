package com.management.gateway.controllers

import com.management.common.repos.{OrganizationRepo, UserRepo}
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.util.ByteString
import play.api.mvc._
import play.api.libs.ws._
import play.api.http.HttpEntity
import play.api.Configuration
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiGatewayController @Inject() (
  val controllerComponents: ControllerComponents,
  wsClient: WSClient,
  wsRequest: StandaloneAhcWSClient,
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

  def processRequest(request: Request[AnyContent], url: String): Future[Result] = {
    println("Processing private api")

    extractHeaders(request).flatMap { headers =>
      println(s"processing request for user ${headers.find(_._1 == "user-id").get._2}")

      request.method match {
        case "GET" | "DELETE" =>
          wsRequest
            .url(url)
            .withMethod(request.method)
            .withHttpHeaders(headers: _*)
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
              Status(response.status).sendEntity(HttpEntity.Streamed(response.bodyAsSource, None, None))
            }
            .recover { case e: Exception =>
              InternalServerError(s"Failed to forward request: ${e.getMessage}")
            }

        case "POST" | "PUT" =>
          request.body.asJson match {
            case Some(jsonBody) =>
              wsRequest
                .url(url)
                .withMethod(request.method)
                .withHttpHeaders(headers: _*)
                .withBody(jsonBody)
                .stream()
                .map { response =>
                  Status(response.status).sendEntity(HttpEntity.Streamed(response.bodyAsSource, None, None))
                }
                .recover { case e: Exception =>
                  InternalServerError(s"Failed to forward request: ${e.getMessage}")
                }
            case None =>
              Future.successful(BadRequest("Request body is not valid JSON"))
          }
      }
    }
  }

  private def processRequest1(request: Request[AnyContent], url: String): Future[Result] =
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

  def bodyWritableForAnyContent(body: AnyContent): Option[BodyWritable[AnyContent]] =
    body match {
      case AnyContentAsJson(json) =>
        Some(BodyWritable(_ => InMemoryBody(ByteString.fromString(json.toString)), "application/json"))
      case AnyContentAsText(text) =>
        Some(BodyWritable(_ => InMemoryBody(ByteString.fromString(text)), "text/plain"))
      case AnyContentAsXml(xml) =>
        Some(BodyWritable(_ => InMemoryBody(ByteString.fromString(xml.toString)), "application/xml"))
      case AnyContentAsRaw(raw) =>
        raw.asBytes().map(bytes => BodyWritable(_ => InMemoryBody(bytes), "application/octet-stream"))
      case _ =>
        None
    }

//  private def processRequestWithoutHeaders(request: Request[AnyContent], url: String): Future[Result] = {
//    val wsRequest =
//      wsClient
//        .url(url)
//        .withHttpHeaders(request.headers.toSimpleMap.toSeq: _*) // Forward headers
//        .withQueryStringParameters(
//          request
//            .queryString
//            .flatMap { case (key, values) =>
//              values.map(key -> _)
//            }
//            .toSeq: _* // Forward query parameters
//        )
//
//    request.method match {
//      case "GET" | "DELETE" =>
//        // Handle GET and DELETE (no body is sent for these)
//        wsRequest
//          .withMethod(request.method)
//          .stream()
//          .map { response =>
//            Status(response.status).sendEntity(HttpEntity.Streamed(response.bodyAsSource, None, None))
//          }
//          .recover { case e: Exception =>
//            InternalServerError(s"Failed to forward request: ${e.getMessage}")
//          }
//
//      case "POST" | "PUT" =>
//        // Handle POST and PUT (body is sent if valid JSON is present)
//        request.body.asJson match {
//          case Some(jsonBody) =>
//            wsRequest
//              .withMethod(request.method)
//              .withBody(jsonBody) // Send the JSON body
//              .stream()
//              .map { response =>
//                Status(response.status).sendEntity(HttpEntity.Streamed(response.bodyAsSource, None, None))
//              }
//              .recover { case e: Exception =>
//                InternalServerError(s"Failed to forward request: ${e.getMessage}")
//              }
//          case None =>
//            Future.successful(BadRequest("Request body is not valid JSON"))
//        }
//
//      case other =>
//        Future.successful(MethodNotAllowed(s"Unsupported HTTP method: $other"))
//    }
//  }

  private def processRequestWithoutHeaders(request: Request[AnyContent], url: String): Future[Result] =
    request.body.asJson match {
      case Some(jsonBody) =>
        wsClient
          .url(url)
          .post(jsonBody)
          .map { response =>
            Status(response.status).sendEntity(HttpEntity.Streamed(response.bodyAsSource, None, None))
          }
          .recover { case e: Exception =>
            InternalServerError(s"Failed to forward request: ${e.getMessage}")
          }
      case None =>
        Future.successful(BadRequest("Request body is not valid JSON"))
    }

  private def processRequestWithoutHeaders1(request: Request[AnyContent], url: String): Future[Result] = {
    val wsRequest =
      wsClient
        .url(url)
        .withMethod(request.method)
        .withHttpHeaders(request.headers.toSimpleMap.toSeq: _*)
        .withQueryStringParameters(
          request
            .queryString
            .flatMap { case (key, values) =>
              values.map(value => key -> value)
            }
            .toSeq: _*
        )

    bodyWritableForAnyContent(request.body) match {
      case Some(writable) =>
        val finalReq = wsRequest.withBody(request.body)(writable)

        finalReq
          .stream()
          .map { response =>
            val s = Status(response.status).sendEntity(HttpEntity.Streamed(response.bodyAsSource, None, None))
            s
          }
          .recover { case e: Exception =>
            InternalServerError(s"Failed to forward request: ${e.getMessage}")
          }
      case None =>
        Future.successful(InternalServerError("Unsupported body content type"))
    }
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
                      "department-id"                -> user.departmentId.get.toString,
                      "organization-id"              -> user.organizationId.toString,
                      "is-meeting-service-enabled"   -> organization.isMeetingServiceEnabled.toString,
                      "is-equipment-service-enabled" -> organization.isEquipmentServiceEnabled.toString,
                      "role"                         -> user.role,
                      "email"                        -> user.email,
                      "key"                          -> token
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
