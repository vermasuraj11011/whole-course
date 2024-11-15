package com.management.equipment.repos

import com.management.equipment.entity.{EquipmentAllocation, EquipmentAllocationTable}

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentAllocationRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val equipmentAllocations = TableQuery[EquipmentAllocationTable]

  def all(): Future[Seq[EquipmentAllocation]] = db.run(equipmentAllocations.result)
  def insert(equipmentAllocation: EquipmentAllocation): Future[Int] =
    db.run(this.equipmentAllocations += equipmentAllocation)
  def findById(id: Int): Future[Option[EquipmentAllocation]] =
    db.run(equipmentAllocations.filter(_.id === id).result.headOption)
  def delete(id: Int): Future[Int] = db.run(equipmentAllocations.filter(_.id === id).delete)

  def update(equipmentAllocation: EquipmentAllocation): Future[Int] =
    db.run(this.equipmentAllocations.filter(_.id === equipmentAllocation.id).update(equipmentAllocation))

  def findNotReturnedEquipment: Future[Seq[EquipmentAllocation]] =
    db.run(equipmentAllocations.filter(_.deallocatedDate < System.currentTimeMillis()).result)
}
