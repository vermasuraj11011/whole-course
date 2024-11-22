package com.management.common.utils

//object TokenGenerator {
//  def generateToken(prefix: String): String = {
//    val token       = java.util.UUID.randomUUID.toString
//    val removeSpace = prefix.replaceAll(" ", "_")
//    s"$removeSpace#$token"
//  }
//}

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.util.Date

object TokenGenerator {
  private val secretKey = sys.env.getOrElse("JWT_SECRET", throw new Exception("JWT_SECRET not found"))
  private val algorithm = Algorithm.HMAC256(secretKey)
  private val issuer    = "play-name"

  // Generate a JWT token
  def generateToken(userId: String, expirationMillis: Long = 3600000): String = {
    val now = System.currentTimeMillis()
    JWT
      .create()
      .withIssuer(issuer)
      .withSubject(userId)
      .withIssuedAt(new Date(now))
      .withExpiresAt(new Date(now + expirationMillis))
      .sign(algorithm)
  }

  def validateToken(token: String): Option[String] =
    try {
      val verifier   = JWT.require(algorithm).withIssuer(issuer).build()
      val decodedJWT = verifier.verify(token)
      print(decodedJWT.getSubject)
      Some(decodedJWT.getSubject)
    } catch {
      case _: JWTVerificationException =>
        None
    }
}
