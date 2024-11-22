package com.management.authentication.controllers

import com.management.common.repos.UserRepo
import jakarta.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

class User2Controller @Inject() (userRepo: UserRepo, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {}
