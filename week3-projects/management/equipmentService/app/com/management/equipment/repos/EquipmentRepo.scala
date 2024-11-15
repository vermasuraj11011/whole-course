package com.management.equipment.repositories

import com.management.equipment.entity.{Equipment, EquipmentTable}

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val equipment = TableQuery[EquipmentTable]

  def all(): Future[Seq[Equipment]]                = db.run(equipment.result)
  def insert(equipment: Equipment): Future[Int]    = db.run(this.equipment += equipment)
  def findById(id: Int): Future[Option[Equipment]] = db.run(equipment.filter(_.id === id).result.headOption)
  def findByEquipmentDetailId(id: Int): Future[Seq[Equipment]] =
    db.run(equipment.filter(_.equipmentDetailId === id).result)
  def delete(id: Int): Future[Int] = db.run(equipment.filter(_.id === id).delete)

  def findAvailableEquipment: Future[Seq[Equipment]]   = db.run(equipment.filter(_.status === "available").result)
  def findUnAvailableEquipment: Future[Seq[Equipment]] = db.run(equipment.filter(_.status === "unavailable").result)

  def update(equipment: Equipment): Future[Int] = db.run(this.equipment.filter(_.id === equipment.id).update(equipment))
}
