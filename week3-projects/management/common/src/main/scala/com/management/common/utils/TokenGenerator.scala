package com.management.common.utils

object TokenGenerator {
  def generateToken(prefix: String): String = {
    val token       = java.util.UUID.randomUUID.toString
    val removeSpace = token.replaceAll(" ", "_")
    s"$removeSpace#$token"
  }
}
