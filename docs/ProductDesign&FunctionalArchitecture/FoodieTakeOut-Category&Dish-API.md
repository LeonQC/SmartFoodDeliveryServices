# FoodieTakeOut API Documentation

## Overview

This document describes the API endpoints for managing and querying categories and dishes — available to both merchants and clients — including:

1. **Merchant Category & Dish Management**
   1. Manage menu categories
   2. Manage dish items
   3. Query categories and dishes status (dashboard)
2. **Client Restaurant & Category & Dish Query**
   1. View all restaurants (sort by distance/rating/price)
   2. View restaurant details
   3. View menu categories
   4. View dish items under a category
   
## API Endpoints

### 1. Merchant Category & Dish Management

#### 1.1. Manage Menu Categories

**Purpose:** Merchant CRUD menu categories of its own restaurant.

##### 1.1.1. View All Menu categories

**Purpose:** View all menu categories in the merchant's restaurant.

**Endpoint:** `/merchant/categories`

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
| Parameter       | Type           | Required   | Description                               |
|-----------------|----------------|------------|-------------------------------------------|
| `code`          | number         | Yes        | Response code: 1 = success; 0 = failure   |
| `msg`           | string         | No         | Message                                   |
| `data`          | object         | Yes        | Returned data                             |
| └─ `id`         | number         | Yes        | Category's ID                             |
| └─ `name`       | string         | Yes        | Category's name                           |
| └─ `sort`       | number         | Yes        | Category's sort                           |
| └─ `status`     | number         | Yes        | Status (1 = enabled; 0 = disabled)        |

**Respond Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "Fast Food",
    "sort": 1,
    "status": 1
  },
  {
    "id": 2,
    "name": "Drinks",
    "sort": 2,
    "status": 1
  },
  {
    "id": 3,
    "name": "Dessert",
    "sort": 3,
    "status": 1
  }
  ...
}
```    

##### 1.1.2. Delete Menu categories

**Purpose:** Delete / Batch delete menu categories by category IDs.

**Endpoint:** `/merchant/categories`

**Method:** `DELETE`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** Query Parameters

**Request Parameter Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `ids`     | array | [1,2,3]  | Yes      | Array of category IDs |

**Request Body Sample:**
```shell
/merchant/categories?ids=1,2,3
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | No         | Message                                 |
| `data`        | object | No         | Returned data                           |

**Response Body Sample**
```json
{
    "code":1,
    "msg":"success",
    "data":null
}
```

##### 1.1.3. Create Menu Categories

**Purpose:** Create new menu categories.

**Endpoint:** `/merchant/categories`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter | Type    | Required | Description                                                        |
|-----------|---------|----------|--------------------------------------------------------------------|
| `name`    | string  | Yes      | Category name                                                      |
| `sort`    | number  | Yes      | For sorting (ascending) categories                                 |

**Request Body Sample**
```json
{
  "name": "Desserts",
  "sort": 1,
}
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | No         | Message                                 |
| `data`        | object | No         | Returned data                           |

**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":null
}
```

##### 1.1.4. Query Menu Category By ID

**Purpose:** Retrieve a specific menu category based on its ID.

**Endpoint:** `/merchant/categories/{id}`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter | Type    | Required | Description   |
|-----------|---------|----------|---------------|
| `id`      | number  | Yes      | Category ID   |

**Request Body Sample:**
```shell
/merchant/categories/1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |
| └─ `id`       | number | Yes        | Category ID                             |
| └─ `name`     | string | Yes        | Category name                           |
| └─ `sort`     | number | Yes        | For sorting (ascending) categories      |
| └─ `createTime` | string | Yes      | Creation time                           |
| └─ `updateTime` | string | Yes      | Update time                             |

**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":{
    "id":1,
    "name":"Desserts",
    "sort":1
  }
}
```

##### 1.1.5. Update Menu Categories

**Purpose:** Update menu categories.

**Endpoint:** `/merchant/categories/{categoryId}`

**Method:** `PUT`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable` + `application/json`

**Request Parameter Description:**
| Parameter | Type    | Required | Description                         |
|-----------|---------|----------|-------------------------------------|
| `id`      | integer | Yes      | Category ID                         |
| `name`    | string  | Yes      | Category name                       |
| `sort`    | integer | Yes      | For sorting (ascending) categories  |

**Request Body Sample**
```json
{
  "name": "Noodles",
  "sort": 1
}
```
```shell
/merchant/categories/{categoryId}
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | No         | Message                                 |
| `data`        | object | No         | Returned data                           |

**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":null
}
```

##### 1.1.6. Category Status Change

**Purpose:** Change category status rather than delete it.

**Endpoint:** `/merchant/categories/{categoryId}/status/{status}`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Body Format:** `Path Variable`

**Request Body Description:**
| Parameter | Type  | Example  | Required | Description               |
|-----------|-------|----------|----------|---------------------------|
| `status`  | number| 1        | Yes      | Status type (1 = avaliable; 0 = unavaliable) |
| `id`      | number| 1        | Yes      | Category ID               |

**Request Body Sample:**
```shell
/merchant/categories/{categoryId}/status/{status}
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter       | Type        | Required   | Description                                   |
| --------------- | ----------- | ---------- | --------------------------------------------- |
| `code`          | number      | Yes        | Response code: 1 = success; 0 = failure       |
| `msg`           | string      | Yes        | Message                                       |
| `data`          | object      | No         | Returned data                                 |

**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": null
}
```

#### 1.2. Manage Dish Items

**Purpose:** CRUD dish items.

##### 1.2.1. Query Dish Items

**Purpose:** Retrieve a paginated, sorted, and filtered list of all dish items based on specified query criteria.

**Endpoint:** `/merchant/dishes`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}
  
**Request Parameter Format:** Query Parameters

**Request Parameter Description:**
| Parameter     | Required   | Example      | Description                                    |
|---------------|------------|--------------|------------------------------------------------|
| `categoryId`  | No         | 1            | Category ID                                    |
| `name`        | No         | Tiramisu     | dish's name                                    |
| `page`        | Yes        | 1            | Page number for pagination (default = 1)       |
| `pageSize`    | Yes        | 10           | Number of records per page (default = 10)      |
| `status`      | No         | 1            | Status type (1 = on sale; 0 = off sale)        |

**Request Body Sample:**
```shell
/merchant/dishes?categoryId=1&page=1&pageSize=10&status=1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter         | Type        | Required   | Description                                                 |
| ----------------- | ----------- | ---------- | ----------------------------------------------------------- |
| `code`            | number      | Yes        | Response code: 1 = success; 0 = failure                     |
| `msg`             | string      | No         | Message                                                     |
| `data`            | object      | No         | Returned data                                               |
| └─ `total`        | number      | Yes        | Total number of records                                     |
| └─ `rows`         | object[]    | Yes        | List of data records                                        |
| └─ `id`           | number      | No         | dish ID                                                     |
| └─ `name`         | string      | No         | dish name                                                   |
| └─ `categoryId`   | number      | No         | Category ID                                                 |
| └─ `price`        | number      | No         | Price                                                       |
| └─ `status`       | number      | No         | Status type (1 = on sale; 0 = off sale)                     |
| └─ `description`  | string      | No         | Description                                                 |
| └─ `image`        | string (URL)| No         | Image URL of dish                                           |
| └─ `createTime`   | string      | No         | Creation timestamp                                          |
| └─ `updateTime`   | string      | No         | Update timestamp                                            |

**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":{
    "total":2,
    "rows":[
      { 
        "id":1,
        "name":"Tiramisu",
        "categoryId":1,
        "price":10.00,
        "status":1,
        "description":"Tiramisu is a dessert consisting of one or more layers. The main layer consists of a mixture of eggs, sugar, and mascarpone cheese that is gradually cooked and folded into the previous layer. The final layer is a ganache composed of egg white and chocolate. Tiramisu is a type of Italian dessert. It is named after the Italian word tiramisù, which means “to walk on eggs.”",
        "image":"https://www.example.com/tiramisu.jpg",
        "createTime":"2023-04-01T12:00:00Z",
        "updateTime":"2023-04-01T12:00:00Z"
      },
      { 
        "id":2,
        "name":"Mango Pudding",
        "categoryId":1,
        "price":8.00,
        "status":1,
        "description":"Mango pudding is a sweet dessert made from mango juice, sugar, and eggs. It is often served with a thin layer of ice cream on top. Mango pudding is a popular dessert in many countries, especially in Southeast Asia and Oceania. It is a simple and delicious dessert that can be made at home.",
        "image":"https://www.example.com/mango_pudding.jpg",
        "createTime":"2023-04-01T12:00:00Z",
        "updateTime":"2023-04-01T12:00:00Z"
      }
    ]
  }
}
```

##### 1.2.2. Delete Dish Item

**Purpose:** Delete / Batch delete dish items based on IDs.

**Endpoint:** `/merchant/dishes`

**Method:** `DELETE`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Body Format:** Query Parameters

**Request Body Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `ids`     | array | [1,2,3]  | Yes      | Array of dish IDs     |

**Request Body Sample:**
```shell
/merchant/dishes?ids=1,2,3
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter | Type        | Required   | Description                              |
|-----------|-------------|------------|------------------------------------------|
| `code`    | number      | Yes        | Response code: 1 = success; 0 = failure  |
| `msg`     | string      | No         | Message                                  |
| `data`    | object      | No         | Returned data                            |
**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": null
}
```

##### 1.2.3. Create Dish Item

**Purpose:** Create a new dish item.

**Endpoint:** `/merchant/dishes`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Body Format:** `application/json`

**Request Body Description:**
| Parameter     | Type        | Required   | Description                                                 |
| ------------- | ----------- | ---------- | ----------------------------------------------------------- |
| `name`        | string      | Yes        | dish name                                                   |
| `categoryId`  | number      | Yes        | Category ID                                                 |
| `price`       | number      | Yes        | Price                                                       |
| `status`      | number      | No         | Status type (1 = on sale; 0 = off sale)                     |
| `description` | string      | No         | Description                                                 |
| `image`       | string (URL)| No         | Image URL of dish                                           |

**Request Body Sample:**
```json
{
  "name":"Tiramisu",
  "categoryId":1,
  "price":10.00,
  "status":1,
  "description":"Tiramisu is a dessert consisting of one or more layers. The main layer consists of a mixture of eggs, sugar, and mascarpone cheese that is gradually cooked and folded into the previous layer. The final layer is a ganache composed of egg white and chocolate. Tiramisu is a type of Italian dessert. It is named after the Italian word tiramisù, which means “to walk on eggs.”",
  "image":"https://www.example.com/tiramisu.jpg"
}
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter       | Type        | Required   | Description                                                 |
| --------------- | ----------- | ---------- | ----------------------------------------------------------- |
| `code`          | number      | Yes        | Response code: 1 = success; 0 = failure                     |
| `msg`           | string      | No         | Message                                                     |
| `data`          | object      | No         | Returned data                                               |

**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": null
}
```

##### 1.2.4. Get Dish Item By ID

**Purpose:** Get dish item by ID. <!-- 获取指定ID的菜品，用于更新前的查询回显 -->

**Endpoint:** `/merchant/dishes/{id}`

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
/merchant/dishes/1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter         | Type        | Required   | Description                                   |
| ---------------   | ----------- | ---------- | --------------------------------------------- |
| `code`            | number      | Yes        | Response code: 1 = success; 0 = failure       |
| `msg`             | string      | Yes        | Message                                       |
| `data`            | object      | Yes        | Returned data                                 |
| └─ `id`           | number      | Yes        | dish ID                                       |
| └─ `name`         | string      | Yes        | dish name                                     |
| └─ `categoryId`   | number      | Yes        | Category ID                                   |
| └─ `price`        | number      | Yes        | Price                                         |
| └─ `status`       | number      | Yes        | Status type (1 = avaliable; 0 = unavaliable)  |
| └─ `description`  | string      | No         | Description                                   |
| └─ `image`        | string (URL)| No         | Image URL of dish                             |
| └─ `createTime`   | string      | No         | Creation timestamp                            |
| └─ `updateTime`   | string      | No         | Update timestamp                              |

**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": {
    "id":1,
    "name":"Tiramisu",
    "categoryId":1,
    "price":10.00,
    "status":1,
    "description":"Tiramisu is a dessert consisting of one or more layers. The main layer consists of a mixture of eggs, sugar, and mascarpone cheese that is gradually cooked and folded into the previous layer. The final layer is a ganache composed of egg white and chocolate. Tiramisu is a type of Italian dessert. It is named after the Italian word tiramisù, which means “to walk on eggs.”",
    "image":"https://www.example.com/tiramisu.jpg",
    "createTime":"2023-07-01 12:00:00",
    "updateTime":"2023-07-01 12:00:00"
  }
}
```

##### 1.2.5. Update Dish Item

**Purpose:** Update dish item.

**Endpoint:** `/merchant/dishes/{dishId}`

**Method:** `PUT`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Body Format:** `application/json`

**Request Body Description:**
| Parameter     | Type        | Required   | Description                                    |
| ------------- | ----------- | ---------- | -----------------------------------------------|
| `id`          | number      | Yes        | dish ID                                        |
| `name`        | string      | Yes        | dish name                                      |
| `categoryId`  | number      | Yes        | Category ID                                    |
| `price`       | number      | Yes        | Price                                          |
| `status`      | number      | Yes        | Status type (1 = avaliable; 0 = unavaliable)   |
| `description` | string      | No         | Description                                    |
| `image`       | string (URL)| No         | Image URL of dish                              |

**Request Body Sample:**
```json
{
  "id":1,
  "name":"Tiramisu",
  "categoryId":1,
  "price":10.00,
  "status":1,
  "description":"Tiramisu is a dessert consisting of one or more layers. The main layer consists of a mixture of eggs, sugar, and mascarpone cheese that is gradually cooked and folded into the previous layer. The final layer is a ganache composed of egg white and chocolate. Tiramisu is a type of Italian dessert. It is named after the Italian word tiramisù, which means “to walk on eggs.”",
  "image":"https://www.example.com/tiramisu.jpg"
}
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter       | Type        | Required   | Description                                   |
| --------------- | ----------- | ---------- | --------------------------------------------- |
| `code`          | number      | Yes        | Response code: 1 = success; 0 = failure       |
| `msg`           | string      | Yes        | Message                                       |
| `data`          | object      | No         | Returned data                                 |

**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": null
}
```

##### 1.2.6. Dish Item Status Change

**Purpose:** Change dish item status rather than delete it.

**Endpoint:** `/merchant/dishes/{dishId}/status/{status}`

**Method:** `PUT`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Body Format:** `Path Variable`

**Request Body Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `status`  | number| 1        | Yes      | Status type (1 = avaliable; 0 = unavaliable) |
| `id`      | number| 1        | Yes      | Dish ID               |

**Request Body Sample:**
```shell
/shop/dishes/1/status/1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter       | Type        | Required   | Description                                   |
| --------------- | ----------- | ---------- | --------------------------------------------- |
| `code`          | number      | Yes        | Response code: 1 = success; 0 = failure       |
| `msg`           | string      | Yes        | Message                                       |
| `data`          | object      | No         | Returned data                                 |

**Response Body Sample:**
```json
{
  "code":1,
  "msg":"Dish enabled successfully",
  "data": null
}
```

#### 1.3. Query categories and dishes status (dashboard)

**Purpose:** Query categories and dishes status.

**Endpoint:** `/merchant/dashboard/categories`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Response Body Format:** HTTP Header

**Request Body Sample:**
```shell
/merchant/dashboard/categories
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter       | Type        | Required   | Description                                   |
| --------------- | ----------- | ---------- | --------------------------------------------- |
| `code`          | number      | Yes        | Response code: 1 = success; 0 = failure       |
| `msg`           | string      | nO         | Message                                       |
| `data`          | object      | Yes        | Returned data                                 |
| └─ `categoryTotal` | number   | Yes        | Number of categories                          |
| └─ `dishTotal`  | number      | Yes        | Number of dishes                              |
| └─ `categoryActive` | number  | Yes        | Number of actived categories                  |
| └─ `dishActive` | number      | Yes        | Number of actived dishes                      |

**Response Body Sample:**
```json
{
  "code": 1,
  "message": "Success",
  "data": {
    "categoryTotal": 5,
    "dishTotal": 10,
    "categoryActive": 4,
    "dishActive": 8
  }
}
```


### 2. Client Restaurant & Category & Dish Query

#### 2.1. Query All Restaurants

<!-- 需要给Merchant添加type字段，requestParameter中可选typeId -->
**Purpose:** Retrieve a paginated, sorted, and filtered list of all restaurants based on restaurant <!-- typeId, --> name, and status. Restaurants must be within 10 km of the user's location.

**Endpoint:** `/client/browse`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}
  
**Request Parameter Format:** Query Parameters

**Request Parameter Description:**
| Parameter        | Required   | Example       | Description                                    |
|------------------|------------|---------------|------------------------------------------------|
| `x`              | Yes        | 0.0           | longitude                                      |
| `y`              | Yes        | 0.0           | latitude                                       |
| `merchantName`   | No         | Chengdu Taste | restaurant's name                              |
| `page`           | Yes        | 1             | Page number for pagination (default = 1)       |
| `pageSize`       | Yes        | 10            | Number of records per page (default = 10)      |
| `merchantStatus` | No         | 1             | Status type (1 = opening; 0 = closed)          |

**Request Body Sample:**
```shell
/client/restaurants?page=1&pageSize=10&x=0.0&y=0.0&status=1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter                 | Type        | Required   | Description                                                 |
| -----------------         | ----------- | ---------- | ----------------------------------------------------------- |
| `code`                    | number      | Yes        | Response code: 1 = success; 0 = failure                     |
| `msg`                     | string      | No         | Message                                                     |
| `data`                    | object      | No         | Returned data                                               |
| └─ `total`                | number      | Yes        | Total number of records                                     |
| └─ `rows`                 | object[]    | Yes        | List of data records                                        |
| └─ `merchantId`           | number      | Yes        | Merchant ID                                                 |
| └─ `merchantName`         | string      | Yes        | Restaurant name                                             |
| └─ `status`               | number      | Yes        | Status type (1 = opening; 0 = closed)                       |
| └─ `merchantDescription`  | string      | No         | Description                                                 |
| └─ `merchantImage`        | string (URL)| No         | Image URL of dish                                           |
| └─ `distance`             | number      | Yes        | Distance from merchant to customer (m)                      |

**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":{
    "total":2,
    "rows":[
      {
        "merchantId":1,
        "merchantName":"Chengdu Taste",
        "status":1,
        "merchantDescription":"Chengdu Taste is a Chinese restaurant located in Chengdu, Sichuan Province, China. It is known for its unique and delicious dishes.",
        "merchantImage":"https://www.example.com/restaurant.jpg",
        "distance": 5000,
      }
      {
        "merchantId":2,
        "merchantName":"Hong Kong Taste",
        "status":1,
        "merchantDescription":"Hong Kong Taste is a Chinese restaurant located in Hong Kong, China. It is known for its unique and delicious dishes.",
        "merchantImage":"https://www.example.com/restaurant.jpg",
        "distance": 300,
      }
    ]
  }
}
```

#### 2.2. Query All Restaurants By Distance
<!-- 还可为merchant增加参数：评分，点赞，销量，评价数。用以选择sorting类型 -->
**Purpose:** Retrieve a paginated, sorted, and filtered list of all restaurants sorted by distance from a specified location.

**Endpoint:** `/client/browse/of/distance`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}
  
**Request Parameter Format:** Query Parameters

**Request Parameter Description:**
| Parameter   | Required   | Example       | Description                                    |
|-------------|------------|---------------|------------------------------------------------|
| `x`         | Yes        | 0.0           | Client's current longitude                     |
| `y`         | Yes        | 0.0           | Client's current latitude                      |
| `page`      | Yes        | 1             | Page number for pagination (default = 1)       |
| `pageSize`  | Yes        | 10            | Number of records per page (default = 10)      |
| `merchantName`   | No         | null     | Merchant name to filter results                |
| `merchantStatus` | No         | 1        | Status type (1 = opening; 0 = closed)          |

**Request Body Sample:**
```shell
/client/browse/of/distance?page=1&pageSize=10&x=0.0&y=0.0
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter                 | Type        | Required   | Description                                                 |
| -----------------         | ----------- | ---------- | ----------------------------------------------------------- |
| `code`                    | number      | Yes        | Response code: 1 = success; 0 = failure                     |
| `msg`                     | string      | No         | Message                                                     |
| `data`                    | object      | No         | Returned data                                               |
| └─ `total`                | number      | Yes        | Total number of records                                     |
| └─ `rows`                 | object[]    | Yes        | List of data records                                        |
| └─ `merchantId`           | number      | Yes        | Merchant ID                                                 |
| └─ `merchantName`         | string      | Yes        | Restaurant name                                             |
| └─ `status`               | number      | Yes        | Status type (1 = opening; 0 = closed)                       |
| └─ `merchantDescription`  | string      | No         | Description                                                 |
| └─ `merchantImage`        | string (URL)| No         | Image URL of dish                                           |
| └─ `distance`             | number      | Yes        | Distance from client (m)                                    |

**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":{
    "total":2,
    "rows":[
      {
        "merchantId":1,
        "merchantName":"Chengdu Taste",
        "status":1,
        "merchantDescription":"Chengdu Taste is a Chinese restaurant located in Chengdu, Sichuan Province, China. It is known for its unique and delicious dishes.",
        "merchantImage":"https://www.example.com/restaurant.jpg",
        "distance":300
      }
      {
        "merchantId":2,
        "merchantName":"Hong Kong Taste",
        "status":1,
        "merchantDescription":"Hong Kong Taste is a Chinese restaurant located in Hong Kong, China. It is known for its unique and delicious dishes.",
        "merchantImage":"https://www.example.com/restaurant.jpg",
        "distance":500
      }
    ]
  }
}
```

#### 2.3. Query Restaurant By Merchant ID

**Purpose:** Retrieve a restaurant in details by merchant ID. Client can go into the restaurant's page and view categories and dishes of the restaurant.

**Endpoint:** `/client/browse/merchants/{merchantId}`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter        | Required   | Example       | Description                                    |
|------------------|------------|---------------|------------------------------------------------|
| `merchantId`     | YES        | 1             | Merchant's ID                                  |

**Request Body Sample:**
```shell
/client/browse/merchants/1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter                 | Type        | Required   | Description                                                 |
| -----------------         | ----------- | ---------- | ----------------------------------------------------------- |
| `code`                    | number      | Yes        | Response code: 1 = success; 0 = failure                     |
| `msg`                     | string      | No         | Message                                                     |
| `data`                    | object      | No         | Returned data                                               |
| └─ `merchantId`           | number      | Yes        | Merchant ID                                                 |
| └─ `merchantName`         | string      | Yes        | Restaurant name                                             |
| └─ `merchantDescription`  | string      | YES        | Description                                                 |
| └─ `merchantImage`        | string (URL)| YES        | Image URL of dish                                           |
| └─ `merchantAddress`      | string      | YES        | Address                                                     |
| └─ `merchantPhone`        | string      | YES        | Phone number                                                |
| └─ `merchantOpeningHours` | string      | YES        | Opening time                                                |


**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":{
    "total":2,
    "rows":[
      {
        "merchantId":1,
        "merchantName":"Chengdu Taste",
        "merchantDescription":"Chengdu Taste",
        "merchantImage":"https://cdn.pixabay.com/photo/2017/01/08/09/05/food-1968525_960_720.jpg",
        "merchantAddress":"Chengdu, China",
        "merchantPhone":"12345678901",
        "merchantOpeningHours":"{\"mon-fri\":\"9-5\",\"sat\":\"10-4\"}"
      }
      {
        "merchantId":2,
        "merchantName":"Hong Kong Taste",
        "merchantDescription":"Hong Kong Taste",
        "merchantImage":"https://cdn.pixabay.com/photo/2017/01/08/09/05/food-1968525_960_720.jpg",
        "merchantAddress":"Hong Kong, China",
        "merchantPhone":"12345678901",
        "merchantOpeningHours":"{\"mon-fri\":\"9-5\",\"sat\":\"10-4\"}"
      }
    ]
  }
}
```

#### 2.4. Query All Categories of a Merchant

**Purpose:** Query all categories when click into a merchant.

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}
  
**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter   | Required   | Example       | Description                                    |
|-------------|------------|---------------|------------------------------------------------|
| `merchantId`| Yes        | 1234567890    | The merchant ID of the restaurant.             |

**Request Body Sample:**
```shell
/client/browse/merchants/{merchantId}/categories
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter                 | Type        | Required   | Description                                                 |
| -----------------         | ----------- | ---------- | ----------------------------------------------------------- |
| `code`                    | number      | Yes        | Response code: 1 = success; 0 = failure                     |
| `msg`                     | string      | No         | Message                                                     |
| `data`                    | object      | No         | Returned data                                               |
| └─ `categoryId`           | number      | Yes        | Category ID                                                 |
| └─ `categoryName`         | string      | Yes        | Category name                                               |
| └─ `status`               | number      | Yes        | Status (1 = enabled; 0 = disabled)                          |
| └─ `sort`                 | number      | Yes        | Sort order                                                  |


**Response Body Sample:**
```json
{
  "code": 1,
  "message": "success",
  "data": {
    "id": 1,
    "categoryName": "Food",
    "status": 1,
    "sort": 1
  },
  {
    "id": 2,
    "categoryName": "Drink",
    "status": 1,
    "sort": 2
  },
  {
    "id": 3,
    "categoryName": "Dessert",
    "status": 1,
    "sort": 3
  }
  ...
}
```

#### 2.5. Query All Dishes in a Category of a Merchant

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}
  
**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter   | Required   | Example       | Description                                    |
|-------------|------------|---------------|------------------------------------------------|
| `merchantId`| Yes        | 1234567890    | The merchant ID of the restaurant.             |
| `categoryId`| Yes        | 1             | The category ID of the dishes.                 |
| `page`      | Yes        | 1             | Page number for pagination (default = 1)       |
| `pageSize`  | Yes        | 10            | Number of records per page (default = 10)      |


**Request Body Sample:**
```shell
/client/browse/merchants/{merchantId}/categories/{categoryId}/dishes
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter                 | Type        | Required   | Description                                                 |
| ------------------------- | ----------- | ---------- | ----------------------------------------------------------- |
| `code`                    | number      | Yes        | Response code: 1 = success; 0 = failure                     |
| `msg`                     | string      | No         | Message                                                     |
| `data`                    | object      | No         | Returned data                                               |
| └─ `total`                | number      | Yes        | Total number of records                                     |
| └─ `rows`                 | object[]    | Yes        | List of data records                                        |
| └─ `dishId`               | number      | Yes        | Dish ID                                                     |
| └─ `dishName`             | string      | Yes        | Dish name                                                   |
| └─ `status`               | number      | Yes        | Status (1 = on sale; 0 = off sale)                          |
| └─ `price`                | number      | Yes        | Price                                                       |
| └─ `dishImage`            | string      | Yes        | Dish image URL                                              |
| └─ `description`          | string      | Yes        | Description                                                 |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
    "total":2,
    "rows":[
      { 
        "id":1,
        "name":"Dish 1",
        "price":10.0,
        "dishImage":"https://example.com/dish1.jpg",
        "description":"Description of Dish 1",
        "status":1
      },
      { 
        "id":2,
        "name":"Dish 2",
        "price":15.0,
        "dishImage":"https://example.com/dish2.jpg",
        "description":"Description of Dish 2",
        "status":1
      }
    ]
}

