package com.management.common.service

import com.management.common.entity.User
import com.management.common.repos.{DepartmentRepo, OrganizationRepo, UserRepo}
import com.management.common.request.UserReq
import com.management.common.views.{DepartmentView, OrganizationView, UserView}
import jakarta.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject() (userRepo: UserRepo, orgRep: OrganizationRepo, deptRepo: DepartmentRepo)(implicit
  ec: ExecutionContext
) {

  def getUserEntity(id: Int): Future[Option[User]] = userRepo.find(id)

  def getUser(id: Int): Future[Option[UserView]] =
    userRepo
      .find(id)
      .flatMap {
        case Some(user) =>
          val orgFuture  = getOrganization(user.organizationId)
          val deptFuture = getDepartment(user.departmentId.get)

          for {
            orgOpt  <- orgFuture
            deptOpt <- deptFuture
          } yield for {
            org  <- orgOpt
            dept <- deptOpt
          } yield UserView.fromUser(user, dept, org)

        case None =>
          Future.successful(None)
      }

  def getAllUsers: Future[Seq[UserView]] =
    userRepo
      .list()
      .flatMap { users =>
        val orgFutures  = users.map(user => getOrganization(user.organizationId))
        val deptFutures = users.map(user => getDepartment(user.departmentId.get))

        val orgFuture  = Future.sequence(orgFutures)
        val deptFuture = Future.sequence(deptFutures)

        for {
          orgs  <- orgFuture
          depts <- deptFuture
        } yield users
          .zip(orgs.zip(depts))
          .flatMap { case (user, (orgOpt, deptOpt)) =>
            (orgOpt, deptOpt) match {
              case (Some(org), Some(dept)) =>
                Some(UserView.fromUser(user, dept, org))
              case _ =>
                None
            }
          }
      }

  def getUsersEntity(ids: List[Int]): Future[Seq[User]] = userRepo.findByIds(ids)

  def getUsers(ids: List[Int]): Future[Seq[UserView]] =
    userRepo
      .findByIds(ids)
      .flatMap { users =>
        val orgFutures  = users.map(user => getOrganization(user.organizationId))
        val deptFutures = users.map(user => getDepartment(user.departmentId.get))

        val orgFuture  = Future.sequence(orgFutures)
        val deptFuture = Future.sequence(deptFutures)

        for {
          orgs  <- orgFuture
          depts <- deptFuture
        } yield users
          .zip(orgs.zip(depts))
          .flatMap { case (user, (orgOpt, deptOpt)) =>
            (orgOpt, deptOpt) match {
              case (Some(org), Some(dept)) =>
                Some(UserView.fromUser(user, dept, org))
              case _ =>
                None
            }
          }
      }

  def getOrganization(id: Int): Future[Option[OrganizationView]] =
    orgRep
      .find(id)
      .flatMap {
        case Some(org) =>
          Future.successful(Some(OrganizationView.fromOrganization(org, null)))
        case None =>
          Future.successful(None)
      }

  def getDepartment(id: Int): Future[Option[DepartmentView]] =
    deptRepo
      .find(id)
      .flatMap {
        case Some(dept) =>
          Future.successful(Some(DepartmentView.fromDepartment(dept)))
        case None =>
          Future.successful(None)
      }

  def createUser(userReq: UserReq): Future[UserView] = {
    val user =
      User(
        id = 1,
        email = userReq.email,
        password = userReq.password,
        name = userReq.name,
        role = "user",
        departmentId = Some(userReq.departmentId),
        organizationId = userReq.organizationId,
        isActive = true,
        token = userReq.token
      )

    userRepo.add(user).map(id => UserView.fromUser(user.copy(id = id), null, null))
  }
}
