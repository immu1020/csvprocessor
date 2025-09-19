# CSV Processor API

A Spring Boot application to upload and process CSV files, flagging rows with valid email addresses.

## Features

- `/API/upload`: Upload CSV file, returns unique ID
- `/API/download/{id}`: Download processed file with email flag
- Validates emails using regex
- Stores files locally and tracks status in memory
- Dockerized for easy deployment

## Tech Stack

- Java 17
- Spring Boot
- JUnit 5
- SLF4J Logging
- Docker

## 📦 Build & Run
# Build your app first
mvn clean package

# Build Docker image
docker build -t csv-processor .

# Run container
docker run -p 8080:8080 csv-processor

```bash
# Clean and build the project
mvn clean package

# Run the application
java -jar target/csv-processor-*.jar

# Docker
docker build -t csv-processor .
docker run -p 8080:8080 csv-processor


Application runs on:
http://localhost:8080

## Docker Support
Dockerfile included in project root
# Build Docker image
docker build -t csv-processor .

# Run container
docker run -p 8080:8080 csv-processor


## API Usage
#Upload CSV
curl -X POST -F "file=@sample.csv" http://localhost:8080/API/upload


# Response:
{
  "message": "File uploaded successfully",
  "status": 200,
  "data": {
    "id": "a225eb00-0907-4273-92ca-5faadeefae5f"
  }
}


## Download Processed File
curl -X GET http://localhost:8080/API/download/{id} -o processed.csv


# Responses:
- 200 OK: File download successful
- 423 Locked: File still processing
- 400 Bad Request: Invalid ID

##Testing
Run all tests
mvn test


Test Coverage Includes:
- Email validation logic
- File processing and flagging
- Upload/download endpoint behavior
- Exception handling


# Sample CSV Format
name,email
John,john@example.com
Jane,jane.com


# Processed output:
name,email,flag
John,john@example.com,true
Jane,jane.com,false



## Cleanup
Processed files are stored in uploaded-files/. You can delete them manually or add a cleanup script if needed.

## Developer Notes
- Follow clean commit practices (feat, fix, test, docs)
- Use Postman or curl for testing endpoints
- Logs are available via SLF4J for debugging

## License
This project is open-source and available for educational and professional use.


