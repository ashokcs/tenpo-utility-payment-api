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

## `POST /v1/bills`
**Crear pago de cuenta**

Request
```bash
curl --request POST \
--url http://localhost:7771/v1/bills \
--header 'Content-Type: application/json' \
--data '{"utility_id": 123,"identifier": "2312312"}'
```
Response
```json
{
  "bill_id": "bbd12666fb0e4068a34cb4699afbface",
  "status": 0,
  "utility_id": 123,
  "identifier": "2312312",
  "amount": 10335
}
```

## `GET /v1/bills/{id}`
**Obtener los detalles de un pago de cuenta**

Request
```bash
curl --request GET \
--url http://localhost:7771/v1/bills/bbd12666fb0e4068a34cb4699afbface \
--header 'Content-Type: application/json'
```
Response
```json
{
  "bill_id": "bbd12666fb0e4068a34cb4699afbface",
  "status": 0,
  "utility_id": 123,
  "identifier": "2312312",
  "amount": 10335
}
```

## `POST /v1/bills/{id}/pay`
**Inicia el proceso para pagar un pago de cuenta**

Request
```bash
curl --request POST \
--url http://localhost:7771/v1/bills/bbd12666fb0e4068a34cb4699afbface/pay \
--header 'Content-Type: application/json' \
--data '{"payment_id": 1, "email": "user@email.cl"}'
```
Response
```json
{
  "redirect_url": "https://payments.staging.multicajadigital.cloud/order/5",
}
```

## `POST /v1/bills/{id}/payment/{paymentId}/confirm`
**Confirma un pago de cuenta**

Request
```bash
curl --request POST \
--url http://localhost:7771/v1/bills/bbd12666fb0e4068a34cb4699afbface/payment/229e36dcf7864baa8e5094b11d1b5955/confirm \
--header 'Content-Type: application/json' \
--data '{}'
```
Response
```json
{
}
```