package producer

case class TemperatureSensorEntity(sensorId: String, timestamp: String, temperature: Float, humidity: Float)

object TemperatureSensorEntity {

  def generateTempSensorReading: TemperatureSensorEntity = {
    val sensorId    = "sensor-" + (1 + scala.util.Random.nextInt(100))
    val timestamp   = System.currentTimeMillis().toString
    val temperature = -50 + scala.util.Random.nextFloat() * 200
    val humidity    = scala.util.Random.nextFloat() * 100
    TemperatureSensorEntity(sensorId, timestamp, temperature, humidity)
  }
}
