# Core API Contract Flow Design:

## UC having `X-Guest-Id` in header:##
**1. Error Response - Missing `X-Guest-Id` in header:**
Status: `400 Bad Request`
Body:
```json
{
   "status": 400,
   "code": "X_GUEST_ID_MISSING",
   "message": "Failed: Required header 'X-Guest-Id' is missing",
   "timestamp": "2026-10-06T15:00:00+07:00"
}
```

**2. Error Response - Invalid `X-Guest-Id`:**
Status: `400 Bad Request`
Body:
```json
{
  "status": 400,
  "code": "GUEST_ID_INVALID",
  "message": "Header 'X-Guest-Id' has an invalid format",
  "timestamp": "2026-10-06T15:00:00+07:00"
}
```


## UC02: Add Product Variant to Cart

### REQUEST: ###

**Header**: chứa `X-Guest-Id`

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

**5. Error Responses about `X-Guest-Id` in header**

## UC03: View Cart

### REQUEST:###

**Header:** `X-Guest-Id`

**Endpoint:** `GET /api/v1/carts/items`

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
**Error Responses about `X-Guest-Id` in header**

## UC06: Place Order
