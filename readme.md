## Multipay: Utility Payments API

# Development

### Docker Compose (for PostgreSQL)
```bash
docker-compose -p multipay_utility up
docker run -it --rm --link multipay_utility_postgres_1:postgres --net multipay_utility_default postgres:9-alpine psql -h postgres -U multipay
```

### Run migrations
```bash
cd multipay-database-migrations
./dbmate migrate
```

### Spring Run & build
```bash
./gradlew build --continuous
./gradlew bootRun
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

notify_user : xNXxrkMmMxqD77GCn2Fw
notify_pass : dd73MTANpLMCLMkRaMxN
notify_auth : eE5YeHJrTW1NeHFENzdHQ24yRnc6ZGQ3M01UQU5wTE1DTE1rUmFNeE4=
```
## Integration
Create Order: https://www.mcdesaqa.cl/bdpcows/CreateOrderWebService  
Get Order Status: https://www.mcdesaqa.cl/BDPGetOrderStatus/GetOrderStatusWebService     

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

notify_user : hyayvu58aK8SSFyHVqfz
notify_pass : bnykHGZ8rKRZrJvR6H9M
notify_auth : aHlheXZ1NThhSzhTU0Z5SFZxZno6Ym55a0hHWjhyS1Jackp2UjZIOU0=
```

## Categories
```
"1": "OTRO"
"2": "EFT"
"3": "SENCILLITO"
"4": "SANTANDER"

"100": "AGUA"
"200": "LUZ"
"300": "TELEF-TV-INTERNET"
"400": "GAS"
"500": "AUTOPISTAS"
"600": "COSMETICA"
"700": "RETAIL"
"800": "CREDITO-FINANCIERA"
"900": "SEGURIDAD"
"1000": "EDUCACION",
"1100": "CEMENTERIO"
"1200": "OTRAS EMPRESAS"
"1300": "EFECTIVO MULTICAJA"
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

# Multicaja Utility Payment
```
https://apidev.mcdesaqa.cl/bill_payment/agreements_data (https://pastebin.com/wGC0VpDn)  
https://apidev.mcdesaqa.cl/bill_payment/debt_data (https://pastebin.com/SGN1kLCz)  

API-KEY: SBBcjF38qNaOASyyPu596dBzdjITzii3 
```

# Endpoints

## `GET /utility-payments/v1/utilities`
**Obtiene los convenios de pago de cuenta**

Request
```bash
curl --request GET \
--url https://multipay.staging.multicajadigital.cloud/utility-payments/v1/utilities \
--header 'Content-Type: application/json'
```

Response
```json
[
  {
    "utility": "COSTANERA NORTE",
    "collector": "1",
    "category": "100",
    "identifiers": []
  },
  {
    "utility": "AGUAS ANDINAS",
    "collector": "4",
    "category": "100",
    "identifiers": [
      "NRO CLIENTE",
      "CON DIGITO VERIFICADOR"
    ]
  },
  {
    "utility": "ESSBIO",
    "collector": "4",
    "category": "100",
    "identifiers": [
      "NRO SERVICIO",
      "SIN DIGITO VERIFICADOR"
    ]
  }
]
```

## `POST /utility-payments/v1/transactions`
**Crea un pago de cuenta**

Request
```bash
curl --request POST \
--url https://multipay.staging.multicajadigital.cloud/utility-payments/v1/transactions \
--header 'Content-Type: application/json' \
--data '{"utility": "ENTEL PCS","collector": "2","category": "300","identifier": "173379595"}'
```
Response
```json
{
  "id": "eaa7f715372d4b71a7fe00402d391df1",
  "status": "PENDING",
  "buy_order": 20190222011546002,
  "amount": 94290,
  "created": "2019-02-22T01:15:46.665983-03:00",
  "updated": "2019-02-22T01:15:46.665983-03:00",
  "bill": {
    "utility": "ENTEL PCS",
    "collector": "2",
    "category": "300",
    "identifier": "173379595",
    "mc_code": "799378736",
    "amount": 94290,
    "due_date": "2015-02-23"
  }
}
```

Response - Without debt  
```
204 No Content
```

## `GET /utility-payments/v1/transactions/{id}`
**Obtiene los detalles de un pago de cuenta**

Request
```bash
curl --request GET \
--url https://multipay.staging.multicajadigital.cloud/utility-payments/v1/transactions/eaa7f715372d4b71a7fe00402d391df1 \
--header 'Content-Type: application/json'
```
Response
```json
{
  "id": "eaa7f715372d4b71a7fe00402d391df1",
  "status": "WAITING",
  "buy_order": 20190223214030002,
  "amount": 94290,
  "payment_method": "EFT",
  "email": "carlos.izquierdo@multicaja.cl",
  "created": "2019-02-23T21:40:30.362763-03:00",
  "updated": "2019-02-23T21:41:19.216-03:00",
  "bill": {
    "status": "PENDING",
    "utility": "ENTEL PCS",
    "collector": "2",
    "identifier": "173379595",
    "mc_code": "799378736",
    "amount": 94290,
    "due_date": "2015-02-23"
  },
  "eft": {
    "status": "PENDING",
    "order": "853121364954859"
  }
}
```

## `POST /utility-payments/v1/transactions/{id}/webpay`
**Inicia el proceso para pagar un pago de cuenta con webpay**

Request
```bash
curl --request POST \
--url https://multipay.staging.multicajadigital.cloud/utility-payments/v1/transactions/eaa7f715372d4b71a7fe00402d391df1/webpay \
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

## `POST /utility-payments/v1/transactions/{id}/eft`
**Inicia el proceso para pagar un pago de cuenta con transferencia**

Request
```bash
curl --request POST \
--url https://multipay.staging.multicajadigital.cloud/utility-payments/v1/transactions/eaa7f715372d4b71a7fe00402d391df1/eft \
--header 'Content-Type: application/json' \
--data '{"email": "carlos.izquierdo@multicaja.cl"}'
```
Response
```json
{
  "url": "https://10.170.1.11:9191/bdp/order.xhtml?id=853121364954859"
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
