package com.management.common.utils

import java.time.{Instant, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

object DateUtils {
  def convertTimestampToHumanReadableDate(timestamp: Long): String = {
    val instant       = Instant.ofEpochMilli(timestamp)
    val zoneId        = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
    val formatter     = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
    zonedDateTime.format(formatter)
  }
}
