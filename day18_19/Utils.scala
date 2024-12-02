package day18_19

object Utils {
  val BUCKET_NAME           = "scala-spark-temp"
  val USER_DATA_PATH        = s"gs://$BUCKET_NAME/transction/CustomerDemographic.csv"
  val TRANSCTION_DATA_PATH  = s"gs://$BUCKET_NAME/transction/Transactions.csv"
  val INPUT_PATH            = s"gs://$BUCKET_NAME/spark/input"
  val OUTPUT_PATH           = s"gs://$BUCKET_NAME/spark/output"
  Val CHECK_POINT_PATH      = s"gs://$BUCKET_NAME/spark/checkpoints"
}