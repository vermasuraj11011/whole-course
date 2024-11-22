package com.management.common.utils

import org.apache.pekko.stream.Materializer
import play.api.http.HttpFilters
import play.api.mvc._
import jakarta.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class JwtAuthenticationFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
    val publicRoutes = Seq("/api/auth/login")

    if (publicRoutes.exists(request.path.startsWith)) {
      nextFilter(request)
    } else {
      val tokenOpt = request.headers.get("Authorization").map(_.replace("Bearer ", ""))
      println(tokenOpt)
      TokenGenerator.validateToken(tokenOpt.getOrElse("")) match {
        case Some(userId) =>
          nextFilter(request)
        case None =>
          Future.successful(Results.Unauthorized("Invalid or missing token"))
      }
    }
  }
}

class Filters @Inject() (jwtAuthFilter: JwtAuthenticationFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(jwtAuthFilter)
}
