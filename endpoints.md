# Endpoints

## `GET /utility-payments/v1/utilities`
**Obtiene los convenios de pago de cuenta**

Request
```bash
curl --request GET \
--url https://tenpo.staging.multicajadigital.cloud/utility-payments/v1/utilities \
--header 'Content-Type: application/json'
```

Response
```json
[
  {
    "utility_code": "COSTANERA NORTE",
    "utility_name": "COSTANERA NORTE", 
    "collector_id": "1",
    "collector_name": "SENCILLITO",
    "category_id": "100",
    "category_name": "AGUA",
    "identifiers": []
  },
  {
    "utility_code": "AGUAS ANDINAS",
    "utility_name": "AGUAS ANDINAS", 
    "collector_id": "4",
    "collector_name": "SENCILLITO",
    "category_id": "100",
    "category_name": "AGUA",
    "identifiers": [
      "NRO CLIENTE",
      "CON DIGITO VERIFICADOR"
    ]
  },
  {
    "utility_code": "ESSBIO",
    "utility_name": "ESSBIO", 
    "collector_id": "4",
    "collector_name": "SENCILLITO",
    "category_id": "100",
    "category_name": "AGUA",
    "identifiers": [
      "NRO SERVICIO",
      "SIN DIGITO VERIFICADOR"
    ]
  }
]
```

## `POST /utility-payments/v1/bills`
**Obtiene las deudas de una cuenta**

Request
```bash
curl --request POST \
--url https://tenpo.staging.multicajadigital.cloud/utility-payments/v1/bills \
--header 'Content-Type: application/json' \
--data '{"utility_code": "PARQUE DEL SENDERO","collector_id": "3","category_id":"300","identifier":"66823334"}'
```

Response
```json
[
    {
        "id": "7cf03915-5df3-4d17-96b6-5ef81da8a85d",
        "utility_name": "PARQUE DEL SENDERO",
        "category_name": "TELEF-TV-INTERNET",
        "identifier": "66823334",
        "amount": 1724,
        "due_date": "No disponible",
        "description": "Deuda total"
    },
    {
        "id": "86702aa8-b111-4b1c-9484-bd40b3faa443",
        "utility_name": "PARQUE DEL SENDERO",
        "category_name": "TELEF-TV-INTERNET",
        "identifier": "66823334",
        "amount": 2745,
        "due_date": "No disponible",
        "description": "Deuda vencida"
    }
]
```

## `POST /utility-payments/v1/transactions`
**Crea una transacción de pago de cuenta**

Request Webpay
```bash
curl --request POST \
--url https://tenpo.staging.multicajadigital.cloud/utility-payments/v1/transactions \
--header 'Content-Type: application/json' \
--data '{"bills": ["7cf03915-5df3-4d17-96b6-5ef81da8a85d"],"payment_method":"webpay","email":"carlos.izquierdo@multicaja.cl"}'
```
Response Webpay
```json
{
  "webpay": {
    "token": "e8863e2f2d9e4d83b09d978c18403d6a20f1a6febb40c22d8342b2ae173f1d5b",
    "url": "https://webpay3gint.transbank.cl/webpayserver/initTransaction"
  }
}
```

Request Tef
```bash
curl --request POST \
--url https://tenpo.staging.multicajadigital.cloud/utility-payments/v1/transactions \
--header 'Content-Type: application/json' \
--data '{"bills": ["7cf03915-5df3-4d17-96b6-5ef81da8a85d"],"payment_method":"tef","email":"carlos.izquierdo@multicaja.cl"}'
```
Response Tef
```json
{
  "tef": {
    "url": "https://www.multicaja.cl/bdp/order.xhtml?id=853121364954859"
  }
}
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

## `POST /utility-payments/v1/transactions/{id}/receipt`
**Reenvía el comprobante de un pago de cuenta**

Request
```bash
curl --request POST \
--url https://multipay.staging.multicajadigital.cloud/utility-payments/v1/transactions/eaa7f715372d4b71a7fe00402d391df1/receipt \
--header 'Content-Type: application/json' \
--data '{"email": "carlos.izquierdo@multicaja.cl","recaptcha": "03AOLTBLTVBtniCIGwS_N4XFQAQ3d5vXsQJp8kYLRsDcqhBUEf2mh8onvgXDOJ_e5mgk7qgNOXOlihtvc4GzV75D6vIsbEfrVb1ha-jmnW-1RzopTSKvNi-0PqttZPrYDqF8ApYfrSKLOPX8WY0uMX4HsfiwVGjZ0MNN8pcLloREsXEsQ-_lZvgOBD8g0eEB-P2jtdFntdnSQR-lvJN2krFqu0X679KkMHq5C1WNAlP_mZW4huwSenvNFUcNZku3-zXgOMzXhpppAhy4ovEqP8GtIUOlcR9vMKn7TkQUOgEwJf7-vvwEo-dlygU2cT9XhaVQpGfddOYm1iNTPsIaFksypT169DAFsn6bW-WMDSUfj6UvlTRnGkwxU"}'
```
Response
```json
200 - OK
```