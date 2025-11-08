# Device Network API

A simple REST API built with **Java 17**, **Spring Boot 3**, and **Maven**  
for registering network devices (Gateways, Switches, Access Points) and exploring their topology.

---

## Tech Stack

- Java 17  
- Spring Boot 3 (Web + Validation)  
- SpringDoc OpenAPI (Swagger UI)  
- In-memory data store (ConcurrentHashMap)  
- JUnit 5 + AssertJ for tests  
- JaCoCo for code coverage

---

## Build & Run

### 1 Build
```bash
mvn clean package
```

### 2 Run
```bash
java -jar target/device-api-0.0.1.jar
```

The app starts on port **8080** by default.

Swagger UI → [http://localhost:8080/swagger](http://localhost:8080/swagger)

---

## Quick Test via cURL

### Register Devices
```bash
# Gateway
curl -X POST http://localhost:8080/api/devices   -H "Content-Type: application/json"   -d '{"deviceType":"GATEWAY","macAddress":"AA:AA:AA:AA:AA:01","uplinkMacAddress":""}'

# Switch
curl -X POST http://localhost:8080/api/devices   -H "Content-Type: application/json"   -d '{"deviceType":"SWITCH","macAddress":"AA:AA:AA:AA:AA:02","uplinkMacAddress":"AA:AA:AA:AA:AA:01"}'

# Access Point
curl -X POST http://localhost:8080/api/devices   -H "Content-Type: application/json"   -d '{"deviceType":"ACCESS_POINT","macAddress":"AA:AA:AA:AA:AA:03","uplinkMacAddress":"AA:AA:AA:AA:AA:02"}'
```

### List All Devices
```bash
curl http://localhost:8080/api/devices
```

### Get Device by MAC
```bash
curl http://localhost:8080/api/devices/AA:AA:AA:AA:AA:02
```

### Get Full Network (Topology)
```bash
curl http://localhost:8080/api/topology
```

### Get Network Starting from a Specific Device
```bash
curl http://localhost:8080/api/topology/AA:AA:AA:AA:AA:01
```

---

## Tests

Run all tests:
```bash
mvn test
```
