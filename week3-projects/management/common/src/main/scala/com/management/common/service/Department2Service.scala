package com.management.common.service

import com.management.common.repos.Department2Repo
import com.management.common.views.{Department2View, Organization2View}
import jakarta.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class Department2Service @Inject() (departmentRepo: Department2Repo)(implicit ec: ExecutionContext) {}
