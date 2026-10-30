# Core API Contract Flow Design:

## Guest Cart Identity

Guest cart identity is managed by the backend through the `GUEST_ID` cookie.
The client must not create, modify, or send an `X-Guest-Id` header.

When a request does not contain a valid `GUEST_ID` cookie, the backend creates
a new UUID, sets it in the response, and continues processing the same request:
-
```http
Set-Cookie: GUEST_ID=<uuid>; HttpOnly; Path=/; SameSite=Lax; Max-Age=2592000
```

If the existing cookie has an invalid or modified value, the backend ignores
it, creates a new UUID, overwrites the cookie, and continues processing the
request. The backend accepts only the canonical UUID format.

The `GUEST_ID` cookie identifies the guest cart only. It is not an
administrator credential and must not be used as a CSRF token or session ID.

The guest cart database record has one unique `guest_id`, so one guest ID maps
to at most one cart.

For browser clients, `GET /api/v1/csrf-token` must be called before the first
guest mutation request. The client must send the returned CSRF token in the
`X-XSRF-TOKEN` header for `POST`, `PATCH`, and `DELETE` guest endpoints.


## UC02: Add Product Variant to Cart

### REQUEST: ###

**Cookie:** `GUEST_ID` is sent automatically by the browser.

**Request Header:**

```http
X-XSRF-TOKEN: <value of the XSRF-TOKEN cookie>
```

**Endpoint:** `POST /api/v1/carts/items`

**Request Body**:
```json
{
  "variantId": 12345,
  "quantity": 2
}
```
**Request Parameter:** none

**Path parameters** none

### SUCCESS PATH:

**1. Success Response - New Cart Item Created**:
Status: `201 Created`
Body:
```json
{
  "status": 201,
  "code": "CART_ITEM_CREATED",
  "message": "Product added to cart successfully",
  "timestamp": "2026-10-02T14:30:00+07:00",
  "data": {
    "cartId": 10,
    "items": [
      {
        "cartItemId": 1,
        "variantId": 12345,
        "quantity": 2
      }
    ],
    "totalItems": 1,
    "totalQuantity": 2
  }
}
```

**2. Success Response - Existing Cart Item updated:"**
Status: `200 Ok`
Body:
```json
{
   "status": 200,
   "code": "EXISTING_CART_ITEM_UPDATED",
   "message": "Cart item updated successfully",
   "timestamp": "2026-10-2T14:30:00+7:00",
   "data":
   {
        "cartId": 10,
        "items":[
          {
             "cartItemId": 1,
             "variantId": 12345,
             "quantity": 4
          }
        ],
        "totalItems": 1,
        "totalQuantity": 4
    }
   
}
```


### BAD PATH:
**1. Error Response - invalid details provided:**
Status: `400 Bad Request`
Body:
```json
{
  "status": 400,
  "code": "INVALID_QUANTITY",
  "message": "Failed: Quantity must be greater than 0",
  "timestamp": "2026-10-2T14:30:00+7:00"
}
```
**1. Error Response - selected variant does not exist:**
Status: `404 Not Found`
Body:
```json
{
  "status": 404,
  "code": "PRODUCT_VARIANT_NOT_FOUND",
  "message": "Failed: Product Variant [id=12345] not found",
  "timestamp": "2026-10-2T14:30:00+7:00"
}
```

**2. Error Response - inactive product:**
Status: `409 Conflict`
Body:
```json
{
  "status": 409,
  "code": "PRODUCT_INACTIVE",
  "message": "Failed: Product asssociated with variant [id=12345] is inactive",
  "timestamp": "2026-10-2T14:30:00+7:00"
}
```

**3. Error Response - Insufficient stock quantity:**
Status: `409 Conflict`
Body:
```json
{
  "status": 409,
  "code": "INSUFFICIENT_STOCK",
  "message": "Failed: Stock quantity insufficient - [wantedQuantity=2] [availableQuantity=1]",
  "timestamp": "2026-10-2T14:30:00+7:00"
}
```

**4. Error Response - total quantity after merging exceeds the available stock:**
Status: `409 Conflict`
Body:
```json
{
  "status": 409,
  "code": "INSUFFICIENT_STOCK",
  "message": "Total cart quantity would exceed available stock",
  "timestamp": "2026-10-02T14:30:00+07:00",
  "details": {
    "existingQuantity": 3,
    "requestedQuantity": 2,
    "availableStock": 4
  }
}
```

The response may set `GUEST_ID` when the request did not contain a valid guest
cookie. The request is still processed normally.

## UC03: View Cart

### REQUEST:###

**Cookie:** `GUEST_ID` is sent automatically by the browser.

`unitPrice` and every `lineTotal` are calculated from the associated `Product.price`. Product variants do not have independent prices.

**Endpoint:** `GET /api/v1/carts`

**Request Body**: none

**Request Parameters:** none

**Path Parameters:** none

### SUCCESS PATH: ###
**1. Success Response - Cart has at least 1 cart items:**
Status: `200 Ok`
Body:
```json
{
   "status": 200,
   "code": "OK",
   "message": "Get cart with items successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data":{
       "cartId": 12,
       "items":[
          {
             "cartItemId": 1,
             "variantId": 123,
             "quantity": 2,
             "lineTotal":3000000 
          },
          {
             "cartItemId": 2,
             "variantId": 134,
             "quantity": 3,
             "lineTotal":100000
          }
       ],
       "subTotal": 3100000,
       "totalItems": 2,
       "totalQuantity": 5
   }
}
```

**2. Success Response - Empty cart:**
Status: `200 Ok`
Body:
```json
{
   "status": 200,
   "code": "CART_EMPTY",
   "message": "Empty cart",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data":{
       "items": [],
       "subTotal": 0,
       "totalItems": 0,
       "totalQuantity": 0
   }
}
```

**3. Success Response - Requested Quantity now exceeds the available stock:**
Status: `200 Ok`
Body:
```json
{
   "status": 200,
   "code": "CART_RETRIEVED",
   "message": "Cart retrieved successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data":{
       "cartId": 12,
       "items":[
          {
             "cartItemId": 1,
             "variantId": 123,
             "unitPrice": 1500000,
             "saleStatus": "ACTIVE",
             "quantity": 2,
             "availableQuantity": 3,
             "lineTotal": 3000000,
             "availability": "AVAILABLE"
          },
          {
             "cartItemId": 2,
             "variantId": 134,
             "unitPrice": 40000,
             "saleStatus": "ACTIVE",
             "quantity": 3,
             "availableQuantity": 2,
             "lineTotal": 120000,
             "availability": "STOCK_INSUFFICIENT"
          }
       ],
       "subTotal": 3120000,
       "totalItems": 2,
       "totalQuantity": 5
   }
}
```

**4. Success Response - A product has become `inactive`:**
Status: `200 OK`
Body:
```json
{
   "status": 200,
   "code": "CART_RETRIEVED",
   "message": "Cart retrieved successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data":{
       "cartId": 12,
       "items":[
          {
             "cartItemId": 1,
             "variantId": 123,
             "unitPrice": 1500000,
             "saleStatus": "INACTIVE",
             "quantity": 2,
             "availableQuantity": 3,
             "lineTotal": 3000000,
             "availability": "PRODUCT_INACTIVE"
          },
          {
             "cartItemId": 2,
             "variantId": 134,
             "unitPrice": 40000,
             "saleStatus": "ACTIVE",
             "quantity": 3,
             "availableQuantity": 2,
             "lineTotal": 120000,
             "availability": "STOCK_INSUFFICIENT"
          }
       ],
       "subTotal": 3120000,
       "totalItems": 2,
       "totalQuantity": 5
   }
}
```

### BAD PATH:

If the `GUEST_ID` cookie is missing or invalid, the backend creates and sets a
new cookie instead of returning a guest ID error.


## UC06: Place Order

### REQUESTS:

**Cookie:** `GUEST_ID` is sent automatically by the browser.

**Request Header:**

```http
X-XSRF-TOKEN: <value of the XSRF-TOKEN cookie>
```

`unitPrice` and every `lineTotal` are calculated from the associated `Product.price`. Product variants do not have independent prices.

**Endpoint**: `POST /api/v1/orders`

**Request Body:**
```json
{
  "customerName": "Phan Hai Anh",
  "phoneNumber": "09329838551",
  "email": "haianh260406@gmail.com",
  "deliveryAddress": "72A-KP3-T09, Phuong An Binh, Bien Hoa, Dong Nai"
}
```

**Request Parameters:** none

**Path Parameters**: none


### SUCCESS PATH:

**1. Success Response:**
Status: `201 CREATED`

**Response Headers**:
```
HTTP/1.1 201 Created
Content-Type: application/json
```

Body:
```json
{
   "status": 201,
   "code": "ORDER_CREATED",
   "message": "Order created successfully",
   "data":{
      "orderCode": "AB1233422MXY",
      "status": "PENDING",
      "createdAt": "2026-10-07T13:30:00+07:00",
      "items":[
        {
            "orderItemId":1,
            "variantId":132,
            "productName": "T-shirt hella",
            "size": "M",
            "quantity": 2,
            "color": "red",
            "unitPrice": "20000" 
        },
        {
            "orderItemId":2,
            "variantId":124,
            "productName": "Pants hella",
            "size": "XL",
            "quantity": 3,
            "color": "black",
            "unitPrice": "30000" 
        }
      ],
      "totalItems":2,
      "totalQuantity":5,
      "totalPrice": "130000"
   }
}
```


### BAD PATH:

**1. Error Response - empty cart:**
Status: `422 Unprocessable Entity`
Body:
```json
{
   "status": 422,
   "code": "CART_EMPTY",
   "message": "Failed: There are no items in cart to place order",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

**2. Error Response - cart does not exist:**
Status: `404 Not Found`
Body:
```json
{
   "status": 404,
   "code": "CART_NOT_FOUND",
   "message": "Failed: Cart not found",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

**3. Error Response - Invalid customer's information ( wrong format ):**
Status: `400 Bad Request`
Body:
```json
{
   "status": 400,
   "code": "INVALID_INFORMATION_PROVIDED",
   "message": "Failed: Invalid details provided",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "error":[
     {
        "field": "customerName",
        "message": "Failed: Invalid Customer's Name"
     },
     {
        "field": "phoneNumber",
        "message": "Failed: Invalid phoneNumber"
     },
     {
        "field": "email",
        "message": "Failed: Invalid email"
     },
     {
        "field": "deliveryAddress",
        "message": "Failed: Invalid delivery address"
     }
   ]
}
```

**4. Error Response - a product or variant is unavailable, inactive, soft-deleted, or has insufficient stock:**
Status: `422 Unprocessable Entity`
Body:
```json
{
   "status": 422,
   "code": "PRODUCT_UNAVAILABLE",
   "message": "Failed:",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "error":[
      {
         "variantId": 132,
         "availability": "PRODUCT_INACTIVE"
      },
      {
         "variantId": 125,
         "availability": "VARIANT_DELETED"
      },
      {
         "variantId": 124,
         "availability": "STOCK_INSUFFICIENT"
      }
   ]
}
```

**5. Error Response -  any operation fails while creating the order, creating order items, decreasing stock, or clearing the cart:**
Status: `409 Conflict`
Body:
```json
{
   "status": 409,
   "code": "OPERATION_FAILED",
   "message": "Failed: There is a problem while handling placing order",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

The response may set `GUEST_ID` when the request did not contain a valid guest
cookie. The request is still processed normally.



# UC07 Guest Order History and Order Lookup

Guest order access uses the backend-managed `GUEST_ID` cookie. The client must
not send a guest ID in the URL, request body, or custom header.

## UC07.1 View Guest Order History

This endpoint returns only orders belonging to the current guest cookie. It
returns order summaries and does not expose delivery address, phone number,
email, or full order items in the list response.

**Endpoint:** `GET /api/v1/orders`

**Cookie:** `GUEST_ID` is sent automatically by the browser.

`GET` requests do not require the `X-XSRF-TOKEN` header.

**Request Body:** none

**Request Parameters:**

| Parameter | Type | Required | Default | Description |
| :-------- | :--- | :------- | :------ | :---------- |
| `page` | integer | No | `0` | Zero-based page number |
| `size` | integer | No | `20` | Number of orders per page; maximum `100` |
| `sort` | string | No | `createdAt,desc` | Sorting expression |

**Path Parameters:** none

### SUCCESS PATH:

If the guest has no orders, the endpoint returns `200 OK` with an empty
`items` list. If the `GUEST_ID` cookie is missing or invalid, the backend
creates and sets a new cookie and returns the same empty-history response.

```json
{
   "status": 200,
   "code": "GUEST_ORDERS_RETRIEVED",
   "message": "Guest orders retrieved successfully",
   "timestamp": "2026-10-29T14:30:00+07:00",
   "data": {
      "items": [
         {
            "orderCode": "AB1233422MXY",
            "status": "PENDING",
            "totalItems": 2,
            "totalQuantity": 5,
            "totalPrice": 2040000,
            "createdAt": "2026-10-07T13:30:00+07:00"
         }
      ],
      "pagination": {
         "page": 0,
         "size": 20,
         "totalElements": 1,
         "totalPages": 1,
         "first": true,
         "last": true
      }
   }
}
```

## UC07.2 View Guest Order Detail

The backend must match both the path order code and the current guest cookie.
An order belonging to another guest is returned as `404`, so the API does not
reveal whether that order code exists.

**Endpoint:** `GET /api/v1/orders/{orderCode}`

**Cookie:** `GUEST_ID` is sent automatically by the browser.

`GET` requests do not require the `X-XSRF-TOKEN` header.

**Request Body:** none

**Path Parameters:**

| Parameter | Type | Required | Description |
| :-------- | :--- | :------- | :---------- |
| `orderCode` | string | Yes | The order code to retrieve |

### SUCCESS PATH:

**1. Success Response:**
Status: `200 OK`

```json
{
   "status": 200,
   "code": "ORDER_DETAIL_RETRIEVED",
   "message": "Order detail retrieved successfully",
   "timestamp": "2026-10-29T14:30:00+07:00",
   "data": {
      "orderCode": "AB1233422MXY",
      "status": "PENDING",
      "customerName": "Phan Hai Anh",
      "phoneNumber": "09329838551",
      "email": "haianh260406@gmail.com",
      "deliveryAddress": "72A-KP3-T09, Phuong An Binh, Bien Hoa, Dong Nai",
      "totalPrice": 2040000,
      "createdAt": "2026-10-07T13:30:00+07:00",
      "items": [
         {
            "orderItemId": 1,
            "variantId": 132,
            "productName": "T-shirt hella",
            "size": "M",
            "quantity": 2,
            "color": "red",
            "unitPrice": 20000
         }
      ]
   }
}
```

## UC07.3 Recover Order by Order Code and Phone Number

This endpoint supports recovery when the guest loses the `GUEST_ID` cookie or
uses another browser/device. It is independent of the guest cookie and should
be rate-limited because it uses customer-provided identifying information.

**Endpoint:** `GET /api/v1/orders/lookup`

**Request Body:** none

**Query Parameters:**

| Parameter | Type | Required | Description |
| :-------- | :--- | :------- | :---------- |
| `orderCode` | string | Yes | The order code |
| `phoneNumber` | string | Yes | The phone number used when placing the order |

`GET` requests do not require the `X-XSRF-TOKEN` header.

### BAD PATH:

**1. Error Response - order not found or ownership information does not match:**
Status: `404 Not Found`

```json
{
   "status": 404,
   "code": "ORDER_NOT_FOUND",
   "message": "Order was not found",
   "timestamp": "2026-10-29T14:30:00+07:00"
}
```

The API must use the same response for an unknown order code, a guest-owned
order requested with another `GUEST_ID`, or a mismatched phone number.


# Supporting API contract flow:


## UC01 Browse All Products

### REQUEST:

**Endpoint:** `GET /api/v1/products`

**Request Body:** none

**Request Parameters:**

| Parameter | Type | Required | Default | Description |
| :-------- | :--- | :------- | :------ | :---------- |
| `page` | integer | No | `0` | Zero-based page number |
| `size` | integer | No | `20` | Number of products per page; maximum `100` |
| `sort` | string | No | Spring default | Sorting expression, for example `id,asc` |

**Path Parameters**: none

### SUCCESS PATH:

**1. Success Response - There are `ACTIVE` products:**
Status: `200 Ok`
Body:
```json
{
   "status": 200,
   "code": "PRODUCT_RETRIEVED_SUCCESSFULLY",
   "message": "Product retrieving successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data": {
     "categories":[
       {
          "category": "TOPS",
          "items":[
             {
                "productId": 1,
                "productName": "T-shirt 01",
                "description": "......",
                "unitPrice": "50000"
             },
             {
                "productId": 2,
                "productName": "Jacket 02",
                "description": "......",
                "unitPrice": "120000"
             }
          ]
       },
       {
          "category": "BOTTOMS",
          "items":[
             {
                "productId": 3,
                "productName": "Pants 01",
                "description": "......",
                "unitPrice": "40000"
             },
             {
                "productId": 4,
                "productName": "Shorts 03",
                "description": "......",
                "unitPrice": "30000"
             }
          ]
       },
       {
          "category": "ACCESSORIES",
          "items":[
             {
                "productId": 7,
                "productName": "Necklace 07",
                "description": "......",
                "unitPrice": "80000"
             },
             {
                "productId": 10,
                "productName": "Wrist 05",
                "description": "......",
                "unitPrice": "110000"
             }
          ]
       }
     ],
     "pagination": {
        "page": 0,
        "size": 20,
        "totalElements": 6,
        "totalPages": 1,
        "first": true,
        "last": true
     }
   }
}
```

**2. Success Response - There are NO `ACTIVE` products:**
Status: `200 Ok`
Body:
```json
{
   "status": 200,
   "code": "PRODUCT_RETRIEVED_SUCCESSFULLY",
   "message": "Product retrieving successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data": {
     "categories":[
       {
          "category": "TOPS",
          "items":[]
       },
       {
          "category": "BOTTOMS",
          "items":[]
       },
       {
          "category": "ACCESSORIES",
          "items":[]
       }
     ],
     "pagination": {
        "page": 0,
        "size": 20,
        "totalElements": 0,
        "totalPages": 0,
        "first": true,
        "last": true
     }
   }
}
```



## UC01.1  Browse Product Variants

### REQUEST:

**Endpoint:** `GET /api/v1/products/{productId}/variants`

**Request Body:** none

**Request Parameters:**

| Parameter | Type | Required | Default | Description |
| :-------- | :--- | :------- | :------ | :---------- |
| `page` | integer | No | `0` | Zero-based page number |
| `size` | integer | No | `20` | Number of variants per page; maximum `100` |
| `sort` | string | No | Spring default | Sorting expression, for example `id,asc` |

**Path Parameters**:

| Parameter   | Type | Required |
| :---------- | :--- | :------- |
| `productId` | Long | Yes      |

### SUCCESS PATH:

**1. Success Response:**
Status: `200 Ok`
Body:
```json
{
   "status": 200,
   "code": "PRODUCT_VARIANT_RETRIEVED_SUCCESSFULLY",
   "message": "Product's Variants retrieving successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data": {
      "productName": "T-Shirt",
      "description": ".............",
      "category": "TOPS",
      "price": 30000,
      "items":[
         {
            "variantId": 1,
            "color": "red",
            "size": "M",
            "quantity": 5,
            "imageUrl": "https://....."
         },
         {
            "variantId": 2,
            "color": "blue",
            "size": "XL",
            "quantity": 4,
            "imageUrl": "https://....."
         }
      ],
      "pagination": {
         "page": 0,
         "size": 20,
         "totalElements": 4,
         "totalPages": 1,
         "first": true,
         "last": true
      }
   }
}
```


### BAD PATH:

**1. Error Response - product not found:**
Status: `404 Not Found`
Body:
```json
{
   "status": 404,
   "code": "PRODUCT_NOT_FOUND",
   "message": "Failed: Product [id=123] not found"
}
```

**2. Error Response - product `INACTIVE`:**
Status: `422 Unprocessable Entity`
Body:
```json
{
   "status": 422,
   "code": "PRODUCT_INACTIVE",
   "message": "Failed: Product [id=123] is inactive"
}
```



## UC01.2 Browse Products by Category

### REQUEST:

Category is a collection filter, so it is represented as a query parameter on the product collection endpoint rather than as a separate resource path.

**Endpoint:** `GET /api/v1/products?category=TOPS`

**Request Body:** none

**Request Parameters:**

| Parameter | Type | Required | Default | Description |
| :-------- | :--- | :------- | :------ | :---------- |
| `category` | string | Yes | none | Product category filter. Supported values: `TOPS`, `BOTTOMS`, `ACCESSORIES`; case-sensitive |
| `page` | integer | No | `0` | Zero-based page number |
| `size` | integer | No | `20` | Number of products per page; maximum `100` |
| `sort` | string | No | Spring default | Sorting expression, for example `id,asc` |

**Path Parameters:** none

### SUCCESS PATH:

**1. Success Response - matching `ACTIVE` products exist:**
Status: `200 OK`
Body:
```json
{
   "status": 200,
   "code": "PRODUCT_RETRIEVED_SUCCESSFULLY",
   "message": "Product retrieving successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data": {
      "category": "TOPS",
      "items": [
         {
            "productId": 1,
            "productName": "T-shirt 01",
            "description": "......",
            "unitPrice": "50000"
         },
         {
            "productId": 2,
            "productName": "Jacket 02",
            "description": "......",
            "unitPrice": "120000"
         }
      ],
      "pagination": {
         "page": 0,
         "size": 20,
         "totalElements": 2,
         "totalPages": 1,
         "first": true,
         "last": true
      }
   }
}
```

**2. Success Response - no matching `ACTIVE` products:**
Status: `200 OK`
Body:
```json
{
   "status": 200,
   "code": "PRODUCT_RETRIEVED_SUCCESSFULLY",
   "message": "Product retrieving successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data": {
      "category": "TOPS",
      "items": [],
      "pagination": {
         "page": 0,
         "size": 20,
         "totalElements": 0,
         "totalPages": 0,
         "first": true,
         "last": true
      }
   }
}
```

### BAD PATH:

**1. Error Response - missing category:**
Status: `400 Bad Request`
Body:
```json
{
   "status": 400,
   "code": "PRODUCT_CATEGORY_REQUIRED",
   "message": "Query parameter 'category' is required",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

**2. Error Response - invalid category:**
Status: `400 Bad Request`
Body:
```json
{
   "status": 400,
   "code": "INVALID_PRODUCT_CATEGORY",
   "message": "Failed: Unsupported product category [category=SHOES]",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```


## UC04 Update Cart Item Quantity

### REQUEST:

**Cookie:** `GUEST_ID` is sent automatically by the browser.

**Request Header:**

```http
X-XSRF-TOKEN: <value of the XSRF-TOKEN cookie>
```

`unitPrice` and every `lineTotal` are calculated from the associated `Product.price`. Product variants do not have independent prices.

**Endpoint:** `PATCH /api/v1/cartItems/{cartItemId}`

**Request Body**:
```json
{"quantity": 5}
```

**Path Parameter:** `cartItemId` - `int` - `required`

**Query Parameter:** none


### SUCCESS PATH:

**1. Success Response:**
Status: `200 Ok`
Body:
```json
{
   "status": 200,
   "code": "CART_ITEM_QUANTITY_UPDATED",
   "message": "Cart item [id=xxx] quantity updated successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data":{
       "cartId": 12,
       "items":[
          {
             "cartItemId": 1,
             "variantId": 123,
             "saleStatus": "ACTIVE",
             "productName": "Bleach T-shirt",
             "imageUrl": "https://anh-Bleach-T-shirt.jpg",
             "color": "red",
             "size": "M",
             "unitPrice": "1500000",
             "quantity": 2,
             "availableQuantity":3,
             "lineTotal":"3000000",
             "availability": "AVAILABLE"
          },
          {
             "cartItemId": 2,
             "variantId": 134,
             "saleStatus": "ACTIVE",
             "productName": "Black Panther Pants",
             "imageUrl": "https://anh-BlackPanther-Pants.jpg",
             "color": "black",
             "size": "L",
             "unitPrice": "40000",
             "quantity": 3,
             "availableQuantity":3,
             "lineTotal":"120000",
             "availability": "AVAILABLE"
          }
       ],
       "subTotal": 3120000,
       "totalItems": 2,
       "totalQuantity": 5
   }
}
```

**2. Success Response - new quantity = old quantity**:
Status: `200 Ok`
```json
{
   "status": 200,
   "code": "CART_ITEM_UNCHANGED",
   "message": "Cart item [id=xxx] quantity remains unchanged",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data":{
       "cartId": 12,
       "items":[
          {
             "cartItemId": 1,
             "variantId": 123,
             "saleStatus": "ACTIVE",
             "productName": "Bleach T-shirt",
             "imageUrl": "https://anh-Bleach-T-shirt.jpg",
             "color": "red",
             "size": "M",
             "unitPrice": "1500000",
             "quantity": 2,
             "availableQuantity":3,
             "lineTotal":"3000000",
             "availability": "AVAILABLE"
          },
          {
             "cartItemId": 2,
             "variantId": 134,
             "saleStatus": "ACTIVE",
             "productName": "Black Panther Pants",
             "imageUrl": "https://anh-BlackPanther-Pants.jpg",
             "color": "black",
             "size": "L",
             "unitPrice": "40000",
             "quantity": 3,
             "availableQuantity":3,
             "lineTotal":"120000",
             "availability": "AVAILABLE"
          }
       ],
       "subTotal": 3120000,
       "totalItems": 2,
       "totalQuantity": 5
   }
}
```


### BAD PATH:

**1. Error Response - cart not found:**
Status: `404 Not Found`
Body:
```json
{
   "status": 404,
   "code": "CART_NOT_FOUND",
   "message": "Failed: Cart not found",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

**2. Error Response - cart item not found:**
Status: `404 Not Found`
Body:
```json
{
   "status": 404,
   "code": "CART_ITEM_NOT_FOUND",
   "message": "Failed: Cart item [id=xxx] not found",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

**3. Error Response - new quantity is less than 1:**
Status: `400 Bad Request`
Body:
```json
{
   "status": 400,
   "code": "INVALID_QUANTITY",
   "message": "Failed: New Quantity must not be less than one",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

**4. Error Response - new quantity exceeds the available stock:**
Status: `409 Conflict`
Body:
```json
{
  "status": 409,
  "code": "INSUFFICIENT_STOCK",
  "message": "Failed: Stock quantity insufficient - [wantedQuantity=2] [availableQuantity=1]",
  "timestamp": "2026-10-2T14:30:00+7:00"
}
```

**5. Error Response - product - variant no longer available:**
Status: `409 Conflict`
Body:
```json
{
   "status": 409,
   "code": "PRODUCT_INACTIVE",
   "message": "Failed: Product [id=xxx] no longer available",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

```json
{
   "status": 409,
   "code": "INSUFFICIENT_STOCK",
   "message": "Failed: Stock quantity insufficient - [wantedQuantity=x] [availableQuantity=0]",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```



## UC05 Remove Item from Cart

### REQUEST:

**Cookie:** `GUEST_ID` is sent automatically by the browser.

**Request Header:**

```http
X-XSRF-TOKEN: <value of the XSRF-TOKEN cookie>
```

`unitPrice` and every `lineTotal` are calculated from the associated `Product.price`. Product variants do not have independent prices.

**Endpoint:** `DELETE /api/v1/cartItems/{cartItemId}`

**Path Parameters:** `cartItemId` - `Long` - `Required`

**Request Body:** none

**Query parameters:** none


### SUCCESS PATH:

**1. Success Response:**
Endpoint example: `DELETE /api/v1/cartItems/134`
Status: `200 OK`
Body:
```json
{
   "status": 200,
   "code": "CART_ITEM_DELETED",
   "message": "Cart item [id=xxx] is deleted successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data":{
       "cartId": 12,
       "items":[
          {
             "cartItemId": 1,
             "variantId": 123,
             "saleStatus": "ACTIVE",
             "productName": "Bleach T-shirt",
             "imageUrl": "https://anh-Bleach-T-shirt.jpg",
             "color": "red",
             "size": "M",
             "unitPrice": "1500000",
             "quantity": 2,
             "availableQuantity":3,
             "lineTotal":"3000000",
             "availability": "AVAILABLE"
          }
       ],
       "subTotal": "3000000",
       "totalItems": 1,
       "totalQuantity": 2
   }
}
```

### BAD PATH::

**1. Error Response - Cart or Cart Item not found:**
Status: `404 Not Found`
Body:
```json
{
   "status": 404,
   "code": "CART_NOT_FOUND",
   "message": "Failed: Cart not found",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

```json
{
   "status": 404,
   "code": "CART_ITEM_NOT_FOUND",
   "message": "Failed: Cart item [id=xxx] not found",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```


## UC08 Administrator Authentication CSRF Contract

The API uses server-side sessions stored behind the `ADMIN_SESSION` cookie.
Because browsers send cookies automatically, browser clients must obtain a
CSRF token before sending state-changing requests such as login and logout.

### CSRF TOKEN REQUEST

**Endpoint:** `GET /api/v1/csrf-token`

**Request Body:** none

**Response:** `200 OK` with an empty body

The response sets a non-HttpOnly cookie:

```http
Set-Cookie: XSRF-TOKEN=<csrf-token>; Path=/; SameSite=Lax
```

The frontend reads the `XSRF-TOKEN` cookie and sends the same value in the
`X-XSRF-TOKEN` header on subsequent state-changing requests. The CSRF token is
not an administrator credential and must not be used as the session ID.

### CSRF-PROTECTED REQUEST HEADER

```http
X-XSRF-TOKEN: <value of the XSRF-TOKEN cookie>
```

`GET` requests such as UC08.1 do not require this header. Login and logout do
require it while CSRF protection is enabled.

## UC08 Administrator Login

### REQUEST: ###

**Endpoint:** `POST /api/v1/admin/sessions`

**Request Header:**

```http
X-XSRF-TOKEN: <value of the XSRF-TOKEN cookie>
```

The endpoint creates an authenticated server-side administrator session. The
session identifier is returned in the `Set-Cookie` response header, not in the
JSON response body.

After successful authentication, the system must rotate the session identifier.
The session identifier that existed before authentication must not remain valid
for the authenticated session. The session identifier is opaque and must not
contain the administrator email, password, password hash, role, or other
personal data.

**Request Body:**
```json
{
   "email": "haianh2504088@gmail.com",
   "password": "rawPassword"
}
```

**Path Parameters:** none

**Query Parameters:** none


### SUCCESS PATH:

**1. Success Response:**
Status: `200 OK`

Response headers (the server creates the cookie):
```http
Content-Type: application/json
Set-Cookie: ADMIN_SESSION=<opaque-session-id>; HttpOnly; Secure; SameSite=Lax; Path=/; Max-Age=1800
```

Body:
```json
{
   "status": 200,
   "code": "ADMIN_LOGIN_SUCCESS",
   "message": "Admin login successful",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data": {
      "authenticated": true
   }
}
```

### BAD PATH: ###

**1. Error Response - Too many login attempts:**
Status: `429 Too Many Requests`

Response headers:
```http
Retry-After: 60
```

Body:
```json
{
   "status": 429,
   "code": "TOO_MANY_REQUEST",
   "message": "Too many login attempts. Please try again later.",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

**2. Error Response - Invalid credentials:**
Status: `401 Unauthorized`
Body:
```json
{
   "status": 401,
   "code": "INVALID_CREDENTIALS",
   "message": "Invalid email or password",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

The response must not reveal whether the email address exists.

**3. Error Response - Invalid information:**
Status: `400 Bad Request`
Body:
```json
{
   "status": 400,
   "code": "INVALID_INFORMATION_PROVIDED",
   "message": "Failed: Invalid details provided",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "error": [
      {
         "field": "email",
         "message": "Email must not be blank"
      },
      {
         "field": "password",
         "message": "Password must not be blank"
      }
   ]
}
```

The `error` list contains field-specific validation messages for missing,
blank, malformed, or otherwise invalid values.


## UC08.1 View Current Administrator Session

### REQUEST: ###

**Endpoint:** `GET /api/v1/admin/sessions/current`

**Request Header (the browser sends the cookie automatically; example only):**
```http
Cookie: ADMIN_SESSION=abc123
```

The request must include the administrator session cookie created by UC08. The
client must not construct, modify, or expose the opaque session identifier in
the URL or JSON body.

**Request Body:** none

**Path Parameters:** none

**Query Parameters:** none


### SUCCESS PATH:

**1. Success Response:**
Status: `200 OK`
`Content-Type: application/json`
Body:
```json
{
   "status": 200,
   "code": "ADMIN_SESSION_RETRIEVED",
   "message": "Current admin session retrieved successfully",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data": {
      "authenticated": true,
      "email": "haianh2504088@gmail.com"
   }
}
```

### BAD PATH: ###

**1. Error Response - Session is missing, invalid, or expired:**
Description:
- Will be handled and checked by SecurityContextPersistenceFilter / Repository
- Request become `anomynous`
- Throw `401` before controller approach

Status: `401 Unauthorized`
Body:
```json
{
   "status": 401,
   "code": "ADMIN_SESSION_UNAUTHORIZED",
   "message": "Admin session is missing, invalid, or expired",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

## UC08.2 Administrator Logout

### REQUEST: ###

**Endpoint:** `DELETE /api/v1/admin/sessions/current`

**Request Header:**

```http
X-XSRF-TOKEN: <value of the XSRF-TOKEN cookie>
```

The `ADMIN_SESSION` cookie is optional for this idempotent logout request, but
the CSRF header is still required.

The browser sends the administrator session cookie automatically when a session
is active. The operation is idempotent: an already missing or expired session
is treated as logged out.

**Request Body:** none

**Path Parameters:** none

**Query Parameters:** none


### SUCCESS PATH:

**1. Success Response:**
Status: `200 OK`

Response headers (the server expires the cookie):
```http
Content-Type: application/json
Set-Cookie: ADMIN_SESSION=; HttpOnly; Secure; SameSite=Lax; Path=/; Max-Age=0
```

Body:
```json
{
   "status": 200,
   "code": "ADMIN_LOGOUT_SUCCESS",
   "message": "Admin logout successful",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data": {
      "authenticated": false
   }
}
```

The `Set-Cookie` header must use the same cookie name, `Path`, `Secure`, and
`SameSite` attributes as the login response, with `Max-Age=0` to expire the
cookie.

### BAD PATH: ###

The operation is idempotent and therefore does not return an error when the
session cookie is missing, invalid, or already expired. It returns the same
successful response described above.


## UC09 Create Product

**Endpoint:** `POST /api/v1/admin/products`
**Request Header:** `X-XSRF-TOKEN: <CSRF-TOKEN>`
**Role:** `ADMIN`
**Request Body:**
```json
{
   "productName": "XXXXXXXX",
   "description": "......",
   "category": "TOPS",
   "basePrice": 1000000
}
```

### AUTHENTICATION AND AUTHORIZATION ERRORS:

**1. Error Response - administrator is not authenticated:**
Status: `401 Unauthorized`

The request does not contain a valid authenticated administrator session. The
server does not create a product.

```json
{
   "status": 401,
   "code": "ADMIN_SESSION_UNAUTHORIZED",
   "message": "Administrator authentication is required",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

**2. Error Response - authenticated user is not an administrator:**
Status: `403 Forbidden`

The session is authenticated, but the authenticated user does not have the
`ADMIN` role. The server does not create a product.

```json
{
   "status": 403,
   "code": "ADMIN_ACCESS_FORBIDDEN",
   "message": "Administrator access is required",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

**3. Error Response - CSRF token is missing or invalid:**
Status: `403 Forbidden`

The request is authenticated or reaches the protected endpoint, but the
`X-XSRF-TOKEN` header is missing or does not match the CSRF cookie. The server
rejects the request before product creation.

```json
{
   "status": 403,
   "code": "CSRF_TOKEN_INVALID",
   "message": "Invalid or missing CSRF token",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

### SUCCESS PATH:
**1. Success Response:**
Status: `201 CREATED`
Body:
```json
{
   "status": 201,
   "code": "PRODUCT_CREATED_SUCCESSFULLY",
   "message": "Successfully creating new product",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data":{
       "productId": 123,
       "productName": "XXXXXXXX",
       "description": "......",
       "category": "TOPS",
       "basePrice": 1000000,
       "saleStatus": "INACTIVE"
   }
}
```

### BAD PATH:
**1. Error Response - required product information is missing or null:**
Status: `400 Bad Request`
Body:
```json
{
   "status": 400,
   "code": "REQUIRED_INFORMATION_MISSING",
   "message": "Failed: Missing required information",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "error":[
       {
           "field": "productName",
           "message": "ProductName is required"
       },
       {
           "field": "basePrice",
           "message": "BasePrice is required"
       },
     {
       "field": "category",
       "message": "Category is required"
     },
     {
       "field": "description",
       "message": "Description is required"
     }
   ]
}
```

**2. Error Response - required product information is invalid:**
Status: `400 Bad Request`
Body:
```json
{
   "status": 400,
   "code": "INVALID_INFORMATION_PROVIDED",
   "message": "Failed: Invalid details provided",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "error": [
      {
         "field": "productName",
         "message": "Invalid Product's name"
      },
      {
         "field": "category",
         "message": "Invalid category"
       },
       {
          "field": "basePrice",
          "message": "Invalid base price"
       },
       {
          "field": "description",
          "message": "Invalid description"
       }
    ]
}
```


## UC10 Update Product ( Basic Information Only )

**Endpoint:** `PATCH /api/v1/admin/products/{productId}`
**Request Header:** `X-XSRF-TOKEN: <CSRF-TOKEN>`
**Role:** `ADMIN`
**Path Variable:** `productId - LONG - required`

This endpoint supports partial updates. The request body must contain only the
basic product fields that need to change: `productName`, `description`,
`category`, and/or `basePrice`. Omitted fields retain their current values.
At least one updatable field must be provided.

The endpoint cannot update `saleStatus`, product variants, stock, size, color,
or images. Sale status changes are handled by UC14, and variant changes are
handled by UC11–UC13.

**Request Body:**
```json
{
   "basePrice": 2000000
}
```

### AUTHENTICATION, AUTHORIZATION, AND CSRF ERRORS:

UC10 inherits the authentication, authorization, and CSRF error contract from
UC09. The same response envelope, status codes, error codes, and behavior apply:

- `401 Unauthorized` with `ADMIN_SESSION_UNAUTHORIZED` when the administrator
  session is missing, invalid, or expired.
- `403 Forbidden` with `ADMIN_ACCESS_FORBIDDEN` when the authenticated user does
  not have the `ADMIN` role.
- `403 Forbidden` with `CSRF_TOKEN_INVALID` when the CSRF token is missing or
  invalid.

The server does not update the product in any of these cases. See the UC09
authentication and authorization error responses for the canonical response
envelopes.


## SUCCESS PATH:
**1. Success Response:**
Status: `200 OK`
Body:
```json
{
   "status": 200,
   "code": "PRODUCT_UPDATE_SUCCESSFULLY",
   "message": "Successfully updating product",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "data":{
       "productId": 123,
       "productName": "XXXXXXXX",
       "description": "......",
       "category": "BOTTOMS",
       "basePrice": 2000000,
       "saleStatus": "INACTIVE"
   }
}
```


### BAD PATH:
**1. Error Response - Product not found:**
Status: `404 Not Found`
Body:
```json
{
   "status": 404,
   "code": "PRODUCT_NOT_FOUND",
   "message": "Product not found",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```

**2. Error Response - new product information is invalid:**
Status: `400 Bad Request`
Body:
```json
{
   "status": 400,
   "code": "INVALID_INFORMATION_PROVIDED",
   "message": "Failed: Invalid details provided",
   "timestamp": "2026-10-02T14:30:00+07:00",
   "error": [
      {
         "field": "productName",
         "message": "Invalid Product's name"
      },
      {
         "field": "description",
         "message": "Invalid description"
      },
      {
         "field": "category",
         "message": "Invalid category"
      },
      {
         "field": "basePrice",
         "message": "Base price must be greater than zero"
      }
   ]
}
```

**3. Error Response - product name already exists:**
Status: `409 Conflict`
Body:
```json
{
   "status": 409,
   "code": "PRODUCT_NAME_ALREADY_EXISTS",
   "message": "Product name is already in use",
   "timestamp": "2026-10-02T14:30:00+07:00"
}
```
