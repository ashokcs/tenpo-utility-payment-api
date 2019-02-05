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

### Azure PostgreSQL Database
**Default User**  
Host: prepaid-postgresql-staging.postgres.database.azure.com  
User: prepago@prepaid-postgresql-staging  
Pass: GcpeWsJ3EWGBrwYY  
```bash
psql -h prepaid-postgresql-staging.postgres.database.azure.com -U prepago@prepaid-postgresql-staging -d postgres
```
**Create Multipay database and user**  
```bash
create user multipay with password 'multipay';
grant multipay to prepago;
create database multipay owner multipay;
revoke multipay from prepago;
```
Host: prepaid-postgresql-staging.postgres.database.azure.com  
User: multipay@prepaid-postgresql-staging  
Pass: multipay  
Database: multipay
```bash
psql -h prepaid-postgresql-staging.postgres.database.azure.com -U multipay@prepaid-postgresql-staging -d multipay
```

# Endpoints

## `GET /v1/utilities`
**Obtiene los convenios de pago de cuenta**

Request
```bash
curl --request GET \
--url http://localhost:7771/v1/utilities \
--header 'Content-Type: application/json'
```

Response
```json
[
  {
    "utility": "AGUAS ANDINAS",
    "collector": {
      "id": "4",
      "name": "SANTANDER"
    },
    "identifiers": [
      "NRO CLIENTE",
      "CON DIGITO VERIFICADOR"
    ]
  },
  {
    "utility": "ESSBIO",
    "collector": {
      "id": "4",
      "name": "SANTANDER"
    },
    "identifiers": [
      "NRO SERVICIO",
      "SIN DIGITO VERIFICADOR"
    ]
  }
]
```

## `POST /v1/bills`
**Crea un pago de cuenta**

Request
```bash
curl --request POST \
--url http://localhost:7771/v1/bills \
--header 'Content-Type: application/json' \
--data '{"utility": "MUNDO_PACIFICO","collector": "3","identifier": "2312312"}'
```
Response
```json
{
  "bill_id": "9e9a9324f24147d9a454c4f163d52d9a",
  "status": 0,
  "identifier": "2312312",
  "amount": 94290,
  "utility": "MUNDO_PACIFICO",
  "collector": "3",
  "due_date": "2015-02-23",
  "transaction_id": "799378736"
}
```

## `GET /v1/bills/{id}`
**Obtiene los detalles de un pago de cuenta**

Request
```bash
curl --request GET \
--url http://localhost:7771/v1/bills/bbd12666fb0e4068a34cb4699afbface \
--header 'Content-Type: application/json'
```
Response
```json
{
  "bill_id": "9e9a9324f24147d9a454c4f163d52d9a",
  "status": 0,
  "utility": "MUNDO_PACIFICO",
  "collector": "3",
  "identifier": "2312312",
  "amount": 94290,
  "due_date": "2015-02-23",
  "transaction_id": "799378736"
}
```

## `POST /v1/bills/{id}/pay`
**Inicia el proceso para pagar un pago de cuenta**

Request
```bash
curl --request POST \
--url http://localhost:7771/v1/bills/9e9a9324f24147d9a454c4f163d52d9a/pay \
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

# TODOS
- Subir api transbank a Azure
- Quitar plataforma de pago
- Implementar pagos con webpay