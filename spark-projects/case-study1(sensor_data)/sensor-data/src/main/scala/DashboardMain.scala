import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.{DELETE, GET, OPTIONS, POST, PUT}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.stream.Materializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, SparkSession}

/** Entry point for the Aggregated Data API.
  */
object DashboardMain {

  // Implicit system components required for Akka HTTP
  implicit val actorSystem: ActorSystem                   = ActorSystem("DashboardAPI")
  implicit val materializer: Materializer                 = Materializer(actorSystem)
  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

  // Path to the JSON files containing sensor metrics
  private val jsonAggregatedDataPath = "gs://scala-spark-temp/sensor-reading/aggregate/json"

  // Initialize Spark session
  private val sparkSession: SparkSession = SensorDataProcessor.initializeSparkSession()

  // CORS settings for the API
  private val corsConfig = CorsSettings.defaultSettings.withAllowedMethods(Seq(GET, POST, PUT, DELETE, OPTIONS))

  // API route definitions
  private val apiRoutes: Route =
    cors(corsConfig) {
      options {
        complete(StatusCodes.OK)
      } ~
        concat(
          path("api" / "aggregated-data") {
            get {
              complete(HttpEntity(ContentTypes.`application/json`, fetchAggregatedData(sparkSession)))
            }
          },
          path("api" / "aggregated-data" / Segment) { sensorId =>
            get {
              complete(
                HttpEntity(ContentTypes.`application/json`, fetchAggregatedDataBySensorId(sparkSession, sensorId))
              )
            }
          }
        )
    }

  /** Main method to start the HTTP server.
    * @param args
    *   Command-line arguments (unused).
    */
  def main(args: Array[String]): Unit =
    Http()
      .newServerAt("localhost", 8080)
      .bind(apiRoutes)
      .onComplete {
        case Success(binding) =>
          println(s"Server listening on  ${binding.localAddress}")
        case Failure(exception) =>
          println(s"Failed to bind server: ${exception.getMessage}")
          actorSystem.terminate()
      }

  /** Fetch all aggregated data from the latest folder.
    * @param sparkSession
    *   The Spark session used for data processing.
    * @return
    *   JSON string of aggregated data.
    */
  private def fetchAggregatedData(sparkSession: SparkSession): String =
    findLatestDataFolder(sparkSession) match {
      case Some(folderPath) =>
        loadDataFromFolder(sparkSession, folderPath).toJSON.collect().mkString("[", ",", "]")
      case None =>
        "[]"
    }

  /** Fetch aggregated data for a specific sensor by ID.
    * @param sparkSession
    *   The Spark session used for data processing.
    * @param sensorId
    *   ID of the sensor.
    * @return
    *   JSON string of the filtered aggregated data.
    */
  private def fetchAggregatedDataBySensorId(sparkSession: SparkSession, sensorId: String): String = {
    import sparkSession.implicits._
    findLatestDataFolder(sparkSession) match {
      case Some(folderPath) =>
        println(s"Fetching data for sensor $sensorId from $folderPath")
        loadDataFromFolder(sparkSession, folderPath)
          .filter($"sensorId" === sensorId)
          .toJSON
          .collect()
          .mkString("[", ",", "]")
      case None =>
        "[]"
    }
  }

  /** Locate the path of the latest data folder based on year, month, day, and hour hierarchy.
    * @param sparkSession
    *   The Spark session used for file system interaction.
    * @return
    *   Optional path to the latest data folder.
    */

  private def findLatestDataFolder(sparkSession: SparkSession): Option[String] = {
    val hdfs          = FileSystem.get(sparkSession.sparkContext.hadoopConfiguration)
    val baseDirectory = new Path(jsonAggregatedDataPath)

    def getLatestSubdirectory(parentPath: Path): Option[String] =
      Option(hdfs.listStatus(parentPath)).filter(_.nonEmpty).map(_.map(_.getPath.getName.toInt).max.formatted("%02d"))

    if (!hdfs.exists(baseDirectory))
      None
    else {
      getLatestSubdirectory(baseDirectory).flatMap(latestYear =>
        getLatestSubdirectory(new Path(s"$jsonAggregatedDataPath/$latestYear")).flatMap(latestMonth =>
          getLatestSubdirectory(new Path(s"$jsonAggregatedDataPath/$latestYear/$latestMonth")).flatMap(latestDay =>
            getLatestSubdirectory(new Path(s"$jsonAggregatedDataPath/$latestYear/$latestMonth/$latestDay"))
              .map(latestHour => s"$jsonAggregatedDataPath/$latestYear/$latestMonth/$latestDay/$latestHour")
          )
        )
      )
    }
  }

  /** Load data from a given folder path as a DataFrame.
    * @param sparkSession
    *   The Spark session used for reading data.
    * @param folderPath
    *   Path to the folder containing data files.
    * @return
    *   DataFrame of the loaded data.
    */
  private def loadDataFromFolder(sparkSession: SparkSession, folderPath: String): DataFrame =
    sparkSession.read.format("json").load(folderPath)
}
