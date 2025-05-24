# FoodieTakeOut API Documentation

## Overview

This document outlines the API endpoints for the smart food delivery service. FoodieTakeOut allows users to:

1. **Merchant Management**
   1. Create merchant account (username/password)
   2. Login and logout via JWT authentication
   3. Manage shop profile information
   4. Manage emloyees information
   5. Manage menu categories
   6. Manage dish items
   7. Manage combo meals
   8. Manage store orders
   <!-- 9.  Manage vouchers -->
   <!-- 10.  Receive new order voice alert -->
   9.  View store data statistic charts
   10. Dashboard function
   <!-- 13. Unregister store -->
   <!-- 14. View account operation logs -->


## API Endpoints

### 1. Merchant Management Module

#### 1.1. Register Merchant (Username/Password)

**Purpose:** Create a new merchant account with username and password.

**Endpoint:** `/merchants/register`

**Method:** `POST`

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter            | Type         | Required | Description                                                       |
| -------------------- | ------------ | -------- | ----------------------------------------------------------------- |
| `username`           | string       | Yes      | Login username for the merchant account.                          |
| `password`           | string       | Yes      | Password for the merchant account (should be stored securely).    |
| `email`              | string       | Yes      | Merchant’s email address.                                         |
| `phone`              | string       | Yes      | Merchant’s contact phone number (e.g., `"217-555-1234"`).         |
| `address`            | string       | Yes      | Street address of the shop (e.g., `"106 Healey St"`).             |
| `city`               | string       | Yes      | City where the shop is located (e.g., `"Champaign"`).             |
| `state`              | string       | Yes      | State or province code (e.g., `"IL"`).                            |
| `zipcode`            | number       | Yes      | Postal code (e.g., `"61820"`).                                    |
| `country`            | string       | Yes      | Country name or code (e.g., `"USA"`).                             |
| `x`                  | number       | Yes      | Longitude coordinate (e.g., `-87.6298`).                          |
| `y`                  | number       | Yes      | Latitude coordinate (e.g., `41.8781`).                            |
| `shopName`           | string       | Yes      | Name of the shop (e.g., `"Chengdu Taste"`).                       |
| `shopDescription`    | string       | No       | Brief description of the shop.                                    |
| `shopImage`          | string (URL) | No       | URL to the shop’s main image or logo.                             |
| `shopType`           | string       | No       | Type of cuisine or category (e.g., `"Chinese Sichuan Food"`).     |
| `shopSocialMedia`    | string       | No       | Comma-separated social media handles (e.g., Instagram, Facebook). |
| `shopOpeningHours`   | object       | Yes      | An object mapping each weekday to its open/close hours.           |

**Request Body Sample:**
```json
{
  "username": "admin",
  "password": "123456",
  "email": "admin@example.com",
  "phone": "217-555-1234",
  "address": "106 Healey St",
  "city": "Champaign",
  "state": "IL",
  "zipcode": "61820",
  "country": "USA",
  "x": -88.23800345645962,
  "y": 40.111771795337546,
  "shopName": "Chengdu Taste",
  "shopDescription": "Chengdu Taste is a Chinese restaurant serving authentic Chinese Sichuan food in Champaign, Illinois.",
  "shopImage": "https://example.com/chengdu_taste.jpg",
  "shopType": "Chinese Sichuan Food",
  "shopSocialMedia": "Instagram: chengdu_taste, Facebook: chengdu_taste, Rednote: chengdu_taste",
  "shopOpeningHours": {
                        "Monday": "10:00 AM - 9:00 PM", 
                        "Tuesday": "closed", 
                        "Wednesday": "10:00 AM - 9:00 PM", 
                        "Thursday": "10:00 AM - 9:00 PM", 
                        "Friday": "10:00 AM - 9:00 PM", 
                        "Saturday": "10:00 AM - 9:00 PM", 
                        "Sunday": "10:00 AM - 9:00 PM"
                      }
}
```

**Respond Parameter Format:** `application/json`

**Respond Parameter Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |

**Respond Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

#### 1.2. Login and logout via JWT authentication (Username/Password)

#####  1.2.1. Login (Username/Password)

**Purpose:** Authenticate user and get JWT access token.

**Endpoint:** `/merchants/login`

**Method:** `POST`

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter            | Type         | Required | Description                                                       |
| -------------------- | ------------ | -------- | ----------------------------------------------------------------- |
| `username`           | string       | Yes      | Login username for the merchant account.                          |
| `password`           | string       | Yes      | Password for the merchant account (should be stored securely).    |

**Request Body Sample:**
```json
{
  "username": "admin",
  "password": "123456"
}
```

**Respond Parameter Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | No         | Message                                 |
| `data`        | object | Yes        | Returned data                           |
| └─ `id`       | number | Yes        | Merchant account ID / Shop ID           |
| └─ `username` | string | Yes        | Username                                |
| └─ `shopName` | string | Yes        | Shop name                               |
| └─ `token`    | string | Yes        | Token                                   |

**Respond Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1001,
    "username": "admin",
    "shopName": "Chengdu Taste",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

##### 1.2.2. Logout (Client-side)

**Purpose:** Client removes tokens from storage. No server-side action required with pure JWT.

**Note:** In a pure JWT implementation, logout is handled client-side by removing the tokens from storage. The frontend application should delete both the access token and refresh token from localStorage or cookies when the user logs out, and then redirect to the login page.

#### 1.3. Manage shop information

##### 1.3.1. View shop information <!-- 查询回显 取线程中id查找 -->

**Purpose:** View shop information.

**Endpoint:** `/merchants/info`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** null

**Response Body Format:** `application/json`

**Response Body Description:**
| `Parameter`           | `Type`       | `Required` | `Description`                                                     |
| --------------------- | ------------ | ---------- | ----------------------------------------------------------------- |
| `code`                | number       | Yes        | Response code: 1 = success; 0 = failure                           |
| `msg`                 | string       | No         | Message                                                           |
| `data`                | object       | Yes        | Returned data                                                     |
| └─ `email`            | string       | Yes        | Merchant’s email address.                                         |
| └─ `phone`            | string       | Yes        | Merchant’s contact phone number (e.g., `"217-555-1234"`).         |
| └─ `address`          | string       | Yes        | Street address of the shop (e.g., `"106 Healey St"`).             |
| └─ `city`             | string       | Yes        | City where the shop is located (e.g., `"Champaign"`).             |
| └─ `state`            | string       | Yes        | State or province code (e.g., `"IL"`).                            |
| └─ `zipcode`          | string       | Yes        | Postal code (e.g., `"61820"`).                                    |
| └─ `country`          | string       | Yes        | Country name or code (e.g., `"USA"`).                             |
| └─ `x`                | number       | Yes        | Longitude coordinate.                                             |
| └─ `y`                | number       | Yes        | Latitude coordinate.                                              |
| └─ `shopName`         | string       | Yes        | Name of the shop (e.g., `"Chengdu Taste"`).                       |
| └─ `shopDescription`  | string       | No         | Brief description of the shop.                                    |
| └─ `shopImage`        | string (URL) | No         | URL to the shop’s main image or logo.                             |
| └─ `shopType`         | string       | No         | Type of cuisine or category (e.g., `"Chinese Sichuan Food"`).     |
| └─ `shopSocialMedia`  | string       | No         | Comma-separated social media handles (e.g., Instagram, Facebook). |
| └─ `shopOpeningHours` | object       | Yes        | An object mapping each weekday to its open/close hours.           |

**Respond Body Sample:**
```json
{ "code": 1,
  "message": "sucess",
  "data": {
    "email": "admin@example.com",
    "phone": "217-555-1234",
    "address": "106 Healey St",
    "city": "Champaign",
    "state": "IL",
    "zipcode": "61820",
    "country": "USA",
    "x": -88.23800345645962,
    "y": 40.111771795337546,
    "shopName": "Chengdu Taste",
    "shopDescription": "Chengdu Taste is a Chinese restaurant serving authentic Chinese Sichuan food in Champaign, Illinois.",
    "shopImage": "https://example.com/chengdu_taste.jpg",
    "shopType": "Chinese Sichuan Food",
    "shopSocialMedia": "Instagram: chengdu_taste, Facebook: chengdu_taste, Rednote: chengdu_taste",
    "shopOpeningHours": {
                          "Monday": "10:00 AM - 9:00 PM", 
                          "Tuesday": "closed", 
                          "Wednesday": "10:00 AM - 9:00 PM", 
                          "Thursday": "10:00 AM - 9:00 PM", 
                          "Friday": "10:00 AM - 9:00 PM", 
                          "Saturday": "10:00 AM - 9:00 PM", 
                          "Sunday": "10:00 AM - 9:00 PM"
                        }
  }                        
}
```

##### 1.3.2. Update shop information

**Purpose:** Update shop information.

**Endpoint:** `/merchants/info/update`

**Method:** `PUT`

**Headers:**
- Authorization: Bearer {token}

**Request Body Format:** `application/json`

**Request Parameter Description:**
| Parameter            | Type         | Required | Description                                                       |
| -------------------- | ------------ | -------- | ----------------------------------------------------------------- |
| `email`              | string       | Yes      | Merchant’s email address.                                         |
| `phone`              | string       | Yes      | Merchant’s contact phone number (e.g., `"217-555-1234"`).         |
| `address`            | string       | Yes      | Street address of the shop (e.g., `"106 Healey St"`).             |
| `city`               | string       | Yes      | City where the shop is located (e.g., `"Champaign"`).             |
| `state`              | string       | Yes      | State or province code (e.g., `"IL"`).                            |
| `zipcode`            | number       | Yes      | Postal code (e.g., `"61820"`).                                    |
| `country`            | string       | Yes      | Country name or code (e.g., `"USA"`).                             |
| `x`                  | number       | Yes      | Longitude coordinate (e.g., `-87.6298`).                          |
| `y`                  | number       | Yes      | Latitude coordinate (e.g., `41.8781`).                            |
| `shopName`           | string       | Yes      | Name of the shop (e.g., `"Chengdu Taste"`).                       |
| `shopDescription`    | string       | No       | Brief description of the shop.                                    |
| `shopImage`          | string (URL) | No       | URL to the shop’s main image or logo.                             |
| `shopType`           | string       | No       | Type of cuisine or category (e.g., `"Chinese Sichuan Food"`).     |
| `shopSocialMedia`    | string       | No       | Comma-separated social media handles (e.g., Instagram, Facebook). |
| `shopOpeningHours`   | object       | Yes      | An object mapping each weekday to its open/close hours.           |

**Request Body Sample:**
```json
{
  "email": "admin@example.com",
  "phone": "217-555-1234",
  "address": "106 Healey St",
  "city": "Champaign",
  "state": "IL",
  "zipcode": "61820",
  "country": "USA",
  "x": -88.23800345645962,
  "y": 40.111771795337546,
  "shopName": "Chengdu Taste",
  "shopDescription": "Chengdu Taste is a Chinese restaurant serving authentic Chinese Sichuan food in Champaign, Illinois.",
  "shopImage": "https://example.com/chengdu_taste.jpg",
  "shopType": "Chinese Sichuan Food",
  "shopSocialMedia": "Instagram: chengdu_taste, Facebook: chengdu_taste, Rednote: chengdu_taste",
  "shopOpeningHours": {
                        "Monday": "10:00 AM - 9:00 PM", 
                        "Tuesday": "closed", 
                        "Wednesday": "10:00 AM - 9:00 PM", 
                        "Thursday": "10:00 AM - 9:00 PM", 
                        "Friday": "10:00 AM - 9:00 PM", 
                        "Saturday": "10:00 AM - 9:00 PM", 
                        "Sunday": "10:00 AM - 9:00 PM"
                      }
}
```

**Respond Parameter Format:** `application/json`

**Respond Parameter Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |

**Respond Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

#### 1.4. Manage Employees Information

**Purpose:** CRUD employees' information.

##### 1.4.1. Query Employees

**Purpose:** Retrieve a paginated, sorted, and filtered list of employees based on specified query criteria.

**Endpoint:** `/merchants/emps`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** Query Parameters

**Request Parameter Description:**
| Parameter   | Required   | Example      | Description                                    |
|-------------|------------|--------------|------------------------------------------------|
| `name`      | No         | Zhang        | Employee name                                  |
| `gender`    | No         | 1            | Gender (1 = male, 2 = female)                  |
| `begin`     | No         | 2020-01-01   | Start date for hire date range filter          |
| `end`       | No         | 2025-01-01   | End date for hire date range filter            |
| `page`      | Yes        | 1            | Page number for pagination (default = 1)       |
| `pageSize`  | Yes        | 10           | Number of records per page (default = 10)      |
| `shopId`    | Yes        | 1            | Only query by current shop ID                  |

**Request Body Sample:**
```shell
/shop/emps?name=Zhang&gender=1&begin=2020-01-01&end=2025-01-01&page=1&pageSize=10&shopId=1
```

**Respond Parameter Format:** `application/json`

**Respond Parameter Description:**
| Parameter       | Type           | Required   | Description                                                                               |
|-----------------|----------------|------------|-------------------------------------------------------------------------------------------|
| `code`          | number         | Yes        | Response code: 1 = success; 0 = failure                                                   |
| `msg`           | string         | No         | Message                                                                                   |
| `data`          | object         | Yes        | Returned data                                                                             |
| └─ `total`      | number         | Yes        | Total number of records                                                                   |
| └─ `rows`       | object[]       | Yes        | List of data records                                                                      |
| └─ `id`         | number         | No         | Employee's ID                                                                             |
| └─ `name`       | string         | No         | Full name                                                                                 |
| └─ `gender`     | number         | No         | Gender (1 = male; 2 = female)                                                             |
| └─ `image`      | string (URL)   | No         | URL to the employee’s avatar.                                                             |
| └─ `phone`      | string         | No         | Phone number of the employee.                                                             |
| └─ `job`        | number         | No         | Position (1 = Manager; 2 = cook; 3 = Waiter / Waitress; 4 = Food Runner; 5 = Dishwasher; 6 = Customer Service) |
| └─ `salary`     | number         | No         | Salary                                                                                    |
| └─ `entryDate`  | string         | No         | Entry date (hire date)                                                                    |
| └─ `shopId`     | number         | No         | Shop ID                                                                                   |
| └─ `createTime` | string         | No         | Creation timestamp                                                                        |
| └─ `updateTime` | string         | No         | Update timestamp                                                                          |


**Respond Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 2,
    "rows": [
       {
        "id": 1,
        "name": "Qiang Zhang",
        "gender": 1,
        "image": "https://web-framework.oss-cn-hangzhou.aliyuncs.com/2022-09-02-00-27-53B.jpg",
        "phone": "13800000000",
        "job": 2,
        "salary": 8000,
        "entryDate": "2015-01-01",
        "shopId": 1,
        "createTime": "2022-09-01T23:06:30",
        "updateTime": "2022-09-02T00:29:04"
      },
      {
        "id": 2,
        "name": "Wukong Sun",
        "gender": 1,
        "image": "https://web-framework.oss-cn-hangzhou.aliyuncs.com/2022-09-02-00-27-53B.jpg",
        "phone": "13800000001",
        "job": 1,
        "salary": 12000,
        "entryDate": "2015-01-01",
        "shopId": 1,
        "createTime": "2022-09-01T23:06:30",
        "updateTime": "2022-09-02T00:29:04"
      }
    ]
  }
}
```

##### 1.4.2. Delete Employees

**Purpose:** Delete / Batch delete employees by employee IDs.

**Endpoint:** `/merchants/emps`

**Method:** `DELETE`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** Query Parameters

**Request Parameter Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `ids`     | array | [1,2,3]  | Yes      | Array of employee IDs |

**Request Body Sample:**
```shell
/shop/emps?ids=1,2,3
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |

**Response Body Sample**
```json
{
    "code":1,
    "msg":"success",
    "data":null
}
```

##### 1.4.3. Add Employees

**Purpose:** Add employee for current shop.

**Endpoint:** `/shop/emps`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter   | Type         | Required | Description                                                                                            |
|-------------|--------------|----------|--------------------------------------------------------------------------------------------------------|
| `name`      | string       | Yes      | Full name                                                                                              |
| `gender`    | number       | Yes      | Gender (1 = male; 2 = female)                                                                          |
| `image`     | string (URL) | No       | Image URL of Avatar                                                                                    |
| `phone`     | string       | No       | Phone number                                                                                           |
| `shopId`    | number       | Yes      | Current shop ID - Automatically generated on client side                                               |
| `job`       | number       | No       | Position (1 = Manager; 2 = cook; 3 = Waiter / Waitress; 4 = Food Runner; 5 = Dishwasher; 6 = Customer Service)    |
| `salary`    | number       | No       | Salary                                                                                                 |

**Request Body Sample**
```json
{
  "image": "https://web-framework.oss-cn-hangzhou.aliyuncs.com/2022-09-03-07-37-38222.jpg",
  "name": "Chris Paul",
  "gender": 1,
  "job": 3,
  "entryDate": "2024-09-18",
  "shopId": 1,
  "phone": "3465771111",
  "salary": 8000,
}
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |

**Response Body Sample**
```json
{
    "code":1,
    "msg":"success",
    "data":null
}
```

##### 1.4.4. Update Employees

**Purpose:** Update employee information in current shop.

**Endpoint:** `/merchants/emps`

**Method:** `PUT`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter   | Type         | Required | Description                                                                                            |
|-------------|--------------|----------|--------------------------------------------------------------------------------------------------------|
| `id`        | integer      | Yes      | Employee's ID                                                                                          |
| `name`      | string       | Yes      | Full name                                                                                              |
| `gender`    | number       | Yes      | Gender (1 = male; 2 = female)                                                                          |
| `image`     | string (URL) | No       | Image URL of Avatar                                                                                    |
| `phone`     | string       | No       | Phone number                                                                                           |
| `shopId`    | number       | Yes      | Current shop ID                                                                                        |
| `entryDate` | string       | Yes      | Entry date (hire date)                                                                                 |
| `job`       | number       | No       | Position (1 = Manager; 2 = cook; 3 = Waiter / Waitress; 4 = Food Runner; 5 = Dishwasher; 6 = Customer Service)    |
| `salary`    | number       | No       | Salary                                                                                                 |

**Request Body Sample**
```json
{
  "id": 1,
  "image": "https://web-framework.oss-cn-hangzhou.aliyuncs.com/2022-09-03-07-37-38222.jpg",
  "name": "Chris Paul",
  "gender": 1,
  "job": 3,
  "entryDate": "2024-09-18",
  "shopId": 1,
  "phone": "3465771111",
  "salary": 8000,
}
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |

**Response Body Sample**
```json
{
    "code":1,
    "msg":"success",
    "data":null
}
```

##### 1.4.5. Get Employee Information By ID

**Purpose:** Retrieve employee information based on employee ID. <!-- 更新员工信息之前，用于查询回显 -->

**Endpoint:** `/merchants/emps/{id}`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter   | Type         | Required | Description    |
| `id`        | integer      | Yes      | Employee's ID  |

**Request Body Sample**
```shell
/shop/emps/1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter       | Type         | Required | Description                                                                                       |
|-----------------|--------------|----------|---------------------------------------------------------------------------------------------------|
| `code`          | number       | Yes      | Response code: 1 = success; 0 = failure                                                           |
| `msg`           | string       | Yes      | Message                                                                                           |
| `data`          | object       | No       | Employee data object                                                                              |
| └─ `id`         | integer      | Yes      | Employee’s ID                                                                                     |
| └─ `name`       | string       | Yes      | Full name                                                                                         |
| └─ `gender`     | number       | Yes      | Gender (1 = male; 2 = female)                                                                     |
| └─ `image`      | string (URL) | No       | Avatar image URL                                                                                  |
| └─ `phone`      | string       | No       | Phone number                                                                                      |
| └─ `shopId`     | number       | Yes      | Current shop ID                                                                                   |
| └─ `entryDate`  | string       | Yes      | Entry date (hire date)                                                                            |
| └─ `job`        | number       | No       | Position (1 = Manager; 2 = Cook; 3 = Waiter/Waitress; 4 = Food Runner; 5 = Dishwasher; 6 = Customer Service) |
| └─ `salary`     | number       | No       | Salary                                                                                            |



#### 1.5. Manage Menu Categories

**Purpose:** CRUD categories of dishes.

##### 1.5.1. Query Menu Categories

**Purpose:** Retrieve a paginated, sorted, and filtered list of all menu categories based on specified query criteria.

**Endpoint:** `/merchants/categories`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** Query Parameters

**Request Parameter Description:**
| Parameter   | Required   | Example      | Description                                    |
|-------------|------------|--------------|------------------------------------------------|
| `name`      | No         | Dessert      | Category's name                                |
| `page`      | Yes        | 1            | Page number for pagination (default = 1)       |
| `pageSize`  | Yes        | 10           | Number of records per page (default = 10)      |
| `type`      | No         | 1            | Category type (1 = dish category; 2 = combo meal category) |

**Request Body Sample:**
```shell
/shop/categories?name=Dessert&page=1&pageSize=10&type=1
```

**Respond Parameter Format:** `application/json`

**Respond Parameter Description:**
| Parameter       | Type           | Required   | Description                                                                               |
|-----------------|----------------|------------|-------------------------------------------------------------------------------------------|
| `code`          | number         | Yes        | Response code: 1 = success; 0 = failure                                                   |
| `msg`           | string         | No         | Message                                                                                   |
| `data`          | object         | Yes        | Returned data                                                                             |
| └─ `total`      | number         | Yes        | Total number of records                                                                   |
| └─ `rows`       | object[]       | Yes        | List of data records                                                                      |
| └─ `id`         | number         | No         | Category ID                                                                               |
| └─ `name`       | string         | No         | Category name                                                                             |
| └─ `sort`       | number         | No         | Number for sorting                                                                        |
| └─ `type`       | number         | No         | Category type (1 = dish category; 2 = combo meal category)                                |
| └─ `createTime` | string         | No         | Creation timestamp                                                                        |
| └─ `updateTime` | string         | No         | Update timestamp                                                                          |


**Respond Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 2,
    "rows": [
       {
        "id": 1,
        "name": "Dessert",
        "sort": 1,
        "type": 1,
        "createTime": "2022-09-01T23:06:30",
        "updateTime": "2022-09-02T00:29:04"
      },
      {
        "id": 2,
        "name": "Traditional Staples",
        "sort": 1,
        "type": 1,
        "createTime": "2022-09-01T23:06:30",
        "updateTime": "2022-09-02T00:29:04"
      }
    ]
  }
}
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |

**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":null
}
```

##### 1.5.2. Create Menu Categories

**Purpose:** Create new menu categories.

**Endpoint:** `/merchants/categories`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter | Type    | Required | Description                                                        |
|-----------|---------|----------|--------------------------------------------------------------------|
| `id`      | integer | No       | Category ID                                                        |
| `name`    | string  | Yes      | Category name                                                      |
| `sort`    | integer | Yes      | Sort order (ascending)                                             |
| `type`    | integer | Yes      | Category type (1 = dish category; 2 = combo meal category)         |

**Request Body Sample**
```json
{
  "name": "Dessert",
  "sort": 1,
  "type": 1
}
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |

**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":null
}
```

##### 1.5.3. Update Menu Categories

**Purpose:** Update menu categories.

**Endpoint:** `/merchants/categories`

**Method:** `PUT`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter | Type    | Required | Description                                                        |
|-----------|---------|----------|--------------------------------------------------------------------|
| `id`      | integer | Yes      | Category ID                                                        |
| `name`    | string  | Yes      | Category name                                                      |
| `sort`    | integer | Yes      | Sort order (ascending)                                             |
| `type`    | integer | Yes      | Category type (1 = dish category; 2 = combo meal category)         |

**Request Body Sample**
```json
{
  "id": 1,
  "name": "Dessert",
  "sort": 1,
  "type": 1
}
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |

**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":null
}
```

##### 1.5.4. Delete Menu Categories

**Purpose:** Delete / Batch delete menu categories based on IDs.

**Endpoint:** `/merchants/categories`

**Method:** `DELETE`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** Query Parameters

**Request Parameter Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `ids`     | array | [1,2,3]  | Yes      | Array of category IDs |

**Request Body Sample:**
```shell
/shop/categories?ids=1,2,3
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |

**Response Body Sample**
```json
{
    "code":1,
    "msg":"success",
    "data":null
}
```

##### 1.5.5. Get Menu Category By ID

**Purpose:** Retrieve a specific menu category based on its ID.

**Endpoint:** `/merchants/categories/{id}`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** `Path Variable`

**Request Parameter Description:**
| Parameter | Type    | Required | Description                                                        |
|-----------|---------|----------|--------------------------------------------------------------------|
| `id`      | integer | Yes      | Category ID                                                        |

**Request Body Sample:**
```shell
/shop/categories/1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | Yes        | Message                                 |
| `data`        | object | No         | Returned data                           |
| └─ `id`       | integer      | Yes      | Category ID                         |
| └─ `name`     | string       | Yes      | Category name                       |
| └─ `sort`     | number       | Yes      | Sort order (ascending)              |
| └─ `type`     | number       | Yes      | Category type (1 = dish category; 2 = combo meal category) |

**Response Body Sample**
```json
{
  "code":1,
  "msg":"success",
  "data":{
    "id":1,
    "name":"Dessert",
    "sort":1,
    "type":1
  }
}
```

#### 1.6. Manage Dish Items

**Purpose:** CRUD dish items.

##### 1.6.1. Query Dish Items

**Purpose:** Retrieve a paginated, sorted, and filtered list of all dish items based on specified query criteria.

**Endpoint:** `/merchants/dishes`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** Query Parameters

**Request Parameter Description:**
| Parameter     | Required   | Example      | Description                                    |
|---------------|------------|--------------|------------------------------------------------|
| `categoryId`  | No         | 1            | Category ID                                    |
| `name`        | No         | Tiramisu     | dish's name                                    |
| `page`        | Yes        | 1            | Page number for pagination (default = 1)       |
| `pageSize`    | Yes        | 10           | Number of records per page (default = 10)      |
| `status`      | No         | 1            | Status type (1 = avaliable; 0 = unavaliable)   |

**Request Body Sample:**
```shell
/shop/dishes?categoryType=1&page=1&pageSize=10&status=1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter       | Type        | Required   | Description                                                 |
| --------------- | ----------- | ---------- | ----------------------------------------------------------- |
| `code`          | number      | Yes        | Response code: 1 = success; 0 = failure                     |
| `msg`           | string      | Yes        | Message                                                     |
| `data`          | object      | No         | Returned data                                               |
| └─ `total`      | number      | Yes        | Total number of records                                     |
| └─ `rows`       | object[]    | Yes        | List of data records                                        |
| └─ `id`         | number      | No         | dish ID                                                     |
| └─ `name`       | string      | No         | dish name                                                   |
| └─ `categoryId` | number      | No         | Category ID                                                 |
| └─ `price`      | number      | No         | Price                                                       |
| └─ `status`     | number      | No         | Status type (1 = avaliable; 0 = unavaliable)                |
| └─ `description`| string      | No         | Description                                                 |
| └─ `image`      | string (URL)| No         | Image URL of dish                                           |
| └─ `createTime` | string      | No         | Creation timestamp                                          |
| └─ `updateTime` | string      | No         | Update timestamp                                            |

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

##### 1.6.2. Create Dish Item

**Purpose:** Create a new dish item.

**Endpoint:** `/merchants/dishes`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {token}

**Request Body Format:** `application/json`

**Request Body Description:**
| Parameter     | Type        | Required   | Description                                                 |
| ------------- | ----------- | ---------- | ----------------------------------------------------------- |
| `name`        | string      | Yes        | dish name                                                   |
| `categoryId`  | number      | Yes        | Category ID                                                 |
| `price`       | number      | Yes        | Price                                                       |
| `status`      | number      | Yes        | Status type (1 = avaliable; 0 = unavaliable)                |
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
| `msg`           | string      | Yes        | Message                                                     |
| `data`          | object      | No         | Returned data                                               |

**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": null
}
```

##### 1.6.3. Delete Dish Item

**Purpose:** Delete / Batch delete dish items based on IDs.

**Endpoint:** `/merchants/dishes`

**Method:** `DELETE`

**Headers:**
- Authorization: Bearer {token}

**Request Body Format:** Query Parameters

**Request Body Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `ids`     | array | [1,2,3]  | Yes      | Array of dish IDs     |

**Request Body Sample:**
```shell
/shop/dishes?ids=1,2,3
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter | Type        | Required   | Description                              |
|-----------|-------------|------------|------------------------------------------|
| `code`    | number      | Yes        | Response code: 1 = success; 0 = failure  |
| `msg`     | string      | Yes        | Message                                  |
| `data`    | object      | No         | Returned data                            |
**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": null
}
```

##### 1.6.4. Get Dish Item By ID

**Purpose:** Get dish item by ID. <!-- 获取指定ID的菜品，用于更新前的查询回显 -->

**Endpoint:** `/merchants/dishes/{id}`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {token}

**Request Body Format:** `Path Variable`

**Request Body Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `id`      | number| 1        | Yes      | Dish ID               |

**Request Body Sample:**
```shell
/shop/dishes/1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter       | Type        | Required   | Description                                   |
| --------------- | ----------- | ---------- | --------------------------------------------- |
| `code`          | number      | Yes        | Response code: 1 = success; 0 = failure       |
| `msg`           | string      | Yes        | Message                                       |
| `data`          | object      | Yes        | Returned data                                 |
| └─ `id`         | number      | Yes        | dish ID                                       |
| └─ `name`       | string      | Yes        | dish name                                     |
| └─ `categoryId` | number      | Yes        | Category ID                                   |
| └─ `price`      | number      | Yes        | Price                                         |
| └─ `status`     | number      | Yes        | Status type (1 = avaliable; 0 = unavaliable)  |
| └─ `description`| string      | No         | Description                                   |
| └─ `image`      | string (URL)| No         | Image URL of dish                             |
| └─ `createTime` | string      | No         | Creation timestamp                            |
| └─ `updateTime` | string      | No         | Update timestamp                              |

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

##### 1.6.5. Update Dish Item

**Purpose:** Update dish item.

**Endpoint:** `/merchants/dishes`

**Method:** `PUT`

**Headers:**
- Authorization: Bearer {token}

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

##### 1.6.6. Dish Item Status Change

**Purpose:** Change dish item status rather than delete it.

**Endpoint:** `/merchants/dishes/status`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {token}

**Request Body Format:** `Path Variable` + Query Parameters

**Request Body Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `status`  | number| 1        | Yes      | Status type (1 = avaliable; 0 = unavaliable) |
| `id`      | number| 1        | Yes      | Dish ID               |

**Request Body Sample:**
```shell
/shop/dishes/status/{status}?id=1
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



#### 1.7. Manage Combo Meals

**Purpose:** CRUD combo meals.

##### 1.7.1. Create Combo Meal

**Purpose:** Create combo meal.

**Endpoint:** `/merchants/combos`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {token}

**Request Body Format:** `application/json`

**Request Body Description:**
| Parameter        | Type       | Required | Description                                        |
|------------------|------------|----------|----------------------------------------------------|
| `categoryId`     | integer    | Yes      | Category ID                                        |
| `description`    | string     | No       | Combo meal description                             |
| `id`             | integer    | No       | Combo meal ID                                      |
| `image`          | string     | No       | Combo meal image URL                               |
| `name`           | string     | Yes      | Combo meal name                                    |
| `price`          | number     | Yes      | Combo meal price                                   |
| `combomealDishes`| object[]   | Yes      | List of dishes included in the combo meal          |
| ├─ `copies`      | integer    | Yes      | Quantity of this dish in the combo meal            |
| ├─ `dishId`      | integer    | Yes      | Dish ID                                            |
| ├─ `id`          | integer    | No       | ID in Combo Meal–Dish Relationship Table           |
| ├─ `name`        | string     | Yes      | Dish name                                          |
| ├─ `price`       | number     | Yes      | Dish price                                         |
| ├─ `combomealId` | integer    | Yes      | Combo meal ID (link back to parent combo meal)     |
| `dicountRate`    | String     | Yes      | Combo meal discount rate (eg, 20% off)             |
| `status`         | integer    | Yes      | Combo meal status (1 = available; 0 = unavailable) |

**Request Body Sample:**
```json
{
  "categoryId":1,
  "description":"Combo meal description...",
  "id":1,
  "image":"https://www.example.com/combo_meal.jpg",
  "name":"Combo Meal A",
  "price":35.00,
  "combomealDishes":[
    {
      "copies":1,
      "dishId":1,
      "id":1,
      "name":"Tiramisu",
      "price":10.00,
      "combomealId":1
    }
    {
      "copies":2,
      "dishId":2,
      "id":2,
      "name":"Coffee",
      "price":15.00,
      "combomealId":1
    }
    {
      "copies":1,
      "dishId":3,
      "id":3,
      "name":"Strawberry Cake",
      "price":20.00,
      "combomealId":1
    }
  ],
  "dicountRate":"22% off",
  "status":1
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

##### 1.7.2. Query Combo Meal

**Purpose:** Retrieve a paginated, sorted, and filtered list of all combo meals based on specified query criteria.

**Endpoint:** `/merchants/combos`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {token}

**Request Parameter Format:** Query Parameters

**Request Parameter Description:**
| Parameter     | Required   | Example      | Description                                    |
|---------------|------------|--------------|------------------------------------------------|
| `categoryId`  | No         | 2            | categoryId ID (eg, 2 = 2-person combo; 3 = 3-person combo) |
| `name`        | No         | Combo A      | Combo's name                                   |
| `page`        | Yes        | 1            | Page number for pagination (default = 1)       |
| `pageSize`    | Yes        | 10           | Number of records per page (default = 10)      |
| `status`      | No         | 1            | Status type (1 = avaliable; 0 = unavaliable)   |

**Request Body Sample:**
```shell
/shop/dishes?categoryType=2&page=1&pageSize=10&status=1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter        | Type       | Required | Description                                            |
|------------------|------------|----------|--------------------------------------------------------|
| `code`           | number     | Yes      | Response code: 1 = success; 0 = failure                |
| `msg`            | string     | No       | Message (nullable)                                     |
| `data`           | object     | No       | Response payload                                       |
| ├─ `total`       | number     | No       | Total number of records                                |
| ├─ `rows`        | object[]   | No       | List of combo meal records                             |
| ├─ `id`          | number     | Yes      | Combo meal ID                                          |
| ├─ `categoryId`  | number     | Yes      | Category ID                                            |
| ├─ `name`        | string     | Yes      | Combo meal name                                        |
| ├─ `price`       | number     | Yes      | Combo meal price                                       |
| ├─ `status`      | number     | Yes      | Combo meal status (1 = available; 0 = unavailable)     |
| ├─ `description` | string     | Yes      | Combo meal description                                 |
| ├─ `image`       | string     | Yes      | Combo meal image URL                                   |
| ├─ `updateTime`  | string     | Yes      | Last update timestamp (ISO 8601)                       |
| ├─ `categoryName`| string     | Yes      | Category name                                          |
| └─ `dicountRate` | string     | Yes      | Combo meal discount rate (eg, 20% off)                 |

**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": {
    "total":1,
    "rows":[
      { 
        "id":1,
        "categoryId":2,
        "name":"Valentine Combo",
        "price":35.00,
        "status":1,
        "description":"Combo meal description...",
        "image":"https://www.example.com/combo_meal.jpg",
        "updateTime":"2023-05-01T12:00:00Z",
        "categoryName":"2-person combo",
        "dicountRate":"22% off"
      }
    ]
  }
}
```

##### 1.7.3. Delete Combo Meal

**Purpose:** Delete / Batch delete combo meals based on IDs.

**Endpoint:** `/merchants/combos`

**Method:** `DELETE`

**Headers:**
- Authorization: Bearer {token}

**Request Body Format:** Query Parameters

**Request Body Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `ids`     | array | [1,2,3]  | Yes      | Array of combo IDs    |

**Request Body Sample:**
```shell
/shop/combos?ids=1,2,3
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter | Type        | Required   | Description                              |
|-----------|-------------|------------|------------------------------------------|
| `code`    | number      | Yes        | Response code: 1 = success; 0 = failure  |
| `msg`     | string      | Yes        | Message                                  |
| `data`    | object      | No         | Returned data                            |
**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": null
}
```

##### 1.7.4. Get Combo Meal by ID

**Purpose:** Retrieve a specific combo meal by its ID.

**Endpoint:** `/merchants/combos/{id}`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {token}

**Request Body Format:** `Path Variable`

**Request Body Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `id`      | number| 1        | Yes      | Combo ID              |

**Request Body Sample:**
```shell
/shop/combos/1
```

**Response Body Format:** `application/json`

**Response Body Description:**
| Parameter                 | Type       | Required | Description                                            |
|---------------------------|------------|----------|--------------------------------------------------------|
| `code`                    | number     | Yes      | Response code: 1 = success; 0 = failure                |
| `msg`                     | string     | No       | Message                                                |
| `data`                    | object     | Yes      | Combo meal details object                              |
| ├─ `id`                   | integer    | Yes      | Combo meal ID                                          |
| ├─ `categoryId`           | integer    | Yes      | Category ID                                            |
| ├─ `categoryName`         | string     | Yes      | Category name                                          |
| ├─ `name`                 | string     | Yes      | Combo meal name                                        |
| ├─ `price`                | number     | Yes      | Combo meal price                                       |
| ├─ `status`               | number     | Yes      | Combo meal status (1 = available; 0 = unavailable)     |
| ├─ `description`          | string     | Yes      | Combo meal description                                 |
| ├─ `image`                | string     | Yes      | Combo meal image URL                                   |
| ├─ `updateTime`           | string     | Yes      | Last update timestamp (ISO 8601)                       |
| ├─ `discountRate`         | string     | Yes      | Combo meal discount rate (e.g., 20% off)               |
| └─ `combomealDishes`      | object[]   | Yes      | List of dishes included in the combo meal              |
|   ├─ `copies`             | integer    | Yes      | Quantity of this dish in the combo meal                |
|   ├─ `dishId`             | integer    | Yes      | Dish ID                                                |
|   ├─ `id`                 | integer    | Yes      | Relationship ID between combo meal and dish            |
|   ├─ `name`               | string     | Yes      | Dish name                                              |
|   ├─ `price`              | number     | Yes      | Dish price                                             |
|   └─ `combomealId`        | integer    | Yes      | Combo meal ID (link back to parent combo meal)         |

**Response Body Sample:**
```json
{
  "code":1,
  "msg":"success",
  "data": { 
    "id":1,
    "categoryId":2,
    "name":"Valentine Combo",
    "price":35.00,
    "status":1,
    "discription":"Combo for Valentine",
    "image":"https://www.example.com/images/valentine.jpg",
    "updateTime":"2025-01-01T00:00:00",
    "discountRate":"22% off",
    "combomealDishes": [
      {
        "copies":2,
        "dishId":1,
        "id":1,
        "name":"Tiramisu",
        "price":20.00,
        "combomealId":1
      }
      {
        "copies":1,
        "dishId":2,
        "id":2,
        "name":"Coffee",
        "price":10.00,
        "combomealId":1
      }
    ]
  }
}
```

##### 1.7.5. Update Combo Meal

**Purpose:** Update a specific combo meal.

**Endpoint:** `/merchants/combos`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {token}

**Request Body Format:** `application/json`

**Request Body Description:**
| Parameter                 | Type       | Required | Description                                            |
|---------------------------|------------|----------|--------------------------------------------------------|
| `id`                   | integer    | Yes      | Combo meal ID                                          |
| `categoryId`           | integer    | Yes      | Category ID                                            |
| `categoryName`         | string     | No       | Category name                                          |
| `name`                 | string     | Yes      | Combo meal name                                        |
| `price`                | number     | Yes      | Combo meal price                                       |
| `status`               | number     | Yes      | Combo meal status (1 = available; 0 = unavailable)     |
| `description`          | string     | No       | Combo meal description                                 |
| `image`                | string     | No       | Combo meal image URL                                   |
| `updateTime`           | string     | No       | Last update timestamp (ISO 8601)                       |
| `discountRate`         | string     | No       | Combo meal discount rate (e.g., 20% off)               |
| `combomealDishes`      | object[]   | Yes      | List of dishes included in the combo meal              |
|  ├─ `copies`           | integer    | Yes      | Quantity of this dish in the combo meal                |
|  ├─ `dishId`           | integer    | Yes      | Dish ID                                                |
|  ├─ `id`               | integer    | No       | Relationship ID between combo meal and dish            |
|  ├─ `name`             | string     | No       | Dish name                                              |
|  ├─ `price`            | number     | No       | Dish price                                             |
|  └─ `combomealId`      | integer    | Yes      | Combo meal ID (link back to parent combo meal)         |

**Request Body Sample:**
```json
{
  "id":1,
  "categoryId":2,
  "name":"Valentine Combo",
  "price":35.00,
  "status":1,
  "discription":"Combo for Valentine",
  "image":"https://www.example.com/images/valentine.jpg",
  "updateTime":"2025-01-01T00:00:00",
  "discountRate":"22% off",
  "combomealDishes": [
    {
      "copies":2,
      "dishId":1,
      "id":1,
      "name":"Tiramisu",
      "price":20.00,
      "combomealId":1
    }
    {
      "copies":1,
      "dishId":2,
      "id":2,
      "name":"Coffee",
      "price":10.00,
      "combomealId":1
    }
  ]
}
```

##### 1.7.6 Combo Meal Status Change

**Purpose:** Change combo meal status rather than delete it.

**Endpoint:** `/merchants/combos/status`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {token}

**Request Body Format:** `Path Variable` + Query Parameters

**Request Body Description:**
| Parameter | Type  | Example  | Required | Description           |
|-----------|-------|----------|----------|-----------------------|
| `status`  | number| 1        | Yes      | Status type (1 = avaliable; 0 = unavaliable) |
| `id`      | number| 1        | Yes      | Combo ID              |

**Request Body Sample:**
```shell
/shop/combos/status/{status}?id=1
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


#### 1.8. Manage Orders

**Purpose:** View historical completed orders, view & accept & cancel current orders.

#### 1.9. View Store Data Statistic Charts

**Purpose:** View store data statistic charts, such as revenue, rank of popular dishes, order count, total/new customer count, etc.

#### 1.10. Dashboard Functions

**Purpose:** Overview of store daily data, such as revenue, order count, total/new customer count, etc. Overview of daily top 3 dishes. Overview of current orders.