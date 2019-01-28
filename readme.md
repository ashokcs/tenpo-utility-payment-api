# Multipay: Utility Payments Service

## Development

### Docker Compose
```bash
docker-compose -p multipay_utility up
docker run -it --rm --link multipay_utility_postgres_1:postgres --net multipay_utility_default postgres:11-alpine psql -h postgres -U multipay
```

### Run migrations
```bash
cd database
./dbmate up
```

### Spring Run & build
```bash
./gradlew build --continuous
./gradlew bootRun
```

### About **dbmate**
```bash
dbmate new create_users_table
dbmate migrate  # run migrations
dbmate up       # run migrations, create db if not exists
dbmate rollback # roll back the most recent migration
dbmate dump     # generate schema.sql
dbmate wait     # pause until the database is available
```

# Endpoints

## `POST /v1/utilities/payment_intents`
Request
```bash
curl --request POST \
--url https://api.multipay.cl/v1/utilities/payment_intents \
--header 'Content-Type: application/json' \
--data '{"utility": "test","identifier": "test","amount": 5000,"email": "email@email.mp"}'
```
Response
```json
{
  "id": "31aad6eb-afd7-4679-af88-04ac83c25113",
  "oc": "P2019012622000008",
  "state": 0,
  "created_at": "2019-01-26T22:07:05.203161"
}
```

## `GET /v1/utilities/payment_intents/:id`
Request
```bash
curl --request GET \
--url https://api.multipay.cl/v1/utilities/payment_intents/31aad6eb-afd7-4679-af88-04ac83c25113 \
--header 'Content-Type: application/json'
```
Response
```json
{
  "id": "31aad6eb-afd7-4679-af88-04ac83c25113",
  "oc": "P2019012622000008",
  "state": 0,
  "created_at": "2019-01-26T22:07:05.203161"
}
```
