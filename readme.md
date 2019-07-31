## Tenpo: Utility Payments API

# Development

### Spring Run & build
```bash
./gradlew build --continuous
./gradlew bootRun
```

### Docker Compose
```bash
docker-compose -p tenpo_nats up
```

# Azure

### Staging Db
```
docker run -it --rm postgres:9-alpine psql -h postgres-db-staging.postgres.database.azure.com -U staging@postgres-db-staging tenpo_utility_payment
YDVxBXJSLNMjY8bf
```

### **Container Registry**
```
Username: McContainerRegistry (docker_username)
Password: hwtF4xp8V2dhxWPCGv=ODCTEWgJTgTVc (docker_password)
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

# TODO API
- cambiar hora from utc to mobile local
- -- cambiar texto mi identificador por el que viene en la api
- -- mostrar deuda ya seleccionado
- -- cambiar estilo agregar a favoritos en pago de cuentas
- -- Pasar al front sectionlist

# TODO 
- -- limitar connection pool
- -- agregar categoria a favorites
- -- agregar rubro en reporte conciliacion
- -- manejar en front sectionlist data
- -- save user id bill
- -- agregar payment method to payments table
- -- Job max attemps in record
- -- Proceso clean bills table (orphans, created)
- -- bill id diferente por cada reintento
- payment external_id to report
- engine, configurar bill_code, merchant_code, merchant_category, merchant_name
- llamar a api balance
- hacer mock de api balance
- apuntar engine a api prepago - dev
- manejar reversa en timeout prepaid charge / endpoing charge prepaid debe ser idenpotente 
- proceso de sync de convenios de pago de cuenta
- probar nodeport para exponer api en rke
- probar contra api bill_payment dev/qa mc
- agregar tabla payment_method a bo
- agregar user_id tabla bills en bo
- dejar expuesto solo el path management en virtual hosts
- fix comments bo
- subir a uat
- subir a prod
- modificar azure apis y poner url internas de ambientes
- api prepaid - payment description - movements event
- api utility - movements event ?
- reports conciliacion
- reports table
- report trxs daily and monthly
- 
- BO Roles
- payment method prepaid
- use domain tenpodigital
- receipt endpoint (Receipt email look and feel)
- Proceso clean transactions table (created, no childs)
- Proceso expire waiting transactions
- Testing
- We need an endpoint from multicaja to check the transaction status
- revisar TODOS

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