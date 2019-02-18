## Multipay: Utility Payments API

# Development

### Docker Compose (for PostgreSQL)
```bash
docker-compose -p multipay_utility up
docker run -it --rm --link multipay_utility_postgres_1:postgres --net multipay_utility_default postgres:9-alpine psql -h postgres -U multipay
```

### Run migrations
```bash
cd database
./dbmate migrate
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

### **Staging PostgreSQL Database**

**Default User**  
Host: postgres-db-staging.postgres.database.azure.com:5432  
User: staging@postgres-db-staging  
Pass: YDVxBXJSLNMjY8bf  
```bash
psql -h postgres-db-staging.postgres.database.azure.com -p 5432 -U staging@postgres-db-staging -d multipay
```

**Create Multipay database and user**  
```bash
create user multipay with password 'multipay';
grant multipay to prepago;
create database multipay owner multipay;
revoke multipay from prepago;
```

### **Container Registry**
```
Username: McContainerRegistry (docker_username)
Password: hwtF4xp8V2dhxWPCGv=ODCTEWgJTgTVc (docker_password)
```

# Webpay
...

# Transferencia
## Production
Create Order: https://www.multicaja.cl/bdpcows/CreateOrderWebService   
Get Order Status: https://www.multicaja.cl/BDPGetOrderStatus/GetOrderStatusWebService   
```
commerce_id : 76828790
branch_id   : 142809
username    : multicaja
password    : fJLQRFm67QNnbo
base64      : bXVsdGljYWphOmZKTFFSRm02N1FObmJv
```
## Development
Create Order: https://10.170.1.11:9191/bdpcows/CreateOrderWebService   
Get Order Status: https://10.170.1.11:9191/BDPGetOrderStatus/GetOrderStatusWebService   
```
commerce_id : 16086857
branch_id   : 118890
username    : EcommerceWebRole
password    : EcommerceWebRole
base64      : RWNvbW1lcmNlV2ViUm9sZTpFY29tbWVyY2VXZWJSb2xl
User        : 10964112-K:1313
```

## Notify Request
```
HTTP request - http://localhost:7771/v1/payments/transferencia/notify/b968349f0e1d4ee7802b08d944027670/1cbc544079894abc895bf74a5862563e/]---
Accept: text/xml, multipart/related
Authorization: Basic aHlheXZ1NThhSzhTU0Z5SFZxZno6Ym55a0hHWjhyS1Jackp2UjZIOU0=
Content-Type: text/xml; charset=utf-8
SOAPAction: "http://www.example.cl/ecommerce/NotifyPaymentWeb/notifyPaymentRequest"

<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:notifyPayment xmlns:ns2="http://www.example.cl/ecommerce/"><mcOrderId>986916273366660</mcOrderId><ecOrderId>1201902131759160003</ecOrderId></ns2:notifyPayment></S:Body></S:Envelope>
```

# Pago de cuentas
```
https://apidev.mcdesaqa.cl/bill_payment/agreements_data (https://pastebin.com/wGC0VpDn)  
https://apidev.mcdesaqa.cl/bill_payment/debt_data (https://pastebin.com/SGN1kLCz)  

API-KEY: SBBcjF38qNaOASyyPu596dBzdjITzii3 
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

## `POST /v1/bills/{id}/transferencia`
**Inicia el proceso para pagar un pago de cuenta con transferencia**

Request
```bash
curl --request POST \
--url http://localhost:7771/v1/bills/efd57e7af34d4189bb705a0e231e0356/transferencia \
--header 'Content-Type: application/json' \
--data '{"email": "carlos.izquierdo@multicaja.cl"}'
```
Response
```json
{
    "url": "https://10.170.1.11:9191/bdp/order.xhtml?id=394939009565861"
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
- integrar tef multicaja