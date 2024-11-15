package com.management.authentication.service

import com.management.common.entity.User
import com.management.common.repos.UserRepo
import jakarta.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthService @Inject() (userRepo: UserRepo)(implicit ec: ExecutionContext) {

  def verifyCreds(email: String, password: String): Future[Option[User]] = userRepo.verifyCreds(email, password)

  def updateUser(user: User): Future[Int] = userRepo.update(user)
}
