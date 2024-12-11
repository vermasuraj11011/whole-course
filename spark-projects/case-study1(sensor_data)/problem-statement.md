# Case Study: Real-Time IoT Data Processing and Aggregation with Visualization

##  Problem Statement
You are tasked with building an end-to-end real-time IoT data processing pipeline. The system must ingest, process, and store high-frequency sensor readings, provide aggregated metrics for dashboard visualization, and manage storage efficiently. The pipeline will include Akka for data generation, Kafka for message brokering, Spark for streaming and batch processing, and Google Cloud Storage (GCS) for storage. The aggregated data must be incrementally updated to reflect new incoming data, and a simple API should expose the aggregated results in JSON format for visualization on a webpage.

## Case Study Tasks
1. <b style="color: #79e5e5"> Data Generation (Akka Producer) </b> <br>
   
   Implement an Akka producer to generate random sensor readings every 100 milliseconds with fields: <br>
   * sensorId: A unique identifier for each sensor.
   * timestamp: The time at which the reading is generated.
   * temperature: A random float value between -50 and 150.
   * humidity: A random float value between 0 and 100. <br>
   Publish the generated readings to a Kafka topic named sensor-readings.
2. <b style="color: #79e5e5"> Message Brokering (Kafka) </b> <br>
   Set up Kafka as the message broker with a topic named sensor-readings. Ensure sufficient partitions for handling high-frequency data. Validate that Kafka receives data from the Akka producer without loss.
3. <b style="color: #79e5e5"> Data Validation and Storage (Spark Streaming + GCS)</b> <br>
   
   Write a Spark Streaming job to:
   * Consume messages from the sensor-readings Kafka topic.
   * Validate the readings: discard records where temperature or humidity values are outside valid ranges.
   * Serialize valid readings into Protobuf format.
   * Store the serialized data in GCS, partitioned by timestamp (gs://your-bucket/raw/sensor-data/yyyy/MM/dd/HH/).
4. <b style="color: #79e5e5"> Incremental Aggregation (Spark Batch)</b> <br>

   Develop a Spark Batch job to:
   * Read raw Protobuf files from gs://your-bucket/raw/sensor-data/yyyy/MM/dd/HH/.
   * Merge new data with existing aggregated Protobuf files in gs://your-bucket/aggregated/protobuf/yyyy/MM/dd/HH/:
   * Recompute metrics, such as weighted averages, min/max values, and anomaly counts.
   * Overwrite the updated aggregated results in Protobuf format in the same location.
   * Generate a JSON representation of the aggregated results for dashboard consumption, and store it in gs://your-bucket/aggregated/json/yyyy/MM/dd/HH/.

5. <b style="color: #79e5e5"> Data Retention and Cleanup</b> <br>
   
   Automate a retention policy to:
   * Delete raw Protobuf files from gs://your-bucket/raw/sensor-data/yyyy/MM/dd/HH/ older than 7 days.
   * Retain only the latest aggregated Protobuf files in gs://your- bucket/aggregated/protobuf/yyyy/MM/dd/HH/.
6. <b style="color: #79e5e5"> Visualization (Akka API + Webpage) </b>
    
    ### Akka API:
    Create an Akka HTTP-based API with endpoints:
    * GET /api/aggregated-data: Fetch the latest aggregated data in JSON format.
    * GET /api/aggregated-data/:sensorId: Fetch aggregated data for a specific sensor. Ensure the API reads from GCS and serves JSON data.
    ### Webpage:
    Create a simple webpage to consume the API and display the aggregated metrics: - Show metrics in a table (sensor ID, averages, min/max values, anomaly counts). - Include a search bar to filter results by sensorId.
    * Provide a refresh button to update the data from the API.
  
## Deliverables
1. Code:
      * Akka producer for data generation.
      * Spark Streaming job for data validation and storage.
      * Spark Batch job for incremental aggregation.
      * Akka API to expose aggregated JSON data.
      * Protobuf schema definitions for raw and aggregated data.
2. Architecture Diagram:
      * A diagram illustrating the interaction between Akka, Kafka, Spark, GCS, and the dashboard.
3. Testing:
      * Test cases for each component (data validation, API responses, merging logic).
4. Visualization:
      * A basic webpage demonstrating aggregated data. (consume the API)
