
## 1. Overview

This document defines the HTTP API contract between the client application and the backend service.

**Base URL:** `/api/v1`

**Content Type:** `application/json`

---------

# Customer APIs

There is no authentication / authorization needed for customer to do basic interactions.

## 1. Product
### UC01 Browse Products ####

1. Returns products currently available for customer to browse.
2. Return products currently available for customer with category constraint like  `category`, `size`, `min_price`, `max_price`,...

**Related Use Case:** `UC01 - Browse Products`

**Endpoint:** `GET /api/v1/products`

**Authentication:** not required

**Path Parameters:** none

**Query Parameters:**

| Parameter   | Type    | Required | Default           | Description                                               |
| ----------- | ------- | -------- | ----------------- | --------------------------------------------------------- |
| `category`  | String  | No       | —                 | Filter by category code                                   |
| `name`      | String  | No       | __                | Filter by unique name of product                          |
| `size`      | String  | No       | —                 | Filter products having an available variant of this size  |
| `color`     | String  | No       | —                 | Filter products having an available variant of this color |
| `min_price` | Decimal | No       | —                 | Minimum product or variant price                          |
| `max_price` | Decimal | No       | —                 | Maximum product or variant price                          |
| `page`      | Integer | No       | `0`               | Zero-based page number                                    |
| `limit`     | Integer | No       | `20`              | Number of products per page                               |
| `sort`      | String  | No       | `created_at,desc` | Sorting rule                                              |

**Business Rules:**

- Only products with `ACTIVE` sale_status are returned.
- An `empty` product collection is a valid response.
- Products not available for sale must not be exposed to customers.
- An empty product collection is a valid response.
- `min_price` and `max_price` must not be negative.
- If both are supplied, `min_price` must not be greater than `max_price`.
- Filter values are combined using `AND`.

**Endpoint Examples:**  `GET /api/v1/products?color=red&size=M`

**Success Response:**
Status: `200 OK`
Body:
```
{
    "content": [
        {
            "id": 101,
            "category": "TOPS",
            "name": "Vietnamese T-shirt",
            "image_url": "/images/products/101/T-shirt.jpg",
            "base_price": 100000,
            
        },
        {
            "id": 312,
            "category": "BOTTOMS",
            "name": "European Pants",
            "image_url": "/images/products/312/pants.jpg",
            "base_price": 100000,
            "size": "M",
            "color": "red"
        }
    ]
}
```

**Empty Result:**
Status: `200 OK`
Body: `{"content": []}`

**Error Responses:**
Example: `GET /api/v1/products?min_price=-1000000`
Status: `400 Bad Request`
Body:
```
{
"status": 400,
"error": "Bad Request"
"code": "INVALID_PRICE_RANGE",
"message": "Mininum price must not be negative"
}
```


##### Get Product Details####

Returns detailed information about a selected product and its available variants

**Related Use Case:** `UC01 — Browse Products`

**Endpoint:** `GET /api/v1/products/{productId}`

**Authentication:** no required

**Path Parameters**

| Parameter   | Type | Required | Description                   |
| :---------- | :--- | :------- | :---------------------------- |
| `productId` | Long | Yes      | Unique index for each product |
**Success Response:**
```
{
  "id": 101,
  "category": "TOPS",
  "name": "Vietnamese T-shirt",
  "description": "Comfortable cotton T-shirt.",
  "base_price": 100000,
  "images": [
    "/images/products/101/front.jpg",
    "/images/products/101/back.jpg"
  ],
  "variants": [
    {
      "id": 1001,
      "size": "M",
      "color": "red",
      "price": 100000,
      "available_quantity": 12
    },
    {
      "id": 1002,
      "size": "L",
      "color": "red",
      "price": 110000,
      "available_quantity": 5
    }
  ]
}
```

**Error Responses: ( Product not found )**
Example: `GET /api/v1/products/10`
Status: `404 Not Found`
Body:

```
{
  "timestamp": "2026-09-30T11:25:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product with [id=10] not found",
  "errorCode": "PRODUCT_NOT_FOUND",
}
```

**Error Responses: ( Invalid product Id )**
Example: `GET /api/v1/products/-10`
Status: `400 Bad Request`
Body:
```
{
  "timestamp": "2026-09-30T11:25:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "ID must be positive",
  "errorCode": "INVALID ID",
  "details": [
    {
      "field": "productId",
      "issue": "Invalid productId",
    }
  ]
}
```

|Field|Type|Description|
|---|---|---|
|`status`|Integer|HTTP status code|
|`code`|String|Application-specific error code|
|`message`|String|Human-readable error description|

####  UC01 Endpoint Summary

| Method | Endpoint                       | Authentication | Purpose                                   |
| ------ | ------------------------------ | -------------- | ----------------------------------------- |
| `GET`  | `/api/v1/products`             | No             | Browse active products                    |
| `GET`  | `/api/v1/products/{productId}` | No             | View selected product and its variants    |
| `GET`  | `/api/v1/products?color=red`   | No             | View product variants matching conditions |


## 2. Cart / Cart Item

#### UC02 Add Product Variant to Cart
#### UC03 View Cart
#### UC04 Update Cart Item Quantity
#### UC05 Remove Item from Cart


## 3. Order

#### UC06 Place Order####

#### UC07 Look Up Order####


________________________________________________________________________


# Administrator APIs

Authentication and Authorization are needed to have the access to these interactions

## 1. Authentication

Admin has to login by filling his email and password to have administrator access

### UC08 Administrator Login ####
Admin submit needed details from frontend login form.
#### UC08 Endpoint Summary:
| Method | Endpoint                   | Authorization | Purpose                           |
| ------ | -------------------------- | ------------- | --------------------------------- |
| `POST` | `/api/v1/admin/auth/login` | Yes           | Admin login to obtain full access |


#### UC08 Detailed info:

**Authentication:** `not required`

**Authorization:** `not required`

**Path Parameters**: none

**Query Parameters:** none

**Business Rules:**
- Login by filling in both `email` and `password`
- `email` and `password` are checked with validation

**Endpoint**: `POST /api/v1/admin/auth/login`

**Request Body:**
```
{
    "email": "haianh2504077@gmail.com",
    "password": "xx***223423asd***"
}
```

**Success Response:**
Status: `200 OK`
Body:
```
{
  "timestamp": "2026-09-30T11:25:00Z",
  "status": 200,
  "cookie": "xasdasxz",
}
```

**Error Response: ( Lack of details - email | password - email & password )**
Endpoint example: `POST /api/v1/admin/auth/login`
Request Body:

***( password not provided )***
```
{
    "email": "haianh2504077@gmail.com",
    "password": ""
}
```

***( email not provided )
```
{
    "email": "",
    "password": "xx***223423asd***"
}
```

***( email and password not provided )***
```
{
    "email": "",
    "password": ""
}
```

Status: `400 Bad Request`
Body: **( for email and password not provided )**
```
{
  "timestamp": "2026-09-30T11:25:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Both email and password are required for admin login",
  "details": [
    {
      "field": "email",
      "issue": "blank email",
    },
    {
      "field": "password",
      "issue": "blank password",
    }
  ]
}
```



## 2. Product

### UC09 Create Product

Create a new product with information sent from Admin at Frontend.

#### UC09 Endpoint Summary:

| Method | Endpoint                 | Request Body                               | Authorization | Purpose            |
| ------ | ------------------------ | ------------------------------------------ | ------------- | ------------------ |
| `POST` | `/api/v1/admin/products` | {<br>    "name": <br>    "category": <br>} | Yes           | Create new product |

#### UC09 Detailed Info:

**Authentication:** `Required`

**Authorization:** `ADMIN`

**Business Rule**:
- `name`, `category` must be provided to create
- `saleStatus` should be `INACTIVE` initially
- `productVariants`, `price` must be added later to officially become `ACTIVE`

**Endpoint:** `POST /api/v1/admin/products`

**Path Parameters**: none

**Query Parameters:** none

**Request Body:**
```
{
    "name": "Beach T-Shirt",
    "category": "TOPS"
}
``` 

**Success Response:**
Endpoint example: `POST /api/v1/admin/products`
Status: `201 CREATED`
Body:
```
{
  "id": 2,
  "name": "Hue Short",
  "category": "BOTTOMS",
  "price": null,
  "saleStatus": "INACTIVE"
}
```

**Error Response: ( lack of details )**
Endpoint example: `POST /api/v1/admin/products/{name}` ,  `POST /api/v1/products`
Status: `400 BAD REQUEST`
Body:
```
{
  "timestamp": "2026-09-30T11:25:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Missing required fields",
  "errorCode": "MISSING_FIELDS",
  "details": [
    {
      "field": "name",
      "issue": "must not be null or empty",
    }
  ]
}
```

**Error Response: ( wrong input )**
Endpoint example: `POST /api/v1/admin/products/Beach Shirt/category/DOWNS`
Status: `400 BAD REQUEST`
Body:
```
{
  "timestamp": "2026-09-30T11:25:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid information provided to create new product",
  "errorCode": "INVALID_DETAILS_CREATE_PRODUCT",
  "details": [
    {
      "field": "category",
      "issue": "Invalid category value",
    }
  ]
}
```

**Error Response: ( duplicate name - name must be unique )**
Endpoint example: `POST /api/v1/admin/products/Beach Shirt/category/DOWNS`
Status: `409 CONFLICT`
Body:
```
{
  "timestamp": "2026-09-30T11:25:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Product with [name=Beach Shirt] already exist",
  "errorCode": "PRODUCT_NAME_DUPLICATED",
}
```



### UC10 Update Product

Changes / Update details in a persisted product in a product edit page ( not include variants ), every detail provided is optional
#### UC08 Endpoint Summary:
| Method  | Endpoint                             | Authorization | Purpose |
| ------- | ------------------------------------ | ------------- | ------- |
| `PATCH` | `/api/v1/admin/products/{productId}` | Yes           |         |
#### UC08 Detailed Info:

**Authentication:** `Required`

**Authorization:** `ADMIN`

**Endpoint:** `PATCH /api/v1/admin/products/{productId}`

**Path Parameters**:

| Parameter | Type | Required |
| :--- | :--- | :--- |
| `id` | `Long` | `Yes`|

**Query Parameters:** none

**Request Body:**
```
{
  "name": "Ergonomic Office Chair",
  "description": "Updated description",
  "category": "BOTTOMS",
  "price":12000000
}
```

```
{
  "name": "Ergonomic Office Chair",
}
```

```
{
  "category": "BOTTOMS",
  "price":10000000
}
```


**Success Response:**
Endpoint example: `PATCH /api/v1/admin/products/123`
Request Body (example):
```
{
  "name": "Ergonomic Office Chair",
  "description": "Updated description",
  "category": "BOTTOMS",
  "price":12000000
}
```
Status: `200 OK`
Body: ( returning the `current representation` of the product )
```
{
  "id": 123,
  "name": "Ergonomic Office Chair",
  "description": "Updated description",
  "category": "BOTTOMS",
  "price": 12000000,
  "saleStatus": "ACTIVE"
}
```


**Error Response:** **( Product not found )**
Endpoint example: `PATCH /api/v1/admin/products/254`
Request Body:
```
{
  "name": "Ergonomic Office Chair",
}
```
Status: `404 NOT FOUND`
Body:
```
{
  "status": 404,
  "code": "PRODUCT_NOT_FOUND",
  "message": "Product with [id=254] not found"
}
```

