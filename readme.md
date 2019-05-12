## Tenpo: Utility Payments API

# Development

### Spring Run & build
```bash
./gradlew build --continuous
./gradlew bootRun
```

### Docker Compose
```bash
docker-compose -p tenpo up
docker run -it --rm --link tenpo_postgres_1:postgres --net tenpo_default postgres:9-alpine psql -h postgres -U tenpo
```

# Azure

### **Container Registry**
```
Username: McContainerRegistry (docker_username)
Password: hwtF4xp8V2dhxWPCGv=ODCTEWgJTgTVc (docker_password)
```

# Webpay
...

# Multicaja Transferencia
## Production
Create Order: https://www.multicaja.cl/bdpcows/CreateOrderWebService   
Get Order Status: https://www.multicaja.cl/BDPGetOrderStatus/GetOrderStatusWebService   
```
Remote WS
commerce_id : 76828790
branch_id   : 142809
username    : multicaja
password    : fJLQRFm67QNnbo
base64      : bXVsdGljYWphOmZKTFFSRm02N1FObmJv

Local WS
notify_user : xNXxrkMmMxqD77GCn2Fw
notify_pass : dd73MTANpLMCLMkRaMxN
notify_auth : eE5YeHJrTW1NeHFENzdHQ24yRnc6ZGQ3M01UQU5wTE1DTE1rUmFNeE4=
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
DEVELOPMENT
API-KEY: SBBcjF38qNaOASyyPu596dBzdjITzii3 
https://apidev.mcdesaqa.cl/bill_payment/agreements_data (https://pastebin.com/wGC0VpDn)  
https://apidev.mcdesaqa.cl/bill_payment/debt_data (https://pastebin.com/SGN1kLCz)  
```

```
DEUDA TOTAL
DEUDA VENCIDA
{"firm":"WOM","collector":"1","payment_id":"101996131"}
{
  "response_code": 88,
  "response_message": "APROBADA",
  "data": {
    "mc_code": "1570079266",
    "debts": [
      {
        "order": "1",
        "due_date": "D.TOTAL",
        "payment_amount": 12571,

        "total_amount": 12570,
        "adjustment_amount": -1
      },
      {
        "order": "2",
        "due_date": "D.VENCIDA",
        "payment_amount": 12571,

        "total_amount": 12570,
        "adjustment_amount": -1
      }
    ],
    "debt_data_id": 17
  }
}

{"firm":"ENEL DISTRIBUCION","collector":"3","payment_id":"130609K"}
{
  "response_code": 88,
  "response_message": "APROBADA",
  "data": {
    "mc_code": "1570099696",
    "authorization_code": "74055746",
    "date_time": "20190412112407",
    "debts": [
      {
        "due_date": "No disponible",
        "payment_amount": 15515,
        
        "adjustment_amount": -5,
        "total_amount": 15510,
        "agreement": "2014",
        "confirmation": "SI",
        "authorizer": "EFT",
        "bill_payment_info": "Pago Cuentas Multicaja",
        "pay_validation": "1",
        "mc_relation_code": "1570099696",
        "adjustment": "-5",
        "rsp_authorization_code": "74055746",
        "rsp_data_time": "20190412112407",
        "rsp_mc_code": "1570099696",
        "rsp_client_id": "130609K",
        "rsp_agreement_id": "2014",
        "rsp_agreement_version_id": "1",
        "rsp_amount": "15515",
        "rsp_due_date": "No disponible",
        "rsp_data_invoice": "order:1;field_01:2055;amount:15515;"
      }
    ],
    "debt_data_id": 18
  }
}

{"firm":"CLARO HOGAR RUT","collector":"1","payment_id":"88155408"}
{
  "response_code": 88,
  "response_message": "APROBADA",
  "data": {
    "mc_code": "1570138666",
    "debts": [
      {
        "due_date": "NO DISPONIBLE ",
        "payment_amount": 49112,

        "total_amount": "49110",
        "adjustment_amount": -2,
        "agreement": "12058",
        "confirmation": "SI",
        "authorizer": "CLARO",
        "bill_payment_info": "Pago Cuentas Multicaja",
        "adjustment": "-2",
        "monto_ajustado": "49110",
        "rsp_mc_code": "1570138666",
        "rsp_transaction_id": "1155508369553923",
        "rsp_total_payment": "49112"
      }
    ],
    "debt_data_id": 25
  }
}

{"firm":"RIPLEY","collector":"3","payment_id":"113920335"}
{
  "response_code": 88,
  "response_message": "APROBADA",
  "data": {
    "mc_code": "1574199124",
    "authorization_code": "74193362",
    "date_time": "20190415155445",
    "debts": [
      {
        "due_date": "No disponible",
        "payment_amount": 597434,

        "total_amount": 597430,
        "adjustment_amount": -4,
        "agreement": "6621",
        "confirmation": "SI",
        "authorizer": "EFT",
        "bill_payment_info": "Pago Cuentas Multicaja",
        "pay_validation": "1",
        "mc_relation_code": "1574199124",
        "adjustment": "-4",
        "rsp_authorization_code": "74193362",
        "rsp_data_time": "20190415155445",
        "rsp_mc_code": "1574199124",
        "rsp_client_id": "113920335",
        "rsp_agreement_id": "6621",
        "rsp_agreement_version_id": "1",
        "rsp_amount": "597434",
        "rsp_due_date": "No disponible",
        "rsp_data_invoice": "order:1;clientNumber:113920335;amount:597434;"
      }
    ],
    "debt_data_id": 131
  }
}

{"firm":"ENTEL PCS","collector":"2","payment_id":"126617852"}
{
  "response_code": 88,
  "response_message": "APROBADA",
  "data": {
    "mc_code": "1574391724",
    "debts": [
      {
        "due_date": "10-04-2019",
        "payment_amount": 41297,

        "total_amount": 41300,
        "adjustment_amount": 3,
        "confirmation": "SI",
        "authorizer": "EFT",
        "bill_payment_info": "Pago Cuentas Multicaja",
        "adjustment": "3",
        "rsp_rut_client": "126617852",
        "rsp_name_client": "LORETO NAVIA LOPEZ",
        "rsp_invoice_number": "216414517",
        "rsp_mc_code": "1574391724",
        "rsp_client_id": "126617852",
        "rsp_agreement_id": "1",
        "rsp_agreement_version_id": "1",
        "rsp_amount": "41297",
        "rsp_due_date": "10-04-2019"
      }
    ],
    "debt_data_id": 136
  }
}
```

# Git
```sh
git tag -a snapshot-v1.0.0 -m "snapshot-v1.0.0"
git push bb snapshot-v1.0.0
git push --delete bb snapshot-v1.0.0
git tag --delete snapshot-v1.0.0

git tag -a release-v1.0.0 -m "release-v1.0.0"
git push bb release-v1.0.0
git push --delete bb release-v1.0.0
git tag --delete release-v1.0.0

git tag -a oti-v1.0.0 -m "oti-v1.0.0"
git push bb oti-v1.0.0
git push --delete bb oti-v1.0.0
git tag --delete oti-v1.0.0
```

# TODO 
- use domain tenpodigital
- receipt endpoint (Receipt email look and feel)
- payment method prepaid
- reports conciliacion
- reports table
- totaliser db
- totaliser bo
- Proceso clean bills table (orphans, created)
- Proceso clean transactions table (created, no childs)
- Testing
- We need an endpoint from multicaja to check the transaction status
- BO Roles

# Cart Feature
```
# General
GET   /categories
GET   /payment-methods

# Utilities
GET   /utilities
POST  /utilities/{id}/bills

# Transactions
POST  /transactions
GET   /transactions/{id}
POST  /transactions/{id}/receipt
```

```
POST    /transactions                       Create transaction
GET     /transactions/{id}                  Get transaction
POST    /transactions/{id}                  Add elements to an existing transaction
DELETE  /transactions/{id}                  Delete elements from an existing transaction
POST    /transactions/{id}/checkout         Set the checkout
POST    /transactions/{id}/checkout/next    Redirect 
POST    /transactions/{id}/receipt          Receipt
```

```
POST  /cart                     Create cart
POST  /cart/{cart_id}           Put a product in the cart
GET   /cart/{cart_id}           Get cart content
POST  /cart/{cart_id}/checkout  Checkout
```
