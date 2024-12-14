---

# Streaming Sensor Data Processing and Dashboard

This application simulates a real-time **sensor data pipeline** using **Kafka**, **Apache Spark Streaming**, **Google Cloud Storage (GCS)**, and **Akka HTTP**. It processes temperature and humidity sensor data, performs incremental aggregation, and exposes an API to visualize the aggregated results.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Application Flow](#application-flow)
3. [Components](#components)
    - KafkaProducerMain
    - SensorDataProcessor
    - DashboardMain
4. [Setup Instructions](#setup-instructions)
5. [Running the Application](#running-the-application)

---

## Project Overview

This project is divided into three main components:

1. **`KafkaProducerMain`**: Produces synthetic temperature and humidity sensor data and sends it to a Kafka topic.
2. **`SensorDataProcessor`**: Consumes sensor data from Kafka, processes it using Spark Streaming, and saves raw and
   aggregated data to Google Cloud Storage (GCS).
3. **`DashboardMain`**: Reads aggregated sensor data from GCS and exposes REST APIs to serve data for a frontend
   dashboard.

---

## Application Flow

1. **Data Generation**:
    - The `KafkaProducerMain` generates synthetic sensor data every second.
    - Data includes `sensorId`, `timestamp`, `temperature`, and `humidity`.
    - This data is serialized using **Protobuf** and published to a Kafka topic (`sensor-readings`).

2. **Data Processing**:
    - The `SensorDataProcessor` reads the raw data from the Kafka topic.
    - It validates and parses the Protobuf sensor data.
    - Incremental aggregations (average, min, max, and count) are performed:
        - Aggregations are saved in **Protobuf** (as Parquet) and **JSON** formats.
    - Results are stored in a hierarchical directory structure in GCS based on `year/month/day/hour`.

3. **Dashboard API**:
    - The `DashboardMain` component reads the latest aggregated JSON data from GCS.
    - It exposes REST endpoints for fetching:
        - All aggregated data.
        - Data filtered by a specific sensor ID.
    - APIs are served using **Akka HTTP**.

---

## Components

### 1. **KafkaProducerMain**

**Responsibilities**:

- Generate synthetic sensor readings.
- Serialize the data to **Protobuf** format.
- Send the serialized data to the Kafka topic `sensor-readings`.

**Code Highlights**:

```scala
val sensorReadingSource = Source
  .tick(0.milliseconds, 1000.milliseconds, ())
  .map(_ => TemperatureSensorEntity.generateTempSensorReading)
  .map { sensorReading =>
    val proto = SensorReadingProto(sensorReading...
    )
    new ProducerRecord[String, Array[Byte]]("sensor-readings", sensorReading.sensorId, proto.toByteArray)
  }
```

---

### 2. **SensorDataProcessor**

**Responsibilities**:

- Read real-time sensor data from Kafka using **Spark Structured Streaming**.
- Validate and parse the data using **Protobuf**.
- Perform incremental aggregations (e.g., average, min, max) of temperature and humidity.
- Store raw and aggregated data into **Google Cloud Storage** in Parquet and JSON formats.

**Key Steps**:

1. **Kafka Stream Reading**:
   ```scala
   val kafkaStreamData = spark.readStream
     .format("kafka")
     .option("kafka.bootstrap.servers", "localhost:9092")
     .option("subscribe", "sensor-readings")
     .load()
   ```

2. **Validation**:
   ```scala
   df.select(from_protobuf(col("value"), messageType, descriptorPath))
     .filter(col("temperature").between(-50, 150) && col("humidity").between(0, 100))
   ```

3. **Incremental Aggregation**:
    - Combines new and previously aggregated data.
    - Updates metrics like:
        - Average temperature/humidity
        - Minimum/maximum temperature/humidity
        - Record counts (weights)

4. **Data Storage**:
    - Raw data → `raw-data/`
    - Aggregated data → `aggregate/`

---

### 3. **DashboardMain**

**Responsibilities**:

- Serve the latest aggregated sensor data via REST APIs.
- Filter results based on sensor IDs.

**Endpoints**:

- `GET /api/aggregated-data`: Fetch all aggregated sensor data.
- `GET /api/aggregated-data/{sensorId}`: Fetch aggregated data for a specific sensor.

**Code Highlights**:

```scala
path("api" / "aggregated-data" / Segment) { sensorId =>
  complete(fetchAggregatedDataBySensorId(sparkSession, sensorId))
}
```

**Logic**:

- Retrieves the latest data folder based on the directory hierarchy (`year/month/day/hour`) in GCS.
- Loads the JSON files and serves them as API responses.

---

## Setup Instructions

### Prerequisites

- **Kafka** (for message streaming)
- **Apache Spark** (for stream processing)
- **Google Cloud Storage** setup with a valid service account key
- **Protobuf** (for serialization)
- **Akka HTTP** (for API serving)

### Steps

1. **Set up Kafka**:
    - Start Zookeeper and Kafka broker.
    - Create a topic `sensor-readings`:
      ```bash
      kafka-topics.sh --create --topic sensor-readings --bootstrap-server localhost:9092
      ```

2. **Set Up Google Cloud Storage**:
    - Enable GCS and create a bucket (`scala-spark-temp`).
    - Update the GCS credentials file path in `gcs-key.json`.

3. **Build the Project**:
    - Use `sbt` to build:
      ```bash
      sbt clean compile
      ```

---

## Running the Application

1. **Step 1**: Start `KafkaProducerMain` to generate and send sensor data:
   ```bash
   sbt runMain KafkaProducerMain
   ```

2. **Step 2**: Start `SensorDataProcessor` to process the stream and store the results:
   ```bash
   sbt runMain SensorDataProcessor
   ```

3. **Step 3**: Start `DashboardMain` to serve the aggregated data through APIs:
   ```bash
   sbt runMain DashboardMain
   ```

4. **Step 4**: Access the APIs:
    - Fetch all aggregated data:
      ```
      GET http://localhost:8080/api/aggregated-data
      ```
    - Fetch data for a specific sensor:
      ```
      GET http://localhost:8080/api/aggregated-data/sensor-1
      ```

---

## Directory Structure

```
sensor-data/
├── src/
│   ├── main/
│   │   ├── scala/
│   │   │   ├── descriptor/
│   │   │   │      ├── SensorReading.desc
│   │   │   │      └── SensorMetric.desc
│   │   │   ├── producer/
│   │   │   │      ├── KafkaProducerMain.scala
│   │   │   │      └── TemperatureSensorEntity.scala
│   │   │   ├── proto/
│   │   │   │      ├── SensorMetric/
│   │   │   │      │        ├── SensorMetricProto.scala
│   │   │   │      │        └── SensorMetric.scala
│   │   │   │      ├── SensorReadingProto/
│   │   │   │      │         ├── SensorReadingProto.scala
│   │   │   │      │         └── SensorReading.scala
│   │   │   │      ├── SensorMetric.proto
│   │   │   │      └── SensorReadingProto.proto
│   │   │   ├── DashboardMain.scala
│   │   │   └── SensorDataProcessor.scala
│       └── resources/
│               └── gcs-key.json (GCS credentials)
├── build.sbt
└── README.md
```

---

## Conclusion

This application provides an end-to-end pipeline for processing real-time sensor data. It efficiently handles:

- **Data generation** via Kafka.
- **Stream processing and aggregation** using Spark Streaming.
- **Storage and serving** of processed data using GCS and Akka HTTP.

The project can be extended further by integrating visualization tools like **Grafana** or **Apache Superset** to build
interactive dashboards.

---