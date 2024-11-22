package com.management.common.service

import com.management.common.repos.Organization2Repo
import com.management.common.views.Organization2View
import jakarta.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrganizationService @Inject() (orgRepo: Organization2Repo)(implicit ec: ExecutionContext) {

//  def getAllOrganizations: Future[Seq[Organization2View]] = {
//    val organizations = orgRepo.getAll
//    organizations.map(_.map(Organization2View.fromOrganization))
//  }

}
