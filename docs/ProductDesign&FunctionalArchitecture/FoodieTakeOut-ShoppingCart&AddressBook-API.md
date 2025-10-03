# FoodieTakeOut API Documentation

## Overview

This document outlines the API endpoints for the client's shopping cart and address book features, which includes:

1. **Client Shopping Cart Module**
   1. Get cart items
   2. Add or update cart item
   3. Remove cart item
   4. Clear cart

2. **Client Address Book Module**
   1. List addresses
   2. Add new address
   3. Update address
   4. Delete address
   5. Set default address

## API Endpoints

### 1. Client Shopping Cart Module

#### 1.1. Get Cart Items

**Purpose:** Retrieve all items currently in the client’s cart.

**Endpoint:** `/client/cart`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** HTTP Header

**Request Parameter Description:**
| Parameter       | Type   | Required | Description                                                |
| -------------   | ------ | -------- | ---------------------------------------------------------- |
| Authorization   | string | Yes      | Bearer JWT accesToken, e.g. `Bearer eyJhbGciOiJIUzI1Ni…`   |
| X-Refresh-Token | string | Yes      | Bearer JWT refresToken, e.g. `eyJhbGciOiJIUzI1Ni…`         |

**Respond Parameter Format:** `application/json`

**Respond Parameter Description:**
| Parameter        | Type    | Required | Description                       |
| ---------------- | ------- | -------- | --------------------------------- |
| `code`           | number  | Yes      | 1 = success; 0 = failure          |
| `msg`            | string  | No       | Human-readable message            |
| `data`           | object  | Yes      | Payload object                    |
| └─ `total`       | integer | Yes      | Total number of items in the cart |
| └─ `rows`        | array   | Yes      | Array of cart item objects        |
| └─── `id`        | integer | Yes      | Cart item ID                      |
| └─── `dishId`    | integer | Yes      | Dish ID                           |
| └─── `dishName`  | string  | Yes      | Snapshot name                     |
| └─── `unitPrice` | number  | Yes      | Snapshot price                    |
| └─── `quantity`  | integer | Yes      | Quantity                          |
| └─── `subtotal`  | number  | Yes      | Subtotal price                    |
| └─── `remark`    | string  | No       | Special request                   |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 2,
    "rows": [
      {
        "id": 5001,
        "dishId": 3001,
        "dishName": "Kung Pao Chicken",
        "unitPrice": 12.50,
        "quantity": 2,
        "subtotal": 25.00,
        "remark": "no onions"
      },
      {
        "id": 5002,
        "dishId": 3002,
        "dishName": "Spring Rolls",
        "unitPrice": 5.00,
        "quantity": 3,
        "subtotal": 15.00,
        "remark": ""
      }
    ]
  }
}
```

#### 1.2. Add or Update Cart Item

**Purpose:** Add a dish to the cart or update its quantity/remark if it already exists.

**Endpoint:** `/client/cart`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter  | Type    | Required | Description           |
| ---------- | ------- | -------- | --------------------- |
| `dishId`   | integer | Yes      | Dish to add or update |
| `quantity` | integer | Yes      | Desired quantity      |
| `remark`   | string  | No       | Special requests      |

**Response Parameter Description:**
| Parameter      | Type    | Required | Description              |
| -------------- | ------- | -------- | ------------------------ |
| `code`         | number  | Yes      | 1=success; 0=failure     |
| `msg`          | string  | No       | Message                  |
| `data`         | object  | Yes      | Updated cart item object |
| └─ `id`        | integer | Yes      | Cart item ID             |
| └─ `quantity`  | integer | Yes      | New quantity             |
| └─ `updateTime`| string  | Yes      | Timestamp of update      |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Item added/updated",
  "data": {
    "id": 5001,
    "quantity": 3,
    "updatedAt": "2025-06-19T15:30:00.000Z"
  }
}
```

#### 1.3. Remove Cart Item

**Purpose:** Remove a specific item from the client’s cart.

**Endpoint:** `/client/cart/{cartItemId}`

**Method:** `DELETE`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Body Sample:**
```shell
/client/cart/1
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description          |
| --------- | ------ | -------- | -------------------- |
| `code`    | number | Yes      | 1=success; 0=failure |
| `msg`     | string | No       | Message              |
| `data`    | object | No       | Data                 |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Item removed",
  "data": null
}
```

#### 1.4. Clear Cart

**Purpose:** Remove all items from the client’s cart.

**Endpoint:** `/client/cart`

**Method:** `DELETE`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameters Format:** HTTP Header

**Response Parameter Description:**
| Parameter | Type   | Required | Description          |
| --------- | ------ | -------- | -------------------- |
| `code`    | number | Yes      | 1=success; 0=failure |
| `msg`     | string | No       | Message              |
| `data`    | object | No       | Data                 |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Cart cleared",
  "data": null
}
```

#### 1.5. Order Again

**Purpose**: Take all items from a previous order and insert them into the client’s cart, then return the updated.

**Endpoint**: `/client/cart/reorder/{orderId}`

**Method**: `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter      | Type    | Required | Description                                          |
| -------------- | ------- | -------- | ---------------------------------------------------- |
| Path `orderId` | integer | Yes      | ID of the order whose items will be copied into cart |

**Request Body Sample:**
```shell
/client/cart/reorder/1
```

**Response Parameter Description:**
| Parameter        | Type    | Required | Description                                |
| ---------------- | ------- | -------- | ------------------------------------------ |
| `code`           | number  | Yes      | 1 = success; 0 = failure                   |
| `msg`            | string  | No       | Human-readable message                     |
| `data`           | object  | Yes      | Updated cart overview                      |
| └─ `total`       | integer | Yes      | Total number of items now in the cart      |
| └─ `rows`        | array   | Yes      | Array of cart item objects                 |
| └─── `dishId`    | integer | Yes      | Dish ID                                    |
| └─── `dishName`  | string  | Yes      | Name of the dish at time of original order |
| └─── `unitPrice` | number  | Yes      | Unit price                                 |
| └─── `quantity`  | integer | Yes      | Quantity                                   |
| └─── `subtotal`  | number  | Yes      | `unitPrice * quantity`                     |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Cart updated with items from order 4001",
  "data": {
    "total": 2,
    "rows": [
      {
        "dishId": 3001,
        "dishName": "Kung Pao Chicken",
        "unitPrice": 12.50,
        "quantity": 2,
        "subtotal": 25.00
      },
      {
        "dishId": 3002,
        "dishName": "Spring Rolls",
        "unitPrice": 5.00,
        "quantity": 1,
        "subtotal": 5.00
      }
    ]
  }
}
```

### 2. Client Address Book Module

#### 2.1. List Addresses

**Purpose:** Retrieve all saved addresses for the client.

**Endpoint:** `/client/addresses`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameters Format:** HTTP Header

**Response Parameter Description:**
| Parameter           | Type    | Required | Description                             |
| ------------------- | ------- | -------- | --------------------------------------- |
| `code`              | number  | Yes      | 1 = success; 0 = failure                |
| `msg`               | string  | No       | Human-readable message                  |
| `data`              | object  | Yes      | Payload object                          |
| └─ `total`          | integer | Yes      | Total number of saved addresses         |
| └─ `rows`           | array   | Yes      | Array of address entry objects          |
| └─── `id`           | integer | Yes      | Address ID                              |
| └─── `label`        | string  | Yes      | User-defined label (e.g., Home, Office) |
| └─── `recipient`    | string  | Yes      | Name of the recipient                   |
| └─── `phone`        | string  | Yes      | Contact phone                           |
| └─── `addressLine1` | string  | Yes      | Primary street address                  |
| └─── `addressLine2` | string  | No       | Secondary address details               |
| └─── `city`         | string  | Yes      | City                                    |
| └─── `state`        | string  | Yes      | State or province                       |
| └─── `zipcode`      | string  | No       | Postal code                             |
| └─── `country`      | string  | Yes      | Country                                 |
| └─── `isDefault`    | boolean | Yes      | Whether this is the default address     |


**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 2,
    "rows": [
      {
        "id": 6001,
        "label": "Home",
        "recipient": "Alice Zhang",
        "phone": "1234567890",
        "addressLine1": "123 Main St",
        "addressLine2": "Apt 4B",
        "city": "Springfield",
        "state": "IL",
        "zipcode": "62704",
        "country": "USA",
        "isDefault": true
      },
      {
        "id": 6002,
        "label": "Office",
        "recipient": "Alice Zhang",
        "phone": "1234567890",
        "addressLine1": "456 Commerce Blvd",
        "addressLine2": null,
        "city": "Springfield",
        "state": "IL",
        "zipcode": "62701",
        "country": "USA",
        "isDefault": false
      }
    ]
  }
}
```

#### 2.2. Add New Address

**Purpose:** Add a new address to the client’s address book.

**Endpoint:** `/client/addresses`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter      | Type    | Required | Description            |
| -------------- | ------- | -------- | ---------------------- |
| `label`        | string  | Yes      | Address label          |
| `recipient`    | string  | Yes      | Recipient name         |
| `phone`        | string  | Yes      | Contact phone          |
| `addressLine1` | string  | Yes      | Primary address        |
| `addressLine2` | string  | No       | Secondary address      |
| `city`         | string  | Yes      | City                   |
| `state`        | string  | Yes      | State                  |
| `zipcode`      | string  | No       | Postal code            |
| `country`      | string  | Yes      | Country                |
| `latitude`     | number  | No       | Geographic latitude    |
| `longitude`    | number  | No       | Geographic longitude   |
| `isDefault`    | boolean | No       | true to set as default |

**Request Body Sample:**
```json
{
  "label": "Office",
  "recipient": "Alice Zhang",
  "phone": "1234567890",
  "addressLine1": "456 Commerce Blvd",
  "city": "Springfield",
  "state": "IL",
  "zipcode": "62701",
  "country": "USA",
  "isDefault": false
}
```

**Response Parameter Description:**
| Parameter | Type    | Required | Description            |
| --------- | ------- | -------- | ---------------------- |
| `code`    | number  | Yes      | 1=success; 0=failure   |
| `msg`     | string  | No       | Message                |
| `data`    | object  | No       | Created address object |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Address added",
  "data": null
}
```

#### 2.3. Delete Address

**Purpose:** Remove an address from the client’s address book.

**Endpoint:** `/client/addresses/{addressId}`

**Method:** `DELETE`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Body Sample:**
```shell
/client/addresses/1
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description          |
| --------- | ------ | -------- | -------------------- |
| `code`    | number | Yes      | 1=success; 0=failure |
| `msg`     | string | No       | Message              |
| `data`    | object | No       | Null                 |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Address deleted",
  "data": null
}
```

#### 2.4. Get Address by ID

**Purpose:** Get dish item by ID. <!-- 获取指定ID的菜品，用于更新前的查询回显 -->

**Endpoint:** `/client/address/{addressId}`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}
  
**Request Body Format:** `Path Variable`

**Request Body Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `id`      | number| 1        | Yes      | Dish ID               |

**Request Body Sample:**
```shell
/client/address/1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter         | Type        | Required   | Description                                   |
| ---------------   | ----------- | ---------- | --------------------------------------------- |
| `code`            | number      | Yes        | Response code: 1 = success; 0 = failure       |
| `msg`             | string      | Yes        | Message                                       |
| `data`            | object      | Yes        | Returned data                                 |
| └─ `id`           | integer     | Yes        | Address ID                                    |
| └─ `label`        | string      | Yes        | User-defined label (e.g., Home, Office)       |
| └─ `recipient`    | string      | Yes        | Name of the recipient                         |
| └─ `phone`        | string      | Yes        | Contact phone                                 |
| └─ `addressLine1` | string      | Yes        | Primary street address                        |
| └─ `addressLine2` | string      | No         | Secondary address details                     |
| └─ `city`         | string      | Yes        | City                                          |
| └─ `state`        | string      | Yes        | State or province                             |
| └─ `zipcode`      | string      | No         | Postal code                                   |
| └─ `country`      | string      | Yes        | Country                                       |
| └─ `isDefault`    | boolean     | Yes        | Whether this is the default address           |

**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": {
    "id": 6001,
    "label": "Home",
    "recipient": "Alice Zhang",
    "phone": "1234567890",
    "addressLine1": "123 Main St",
    "addressLine2": "Apt 4B",
    "city": "Springfield",
    "state": "IL",
    "zipcode": "62704",
    "country": "USA",
    "isDefault": true
  }
}
```

#### 2.5. Update Address

**Purpose:** Modify an existing address entry.

**Endpoint:** `/client/addresses/{addressId}`

**Method:** `PUT`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter       | Type        | Required   | Description                                   |
| --------------- | ----------- | ---------- | --------------------------------------------- |
| `label`         | string      | Yes        | User-defined label (e.g., Home, Office)       |
| `recipient`     | string      | Yes        | Name of the recipient                         |
| `phone`         | string      | Yes        | Contact phone                                 |
| `addressLine1`  | string      | Yes        | Primary street address                        |
| `addressLine2`  | string      | No         | Secondary address details                     |
| `city`          | string      | Yes        | City                                          |
| `state`         | string      | Yes        | State or province                             |
| `zipcode`       | string      | No         | Postal code                                   |
| `country`       | string      | Yes        | Country                                       |
| `isDefault`     | boolean     | Yes        | Whether this is the default address           |

**Request Body Sample:**
```json
{
  "label": "Home",
  "recipient": "Chris Wang",
  "phone": "1234567890",
  "addressLine1": "123 Main St",
  "addressLine2": "Apt 4B",
  "city": "Springfield",
  "state": "IL",
  "zipcode": "62704",
  "country": "USA",
  "isDefault": true
}
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description          |
| --------- | ------ | -------- | -------------------- |
| `code`    | number | Yes      | 1=success; 0=failure |
| `msg`     | string | No       | Message              |
| `data`    | object | No       | Data                 |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Address updated",
  "data": null
}
```

#### 2.6. Set Default Address

**Purpose:** Mark one address as the client’s default.

**Endpoint:** `/client/addresses/{addressId}/default`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter        | Type    | Required | Description       |
| ---------------- | ------- | -------- | ----------------- |
| Path `addressId` | integer | Yes      | ID of the address |

**Request Body Sample:**
```json
{}
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description          |
| --------- | ------ | -------- | -------------------- |
| `code`    | number | Yes      | 1=success; 0=failure |
| `msg`     | string | No       | Message              |
| `data`    | object | No       | Data                 |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Default address set",
  "data": null
}
```