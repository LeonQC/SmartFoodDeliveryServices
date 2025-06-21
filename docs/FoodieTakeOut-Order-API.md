# FoodieTakeOut API Documentation

## Overview

This document outlines the API endpoints for the order-related features, which includes:

1. **Client Order Module**
   1. Submit new order
   2. Pay for order
   3. Cancel order
   4. View historical orders
   5. View order details
   6. Order again
   7. Remind the merchant to confirm the order
   8. Track order status & location (Map)

2. **Merchant Order Module**
   1. Query orders (with filters & pagination)
   2. Orders statistics by status
   3. View order details
   4. Confirm order
   5. Reject order
   6. Mark order ready for pickup

3. **Rider Order Module**
   1. Accept order
   2. Reject order
   3. Mark order picked up (out for delivery)
   4. Get shortest route for delivery
   5. Mark order delivered (complete)

## API Endpoints

### 1. Client Order Module

#### 1.1. Submit New Order

**Purpose:** Create a brand-new order for a given client and merchant.

**Endpoint:** `/client/orders`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter       | Type    | Required | Description                                           |
| --------------- | ------- | -------- | ----------------------------------------------------- |
| `clientId`      | integer | Yes      | ID of the client placing this order                   |
| `merchantId`    | integer | Yes      | ID of the merchant receiving the order                |
| `items`         | array   | Yes      | List of order items                                   |
| └─ `dishId`     | integer | Yes      | ID of the dish                                        |
| └─ `quantity`   | integer | Yes      | Quantity ordered                                      |
| └─ `remark`     | string  | No       | Special requests (e.g., “no onions”)                  |
| `addressId`     | integer | Yes      | ID of the client_address table                        |  <!-- 在创建地址簿后更新 -->

**Request Body Sample:**
```json
{
  "clientId": 1001,
  "merchantId": 2001,
  "items": [
    { "dishId": 3001, "quantity": 2, "remark": "no onions" },
    { "dishId": 3002, "quantity": 1 }
  ],
  "addressId": 5001
}
```

**Response Parameter Description:**
| Parameter       | Type    | Required | Description                              |
| --------------- | ------- | -------- | ---------------------------------------- |
| `code`          | number  | Yes      | 1 = success; 0 = failure                 |
| `msg`           | string  | No       | Human-readable message                   |
| `data`          | object  | Yes      | Summary of the newly created order       |
| └─ `orderId`    | integer | Yes      | Unique ID of the created order           |
| └─ `status`     | number  | Yes      | Initial status (0 = pending payment)     |
| └─ `createTime` | string  | Yes      | Timestamp when the order was created     |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Order created successfully",
  "data": {
    "orderId": 4001,
    "status": 0,
    "createTime": "2025-06-16T10:00:00.000Z"
  }
}
```

#### 1.2. Pay for Order

**Purpose:** Capture payment for an existing order.

**Endpoint:** `/client/orders/{orderId}/pay`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter             | Type    | Required | Description                                       |
| --------------------- | ------- | -------- | ------------------------------------------------- |
| Path `orderId`        | integer | Yes      | ID of the order to pay                            |
| Body `paymentMethod`  | string  | Yes      | Payment method used (e.g., ApplePay, credit card) |
| Body `paymentDetails` | object  | Yes      | Gateway-specific payment payload                  |
| └─ `transactionId`    | string  | Yes      | External transaction reference                    |

**Request Body Sample:**
```json
{
  "paymentMethod": "ApplePay",
  "paymentDetails": { "transactionId": "txn_1234567890" }
}
```

**Response Parameter Description:**
| Parameter          | Type    | Required | Description                                           |
| ------------------ | ------- | -------- | ----------------------------------------------------- |
| `code`             | number  | Yes      | 1 = success; 0 = failure                              |
| `msg`              | string  | No       | Message                                               |
| `data`             | object  | Yes      | Payment confirmation                                  |
| └─ `orderId`       | integer | Yes      | ID of the paid order                                  |
| └─ `status`        | number  | Yes      | New status (1 = paid, awaiting merchant confirmation) |
| └─ `paymentMethod` | string  | Yes      | Payment method used                                   |
| └─ `paidAt`        | string  | Yes      | Timestamp of payment                                  |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Payment successful",
  "data": {
    "orderId": 4001,
    "status": 1,
    "paymentMethod": "ApplePay",
    "paidAt": "2025-06-16T10:02:00.000Z"
  }
}
```

#### 1.3. Cancel Order

**Purpose:** Cancel an order that is still pending.

**Endpoint:** `/client/orders/{orderId}/cancel`

**Method:** `PUT`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `application/json`

**Request Parameter Description:** `Path Variable`

**Request Body Sample:**
```shell
/client/orders/1/cancel
```

**Response Parameter Description:**
| Parameter    | Type    | Required | Description                                           |
| ------------ | ------- | -------- | ----------------------------------------------------- |
| `code`       | number  | Yes      | 1 = success; 0 = failure                              |
| `msg`        | string  | No       | Message                                               |
| `data`       | object  | No       | Cancellation confirmation                             |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Order canceled successfully",
  "data": null
}
```

#### 1.4. View Historical Orders

**Purpose:** Retrieve a paginated list of past orders for the client.

**Endpoint:** `/client/orders`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Query Parameters`

**Request Parameter Description:**
| Parameter     | Required   | Example      | Description                                    |
|---------------|------------|--------------|------------------------------------------------|
| `page`        | Yes        | 1            | Page number for pagination (default = 1)       |
| `pageSize`    | Yes        | 10           | Number of records per page (default = 10)      |
| `status`      | No         | 5            | Filter by status code                          |
| `startTime`   | No         | "2025-06-16" | Filter by start time (inclusive)               |
| `endTime`     | No         | "2025-07-16" | Filter by end time (inclusive)                 |

**Request Body Sample:**
```shell
/client/orders?page=1&pageSize=10&status=5&startTime=2025-06-16&endTime=2025-07-16
```

**Response Parameter Description:**
| Parameter        | Type        | Required   | Description                               |
| -----------------| ----------- | ---------- | ----------------------------------------- |
| `code`           | number      | Yes        | Response code: 1 = success; 0 = failure   |
| `msg`            | string      | No         | Message                                   |
| `data`           | object      | No         | Returned data                             |
| └─ `total`       | number      | Yes        | Total number of records                   |
| └─ `rows`        | object[]    | Yes        | List of data records                      |
| └─ `orderId`     | number      | Yes        | Order ID                                  |
| └─ `status`      | number      | Yes        | Order status code                         |
| └─ `totalAmount` | number      | Yes        | Total amount of the order                 |
| └─ `merchantName`| string      | Yes        | Merchant name                             |
| └─ `createTime`  | string      | Yes        | Timestamp of order creation               |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Success",
  "data": {
    "total": 2,
    "rows": [
      {
        "orderId": 4001,
        "status": 5,
        "totalAmount": 100.00,
        "merchantName": "Merchant A",
        "createTime": "2025-06-16T10:02:00.000Z"
      }
      {
        "orderId": 4002,
        "status": 5,
        "totalAmount": 100.00,
        "merchantName": "Merchant B",
        "createTime": "2025-06-16T10:02:00.000Z"
      }
    ]
  }
}
```

#### 1.5. View Order Details

**Purpose:** Fetch full details of a single order.

**Endpoint:** `/client/orders/{orderId}`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Body Sample:**
```shell
/client/orders/4001
```

**Response Parameter Description:**
| Parameter        | Type    | Required | Description                          |
| ---------------- | ------- | -------- | ------------------------------------ |
| `code`           | number  | Yes      | 1 = success; 0 = failure             |
| `msg`            | string  | No       | Message                              |
| `data`           | object  | Yes      | Full order detail                    |
| └─ `id`          | integer | Yes      | Order ID                             |
| └─ `addressId`   | integer | Yes      | Client_address ID                    |  <!-- 在创建地址簿后更新 -->
| └─ `recipient`   | string  | Yes      | Recipient name                       |  <!-- 在创建地址簿后更新 -->
| └─ `merchantId`  | integer | Yes      | Merchant ID                          |
| └─ `merchantName`| string  | Yes      | Merchant name                        |
| └─ `status`      | number  | Yes      | Current status code                  |
| └─ `totalAmount` | number  | Yes      | Total amount                         |
| └─ `deliveryFee` | number  | Yes      | Delivery fee                         |
| └─ `paidAt`      | string  | No       | Payment timestamp                    |
| └─ `items`       | array   | Yes      | List of order items (with name, qty) |
|   └─ `dishId`    | integer | Yes      | ID of the dish                       |
|   └─ `dishName`  | string  | Yes      | Name of the dish                     |
|   └─ `unitPrice` | number  | Yes      | Unit price                           |
|   └─ `quantity`  | integer | Yes      | Quantity ordered                     |
|   └─ `subtotal`  | number  | Yes      | `unitPrice * quantity`               |
| └─ `createTime`  | string  | Yes      | Creation timestamp                   |
| └─ `updateTime`  | string  | Yes      | Last update timestamp                |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": { 
    "id": 1,
    "addressId": 1,
    "recipient": "Jane Doe",
    "merchantId": 1,
    "merchantName": "Foodie Take Out",
    "status": 5,
    "totalAmount": 13.0,
    "deliveryFee": 2.0,
    "paidAt": "2023-04-01T12:00:00Z",
    "items": [
      {
        "id": 1,
        "dishName": "Chicken Nuggets",
        "unitPrice": 5.0,
        "quantity": 2,
        "subtotal": 10.0
      },
      {
        "id": 2,
        "dishName": "French Fries",
        "unitPrice": 3.0,
        "quantity": 1,
        "subtotal": 3.0
      }
    ],
    "createTime": "2023-04-01T12:00:00Z",
    "updateTime": "2023-04-01T12:00:00Z"
  }
}
```

#### 1.6. Remind Shop to Confirm Order

**Purpose:** Send a nudge to the merchant if they haven’t confirmed.

**Endpoint:** `/client/orders/{orderId}/remind`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Body Sample:**
```shell
/client/orders/1/remind
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description              |
| --------- | ------ | -------- | ------------------------ |
| `code`    | number | Yes      | 1 = success; 0 = failure |
| `msg`     | string | No       | Message                  |
| `data`    | object | No       | Data                     |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

#### 1.7. Track Order Status & Location

**Purpose:** Provide real-time status and approximate rider location.

**Endpoint:** `/client/orders/{orderId}/track`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Body Sample:**
```shell
/client/orders/1/track
```

**Response Parameter Description:**
| Parameter      | Type   | Required | Description                            |
| -------------- | ------ | -------- | -------------------------------------- |
| `code`         | number | Yes      | 1 = success; 0 = failure               |
| `msg`          | string | No       | Message                                |
| `data`         | object | Yes      | Tracking info                          |
| └─ `status`    | number | Yes      | Order status (e.g. 0=pending, 1= paid, 2=accepted, 3=Ready to go, 4=picking up by rider, 5=out of delivery, 6=delivered (completed), 7=canceled) |
| └─ `updatedAt` | string | Yes      | Last status update timestamp           |
| └─ `riderLat`  | number | No       | Rider’s current latitude (if assigned) |
| └─ `riderLng`  | number | No       | Rider’s current longitude              |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Success",
  "data": {
    "status": 4,
    "updatedAt": "2025-05-01T09:01:01.000Z",
    "riderLat": 23.123456,
    "riderLng": 113.123456
  }
}
```

### 2. Merchant Order Module

#### 2.1. Query Orders with Filters & Pagination

**Purpose:** Fetch a paginated list of orders for a merchant, with optional status and date filters.

**Endpoint:** `/merchant/orders`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Query Parameters`

**Request Parameter Description:**
| Parameter   | Type    | Required | Description                                 |
| ----------- | ------- | -------- | ------------------------------------------- |
| `page`      | integer | Yes      | Page number (default 1)                     |
| `pageSize`  | integer | Yes      | Page size (default 10)                      |
| `status`    | number  | No       | Filter by order status code                 |
| `startDate` | string  | No       | ISO date filter start (e.g. `"2025-06-01"`) |
| `endDate`   | string  | No       | ISO date filter end (e.g. `"2025-06-30"`)   |

**Request Body Sample:**
```shell
/merchant/orders?page=1&pageSize=10
```

**Response Parameter Description:**
| Parameter          | Type    | Required | Description                          |
| ------------------ | ------- | -------- | ------------------------------------ |
| `code`             | number  | Yes      | 1 = success; 0 = failure             |
| `msg`              | string  | No       | Human-readable message               |
| `data`             | object  | Yes      | Paginated order list                 |
| └─ `total`         | integer | Yes      | Total number of matching orders      |
| └─ `rows`          | object[]| Yes      | List of data records                 |
| └─ `orderId`       | integer | Yes      | Order ID                             |
| └─ `clientUsername`| integer | Yes      | Client ID                            |
| └─ `status`        | number  | Yes      | Order status code                    |
| └─ `totalAmount`   | number  | Yes      | Total amount of the order            |
| └─ `createTime`    | string  | Yes      | ISO timestamp when order was created |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 2,
    "rows": [
      { 
        "orderId": 4001,
        "clientUsername": "chriswang",
        "status": 2,
        "totalAmount": 29.99,
        "createTime": "2025-06-15T12:00:00.000Z"
      },
      { 
        "orderId": 4002,
        "clientUsername": "chrisyu",
        "status": 4,
        "totalAmount": 15.50,
        "createTime": "2025-06-14T18:30:00.000Z"
      }
    ]
  }
}  
```

#### 2.2. Orders Statistics by Status

**Purpose:** Get counts of orders grouped by status for a merchant within an optional date range.

**Endpoint:** `/merchant/orders/statistics`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Query Parameters`

**Request Parameter Description:**
| Parameter | Type   | Required | Description                                                                             |
| --------- | ------ | -------- | --------------------------------------------------------------------------------------- |
| `period`  | string | Yes      | Time window for statistics. One of:                                                     |
|           |        |          | • `1d` – last 1 day                                                                     |
|           |        |          | • `7d` – last 7 days                                                                    |
|           |        |          | • `1m` – last 1 month                                                                   |
|           |        |          | • `3m` – last 3 months                                                                  |
|           |        |          | • `6m` – last 6 months                                                                  |
|           |        |          | • `1y` – last 1 year                                                                    |

**Request Body Sample:**
```shell
/merchant/orders/statistics?period=1d
```

**Response Parameter Description:**
| Parameter         | Type    | Required | Description                       |
| ----------------- | ------- | -------- | --------------------------------- |
| `code`            | number  | Yes      | 1 = success; 0 = failure          |
| `msg`             | string  | No       | Human-readable message            |
| `data`            | object  | Yes      | Status counts object              |
| └─ `statusCounts` | object  | Yes      | Map of status code to order count |
| └─── `0`          | integer | Yes      | Number of pending orders          |
| └─── `1`          | integer | Yes      | Number of paid orders             |
| └─── `2`          | integer | Yes      | Number of accepted orders         |
| └─── `3`          | integer | Yes      | Number of ready orders            |
| └─── `4`          | integer | Yes      | Number of waiting for rider orders|
| └─── `5`          | integer | Yes      | Number of out of delivery orders  |
| └─── `6`          | integer | Yes      | Number of completed orders        |
| └─── `7`          | integer | Yes      | Number of cancelled orders        |


**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "statusCounts": {
      "0": 3,
      "1": 5,
      "2": 18,
      "3": 3,
      "4": 5,
      "5": 5,
      "6": 128,
      "7": 5
    }
  }
}
```

#### 2.3. View Order Details

**Purpose:** Retrieve detailed information for a single order (merchant view).

**Endpoint:** `/merchant/orders/{orderId}`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Body Sample:**
```shell
/merchant/orders/1
```

**Response Parameter Description:**
| Parameter          | Type    | Required | Description              |
| ------------------ | ------- | -------- | ------------------------ |
| `code`             | number  | Yes      | 1 = success; 0 = failure |
| `msg`              | string  | No       | Human-readable message   |
| `data`             | object  | Yes      | Detailed order object    |
| └─ `id`            | integer | Yes      | Order ID                 |
| └─ `clientId`      | integer | Yes      | ID of the client         |
| └─ `clientUsername`| string  | Yes      | Name of the client       |
| └─ `status`        | number  | Yes      | Current status code      |
| └─ `totalAmount`   | number  | Yes      | Total amount             |
| └─ `deliveryFee`   | number  | Yes      | Delivery fee             |
| └─ `paymentMethod` | string  | Yes      | Payment method           |
| └─ `paidAt`        | string  | No       | Payment timestamp        |
| └─ `items`         | array   | Yes      | List of order items      |
| └─── `dishId`      | integer | Yes      | Dish ID                  |
| └─── `dishName`    | string  | Yes      | Dish name (snapshot)     |
| └─── `unitPrice`   | number  | Yes      | Unit price               |
| └─── `quantity`    | integer | Yes      | Quantity ordered         |
| └─── `subtotal`    | number  | Yes      | Subtotal                 |
| └─ `createTime`    | string  | Yes      | Creation timestamp       |
| └─ `updateTime`    | string  | Yes      | Last update timestamp    |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 4001,
    "clientId": 1001,
    "clientName": "AliceZhang",
    "status": 1,
    "totalAmount": 29.99,
    "deliveryFee": 5.00,
    "paymentMethod": "ApplePay",
    "paidAt": "2025-06-16T10:02:00.000Z",
    "items": [
      {
        "dishId": 3001,
        "dishName": "Kung Pao Chicken",
        "unitPrice": 12.50,
        "quantity": 2,
        "subtotal": 25.00
      }
    ],
    "createTime": "2025-06-16T10:00:00.000Z",
    "updateTime": "2025-06-16T10:00:00.000Z"
  }
}
```


#### 2.4. Confirm Order

**Purpose:** Merchant confirms and accepts the specified order.

**Endpoint:** `/merchant/orders/{orderId}/confirm`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter      | Type    | Required | Description                |
| -------------- | ------- | -------- | -------------------------- |
| Path `orderId` | integer | Yes      | ID of the order to confirm |

**Request Body Sample:**
```json
{}
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description              |
| --------- | ------ | -------- | ------------------------ |
| `code`    | number | Yes      | 1 = success; 0 = failure |
| `msg`     | string | No       | message                  |
| `data`    | null   | No       | return data              |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Order confirmed successfully",
  "data": null
}
```

#### 2.5. Reject Order

**Purpose:** Merchant rejects the specified order.

**Endpoint:** `/merchant/orders/{orderId}/reject`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter      | Type    | Required | Description               |
| -------------- | ------- | -------- | ------------------------- |
| Path `orderId` | integer | Yes      | ID of the order to reject |

**Request Body Sample:**
```json
{ "reason": "Out of stock" }
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description              |
| --------- | ------ | -------- | ------------------------ |
| `code`    | number | Yes      | 1 = success; 0 = failure |
| `msg`     | string | No       | message                  |
| `data`    | null   | No       | return data              |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Order rejected successfully",
  "data": null
}
```

#### 2.6. Mark Order Ready

**Purpose:** Merchant marks the order as ready for rider pickup.

**Endpoint:** `/merchant/orders/{orderId}/ready`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter      | Type    | Required | Description                   |
| -------------- | ------- | -------- | ----------------------------- |
| Path `orderId` | integer | Yes      | ID of the order to mark ready |

**Request Body Sample:**
```json
{}
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description              |
| --------- | ------ | -------- | ------------------------ |
| `code`    | number | Yes      | 1 = success; 0 = failure |
| `msg`     | string | No       | message                  |
| `data`    | null   | No       | return data              |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Order marked ready for pickup",
  "data": null
}
```

### 3. Rider Order Module

#### 3.1. Accept Order

**Purpose:** Rider accepts an order assigned to them.

**Endpoint:** `/rider/orders/{orderId}/accept`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter      | Type    | Required | Description               |
| -------------- | ------- | -------- | ------------------------- |
| Path `orderId` | integer | Yes      | ID of the order to accept |

**Request Body Sample:**
```json
{}
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description              |
| --------- | ------ | -------- | ------------------------ |
| `code`    | number | Yes      | 1 = success; 0 = failure |
| `msg`     | string | No       | message                  |
| `data`    | null   | No       | return data              |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Order accepted",
  "data": null
}
```

#### 3.2. Reject Order

**Purpose:** Rider rejects an order assigned to them.

**Endpoint:** `/rider/orders/{orderId}/reject`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter      | Type    | Required | Description               |
| -------------- | ------- | -------- | ------------------------- |
| Path `orderId` | integer | Yes      | ID of the order to reject |

**Request Body Sample:**
```json
{ "reason": "Far distance" }
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description              |
| --------- | ------ | -------- | ------------------------ |
| `code`    | number | Yes      | 1 = success; 0 = failure |
| `msg`     | string | No       | message                  |
| `data`    | null   | No       | return data              |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Order rejected",
  "data": null
}
```


#### 3.3. Mark Order Picked Up

**Purpose:** Rider indicates they have picked up the order from the merchant.

**Endpoint:** `/rider/orders/{orderId}/pickup`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter      | Type    | Required | Description                     |
| -------------- | ------- | -------- | ------------------------------- |
| Path `orderId` | integer | Yes      | ID of the order being picked up |

**Request Body Sample:**
```json
{}
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description              |
| --------- | ------ | -------- | ------------------------ |
| `code`    | number | Yes      | 1 = success; 0 = failure |
| `msg`     | string | No       | message                  |
| `data`    | null   | No       | returned data            |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Order pickup confirmed",
  "data": null
}
```

#### 3.4. Get Delivery Route

**Purpose:** Retrieve an optimized route for delivering the order.

**Endpoint:** `/rider/orders/{orderId}/route`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter      | Type    | Required | Description                        |
| -------------- | ------- | -------- | ---------------------------------- |
| Path `orderId` | integer | Yes      | ID of the order for route planning |

**Request Body Sample:**
N/A

**Response Parameter Description:**
| Parameter          | Type   | Required | Description                      |
| ------------------ | ------ | -------- | -------------------------------- |
| `code`             | number | Yes      | 1 = success; 0 = failure         |
| `msg`              | string | No       | Human-readable message           |
| `data`             | object | Yes      | Route information                |
| └─ `route`         | array  | Yes      | Array of \[lat, lng] coordinates |
| └─ `distance`      | number | Yes      | Total distance in meters         |
| └─ `estimatedTime` | number | Yes      | Estimated time in seconds        |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "route": [
      [39.78, -89.64],
      [39.781, -89.645],
      [39.782, -89.65]
    ],
    "distance": 1500,
    "estimatedTime": 600
  }
}
```


#### 3.5. Mark Order Delivered

**Purpose:** Rider confirms that the order has been delivered to the client.

**Endpoint:** `/rider/orders/{orderId}/complete`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter      | Type    | Required | Description                       |
| -------------- | ------- | -------- | --------------------------------- |
| Path `orderId` | integer | Yes      | ID of the order to mark delivered |

**Request Body Sample:**
```json
{}
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description              |
| --------- | ------ | -------- | ------------------------ |
| `code`    | number | Yes      | 1 = success; 0 = failure |
| `msg`     | string | No       | message                  |
| `data`    | null   | No       | return data              |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Order delivered successfully",
  "data": null
}
```