package models

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class Temperature(id: Long, timestamp: Timestamp, temperature: Float) {

  def temperatureString = f"$temperature%2.1f"

  def timeString = new SimpleDateFormat("HH:mm").format(timestamp)

}

class TemperatureRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.driver.api._

  private val Temperatures = TableQuery[TemperatureTable]

  db.run(Temperatures.schema.create)

  def all: Future[List[Temperature]] =
    db.run(Temperatures.to[List].result)

  def last: Future[Temperature] = db.run(Temperatures.sortBy(_.timestamp.desc).take(1).result.head)

  def add(temp: Float): Future[Long] = {
    val temperature = Temperature(0, java.sql.Timestamp.valueOf(LocalDateTime.now()), temp)
    db.run(Temperatures returning Temperatures.map(_.id) += temperature)
  }

  private class TemperatureTable(tag: Tag) extends Table[Temperature](tag, "TEMPERATURES") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def timestamp = column[Timestamp]("TIMESTAMP")

    def temperature = column[Float]("TEMPERATURE")

    def * = (id, timestamp, temperature) <> (Temperature.tupled, Temperature.unapply)

    def ? = (id.?, timestamp.?, temperature.?).shaped.<>({ r => import r._; _1.map(_ => Temperature.tupled((_1.get, _2.get, _3.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }

}