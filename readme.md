## Multipay: Utility Payments API

# Development

### Docker Compose (for PostgreSQL)
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
# Azure

### PostgreSQL Database
**Default User**  
Host: postgres-db-staging.postgres.database.azure.com  
User: staging@postgres-db-staging  
Pass: ??????  
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

# Webpay
...

# Transferencia
- Create Order: https://www.multicaja.cl/bdpcows/CreateOrderWebService
- Get Order Status: https://www.multicaja.cl/bdpgosws/GetOrderStatusWebService
```
commerce_id : 76828790
branch_id   : 142809
username    : multicaja
password    : fJLQRFm67QNnbo
base64      : bXVsdGljYWphOmZKTFFSRm02N1FObmJv
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
  "bill_id": "efd57e7af34d4189bb705a0e231e0356",
  "status": "pending",
  "utility": "MUNDO_PACIFICO",
  "collector": "3",
  "identifier": "2312312",
  "amount": 94290,
  "due_date": "2015-02-23",
  "transaction_id": "799378736"
}
```

## `GET /v1/bills/{id}`
**Obtiene los detalles de un pago de cuenta**

Request
```bash
curl --request GET \
--url http://localhost:7771/v1/bills/efd57e7af34d4189bb705a0e231e0356 \
--header 'Content-Type: application/json'
```
Response
```json
{
  "bill_id": "efd57e7af34d4189bb705a0e231e0356",
  "buy_order": "PC201902121743000003",
  "status": "succeed",
  "utility": "MUNDO_PACIFICO",
  "collector": "3",
  "identifier": "2312312",
  "amount": 94290,
  "due_date": "2015-02-23",
  "transaction_id": "799378736",
  "payment": "webpay",
  "email": "carlos.izquierdo@multicaja.cl"
}
```

## `POST /v1/bills/{id}/webpay`
**Inicia el proceso para pagar un pago de cuenta con webpay**

Request
```bash
curl --request POST \
--url http://localhost:7771/v1/bills/efd57e7af34d4189bb705a0e231e0356/webpay \
--header 'Content-Type: application/json' \
--data '{"email": "carlos.izquierdo@multicaja.cl"}'
```
Response
```json
{
  "token": "e8863e2f2d9e4d83b09d978c18403d6a20f1a6febb40c22d8342b2ae173f1d5b",
  "url": "https://webpay3gint.transbank.cl/webpayserver/initTransaction"
}
```

# TODOS
- hacer pipeline, Implementar migrations, secrets and consul
- hacer pago tef
- hacer reintentos
- reportes conciliación
- implementar email
- hacer bo
- api transbank poner api-key
- api tef poner api-key