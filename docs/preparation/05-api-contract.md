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

### REQUESTS:

**Header:** `X-Guest-Id`

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

**4. Error Response - a product is inactive or unavailable or has insufficient stock:**
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

**6. Error Responses about `X-Guest-Id` in header**



# Supporting API contract flow:


## UC01 Browse All Products

### REQUEST:

**Endpoint:** `GET /api/v1/products`

**Request Body:** none

**Request Parameters:** none

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
   "data":[
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
   ]
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
   "data":[
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
   ]
}
```



## UC01.1  Browse Product Variants

### REQUEST:

**Endpoint:** `GET /api/v1/products/{productId}/variants`

**Request Body:** none

**Request Parameters:** none

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
      },
      {
         "variantId": 3,
         "color": "blue",
         "size": "M",
         "quantity": 6,
         "imageUrl": "https://....."
      },
      {
         "variantId": 4,
         "color": "red",
         "size": "XL",
         "quantity": 0,
         "imageUrl": "https://....."
      }
   ]
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



## UC01.2  Filter Products

### REQUEST: 


## UC04 Update Cart Item Quantity

### REQUEST:

**Header:** `X-Guest-Id`

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

**Header**: `X-Guest-Id`

**Endpoint:** `DELETE /api/v1/cartItems/{cartItemId}`

**Path Parameters:** `cartItemId` - `Long` - `Required`

**Request Body:** none

**Query parameters:** none


### SUCCESS PATH:

**1. Success Response:**
Endpoint example: `DELETE /api/v1/cartItems/134`
Status: `204 No Content`
Body:
```json
{
   "status": 204,
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

