+--------------------+                  +---------------------+  
|   Akka Producer    |  ---> Kafka      |     Kafka Broker    |
| (Generate Sensor   |                  |   (Topic: sensor-   |
|    Data)           |                  |        readings)    |
+--------------------+                  +---------------------+
                                                |
                                                v
                                        +--------------------+      
                                        | Spark Streaming    |       Raw Data (Protobuf)
                                        | (Validation +      |   --> GCS (Raw Storage)
                                        | Protobuf Writing)  |
                                        +--------------------+
                                                |
                                                v
                                        +----------------------+      Aggregated Data (Protobuf)
                                        | Spark Batch          |  --> GCS (Aggregated Storage)
                                        | (Aggregation + JSON) |
                                        +----------------------+
                                                |
                                                v
                                        +--------------------+
                                        | Akka HTTP API      | ---> Web Visualization
                                        | (Serve Aggregates) |
                                        +--------------------+
