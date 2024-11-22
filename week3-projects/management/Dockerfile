# First stage: Build all services
FROM openjdk:11-jdk-slim

WORKDIR /app

# Install dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl gnupg unzip && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99e82a75642ac823" | apt-key add && \
    apt-get update && apt-get install -y sbt && \
    rm -rf /var/lib/apt/lists/*

# Copy project files
COPY . /app

# Set build-time variables
ARG SERVICE_NAME
ARG FOLDER_NAME

# Build and package the application
RUN if [ "$SERVICE_NAME" = "notification" ]; then \
        echo "Building Notification Service" && \
        sbt clean compile; \
    else \
        echo "Building $SERVICE_NAME Service" && \
        sbt clean compile dist; \
    fi

RUN if [ "$SERVICE_NAME" != "notification" ]; then \
        echo "Setting up $SERVICE_NAME Service" && \
        cd /app/$FOLDER_NAME/target/universal && \
        unzip $SERVICE_NAME-1.0.zip; \
    fi

# Set runtime environment variables
ENV SERVICE_NAME=$SERVICE_NAME
ENV FOLDER_NAME=$FOLDER_NAME

# Expose service port
ENV SERVICE_PORT=$SERVICE_PORT
EXPOSE $SERVICE_PORT

# CMD with runtime evaluation of environment variables
CMD ["sh", "-c", "if [ \"$SERVICE_NAME\" = \"notification\" ]; then sbt clean \"project notification\" run; else /app/$FOLDER_NAME/target/universal/${SERVICE_NAME}-1.0/bin/${SERVICE_NAME}; fi"]
