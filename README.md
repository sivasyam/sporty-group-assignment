# sporty-group-assignment

Spring Boot solution for the Kafka / RocketMQ backend assignment.

## What it does

- Exposes `POST /api/event-outcomes` to publish an event outcome.
- Consumes the outcome from a mock Kafka broker on topic `event-outcomes`.
- Matches bets in an in-memory repository by `eventId`.
- Produces settlement messages to a mock RocketMQ broker on topic `bet-settlements`.
- Consumes settlement messages from the same settlement topic.
- Includes `POST /api/bets`, `GET /api/bets`, `GET /api/settlements`, and `GET /api/health` for local verification.

## Run locally

You need Maven and a JDK.

```powershell
mvn spring-boot:run
```

The service listens on `http://localhost:8080`.

Run tests with:

```powershell
mvn test
```

## Example requests

Create a bet:

```bash
curl -X POST http://localhost:8080/api/bets \
  -H "Content-Type: application/json" \
  -d "{\"betId\":1,\"userId\":10,\"eventId\":100,\"eventMarketId\":5,\"eventWinnerId\":2,\"betAmount\":25.0}"
```

Publish an outcome:

```bash
curl -X POST http://localhost:8080/api/event-outcomes \
  -H "Content-Type: application/json" \
  -d "{\"eventId\":100,\"eventName\":\"Match 100\",\"eventWinnerId\":2}"
```

Read settlements:

```bash
curl http://localhost:8080/api/settlements
```

## Docker

Build and run the app container:

```bash
docker build -t sporty-group-assignment .
docker run --rm -p 8080:8080 sporty-group-assignment
```

The broker layer is mocked in-process, so no Kafka or RocketMQ containers are required.
