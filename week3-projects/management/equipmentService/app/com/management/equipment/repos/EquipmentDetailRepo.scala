package com.management.equipment.repos

import com.management.equipment.entity.{EquipmentDetail, EquipmentDetailTable}

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentDetailRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val equipmentDetails = TableQuery[EquipmentDetailTable]

  def all(): Future[Seq[EquipmentDetail]]                = db.run(equipmentDetails.result)
  def add(equipmentDetail: EquipmentDetail): Future[Int] = db.run(this.equipmentDetails += equipmentDetail)
  def findById(id: Int): Future[Option[EquipmentDetail]] =
    db.run(equipmentDetails.filter(_.id === id).result.headOption)

  def update(equipmentDetail: EquipmentDetail): Future[Int] =
    db.run(equipmentDetails.filter(_.id === equipmentDetail.id).update(equipmentDetail))
  def delete(id: Int): Future[Int] = db.run(equipmentDetails.filter(_.id === id).delete)

}
