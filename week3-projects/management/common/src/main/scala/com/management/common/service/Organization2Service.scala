package com.management.common.service

import com.management.common.entity.{Department, Organization, User}
import com.management.common.repos.{Department2Repo, Organization2Repo, User2Repo}
import com.management.common.utils.FutureUtil
import com.management.common.views.{Department2View, Organization2View, User2View}
import jakarta.inject.{Inject, Singleton}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Organization2Service @Inject() (orgRepo: Organization2Repo, departmentRepo: Department2Repo, userRepo: User2Repo)(
  implicit ec: ExecutionContext
) {

  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  def getAllOrganizations_admin: Future[Seq[Organization2View]] =
    FutureUtil
      .join(orgRepo.getAll, departmentRepo.getAll, userRepo.getAll)
      .map { case (orgs: Seq[Organization], depts: Seq[Department], users: Seq[User]) =>
        orgs.map { org =>
          val groupDepartmentsByOrg = depts.groupBy(_.organizationId)
          val groupUserByDepartment = users.groupBy(_.departmentId)

          val groupUserViewByDepartment: Map[Int, List[User2View]] =
            groupUserByDepartment.map { case (deptId, users) =>
              val usersView = users.map(user => User2View.fromUserDefault(user))
              deptId.get -> usersView
            }

          val groupDepartmentViewByOrg: Map[Int, List[Department2View]] =
            groupDepartmentsByOrg.map { case (orgId, depts) =>
              val deptsView =
                depts.map(dept =>
                  Department2View
                    .fromDepartmentDefault(dept)
                    .copy(users = groupUserViewByDepartment.getOrElse(dept.id, List.empty), head = None)
                )
              orgId -> deptsView
            }

          logger.

          Organization2View
            .fromOrganizationDefault(org)
//            .copy(departments = groupDepartmentViewByOrg.get(org.id), head = None)
            .copy(departments = groupDepartmentViewByOrg.getOrElse(org.id, List.empty), head = None)
        }
      }

}
