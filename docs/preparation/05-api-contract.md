
## 1. Overview
This document defines the HTTP API contract between the client application and the backend service.

**Base URL:** `/api/v1`

**Content Type:** `application/json`

## 2. Products

#### 2.1 Browse Products ####

1. Returns products currently available for customer to browse.
2. Return products currently available for customer with category constraint like  `category`, `size`, `min_price`, `max_price`,...

**Related Use Case:** UC01 - Browse Products

**Endpoint:** `GET /api/v1/products`

**Authentication:** not required

**Path Parameters:** none

**Query Parameters:** 

|Parameter|Type|Required|Default|Description|
|---|---|---|---|---|
|`category`|String|No|—|Filter by category code|
|`product_size`|String|No|—|Filter products having an available variant of this size|
|`color`|String|No|—|Filter products having an available variant of this color|
|`min_price`|Decimal|No|—|Minimum product or variant price|
|`max_price`|Decimal|No|—|Maximum product or variant price|
|`page`|Integer|No|`0`|Zero-based page number|
|`limit`|Integer|No|`20`|Number of products per page|
|`sort`|String|No|`created_at,desc`|Sorting rule|

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

"code": "INVALID_PRICE_RANGE",

"message": "Mininum price must not be negative"

}
```


#### 2.2 Get Product Details####

Returns detailed information about a selected product and its available variants

**Related Use Case:** UC01 — Browse Products 

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
**Error Responses:**

Example: `GET /api/v1/products/10`
Status: `404 Not Found`
Body:
```
{

"status": 404,

"code": "PRODUCT_NOT_FOUND",

"message": "Product was not found"

}
```


Example: `GET /api/v1/products/-10`
Status: `400 Bad Request`
Body:
```
{

"status": 400,

"code": "INVALID_PRODUCT_ID",

"message": "Product ID must be positive"

}
```

|Field|Type|Description|
|---|---|---|
|`status`|Integer|HTTP status code|
|`code`|String|Application-specific error code|
|`message`|String|Human-readable error description|

# 4. UC01 Endpoint Summary

| Method | Endpoint                       | Authentication | Purpose                                   |
| ------ | ------------------------------ | -------------- | ----------------------------------------- |
| `GET`  | `/api/v1/products`             | No             | Browse active products                    |
| `GET`  | `/api/v1/products/{productId}` | No             | View selected product and its variants    |
| `GET`  | `/api/v1/products?color=red`   | No             | View product variants matching conditions |
