package com.management.common.exceptions

case class PermissionDeniedException(entity: String) extends Exception(s"You do not have permission to view $entity")
