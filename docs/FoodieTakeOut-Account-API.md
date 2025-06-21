# FoodieTakeOut API Documentation

## Overview

This document outlines the API endpoints for the account-related functions, which allow users to:

1. **Account Module**
   1. Register merchant/client/rider account using username&password
   2. Login as merchant/client/rider using username&password or OAuth2.0
   3. Logout as merchant/client/rider
   4. View and update profile

2. **Status Module**   
   1. Merchant status change
   2. Rider status change
   3. Get rider current position

## API Endpoints

### 1. Account Module

#### 1.1. Register Merchant/Client/Rider Account (Username/Password)

**Purpose:** Register a new merchant/client/rider account with username and password.

**Endpoint:** `/register`

**Method:** `POST`

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter     | Type         | Required | Description                                                       |
| ------------- | ------------ | -------- | ----------------------------------------------------------------- |
| `username`    | string       | Yes      | Login username for the merchant account.                          |
| `password`    | string       | Yes      | Password for the merchant account (should be stored securely).    |
| `email`       | string       | Yes      | Merchant’s email address.                                         |
| `role`        | string       | Yes      | Role of the user (e.g., `"merchant"`, `"consumer"`, `"rider"`).   |
<!-- Add in backend:
| `profile_completed`  | boolean      | Yes      | Indicates whether the user has completed their profile. Defaults to false, prompting users to complete their profile information upon first login. |
-->

**Request Body Sample:**
```json
{
  "username": "admin",
  "password": "123456",
  "email": "admin@example.com",
  "role": "merchant"
}
```

**Respond Parameter Format:** `application/json`

**Respond Parameter Description:**
| Parameter     | Type   | Required   | Description                             |
| ------------- | ------ | ---------- | --------------------------------------- |
| `code`        | number | Yes        | Response code: 1 = success; 0 = failure |
| `msg`         | string | No         | Message                                 |
| `data`        | object | No         | Returned data                           |

**Respond Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

#### 1.2. Login (Username&Password or OAuth2.0)

**Purpose:** Authenticate user and get JWT access token and refresh token.

**Endpoint:** `/login`

**Method:** `POST`

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter     | Type   | Required                          | Description                                                     |
| ------------- | ------ | --------------------------------- | --------------------------------------------------------------- |
| `loginType`   | string | Yes                               | Login type (e.g., `"password"`, `"oauth2"`).                    |
| `username`    | string | Yes if `loginType` = `"password"` | Login username for the merchant account.                        |
| `password`    | string | Yes if `loginType` = `"password"` | Password for the merchant account (should be stored securely).  |
| `role`        | string | Yes                               | Role of the user (e.g., `"merchant"`, `"consumer"`, `"rider"`). |
| `oauth2Token` | string | Yes if `loginType` = `"oauth2"`   | OAuth2.0 access token for the merchant account.                 |


**Request Body Sample:**
```json
{
  "loginType": "password",
  "username": "admin",
  "password": "123456",
  "role": "merchant",
  "oauth2Token": null
}
```

**Respond Parameter Description:**
| Parameter                   | Type    | Required   | Description                             |
| --------------------------- | ------- | ---------- | --------------------------------------- |
| `code`                      | number  | Yes        | Response code: 1 = success; 0 = failure |
| `msg`                       | string  | No         | Message                                 |
| `data`                      | object  | Yes        | Returned data                           |
| └─ `username`               | string  | Yes        | Username                                |
| └─ `email`                  | string  | Yes        | Email                                   |
| └─ `role`                   | string  | Yes        | Role                                    |
| └─ `accessToken`            | string  | Yes        | AccessToken                             |
| └─ `refreshToken`           | string  | Yes        | RefreshToken                            |
| └─ `needsProfileCompletion` | boolean | Yes        | Indicates whether the user needs to complete their profile |

**Respond Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1001,
    "username": "admin",
    "email": "admin@example.com",
    "role": "merchant",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "needsProfileCompletion": true
  }
}
```

#### 1.3. Logout (Client-side)

**Purpose:** Client removes tokens from storage. No server-side action required with pure JWT.

**Note:** In a pure JWT implementation, logout is handled client-side by removing the tokens from storage. The frontend application should delete both the access token and refresh token from localStorage or cookies when the user logs out, and then redirect to the login page.

#### 1.4. View and update profile

**Purpose:** View user (merchant/client/rider) profile and update profile information.

##### 1.4.1. View profile

**Purpose:** View profile of current user, Fetch and display data.

**Endpoint:** `/profile`

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
| Parameter                  | Type    | Required | Description                                                  |
| -------------------------- | ------- | -------- | ------------------------------------------------------------ |
| `code`                     | number  | Yes      | Response code: 1 = success; 0 = failure                      |
| `msg`                      | string  | No       | Message                                                      |
| `data`                     | object  | Yes      | Profile data object                                          |
| └─`username`               | string  | Yes      | User’s login name                                            |
| └─`email`                  | string  | Yes      | User’s email address                                         |
| └─`role`                   | string  | Yes      | User’s role (e.g., `"merchant"`, `"client"`, `"rider"`)      |
| └─`profileCompleted`       | boolean | Yes      | `true` if the user has completed their profile setup         |
| └─`createTime`             | string  | Yes      | Account creation timestamp                                   |
| └─`updateTime`             | string  | Yes      | Last profile update timestamp                                |
| └─`merchant`               | object  | No       | Merchant-specific profile (present if `role="merchant"`)     |
| └───`merchantId`           | integer | Yes      | Merchant unique ID                                           |
| └───`phone`                | string  | Yes      | Merchant phone number                                        |
| └───`address`              | string  | Yes      | Merchant street address                                      |
| └───`city`                 | string  | Yes      | Merchant city                                                |
| └───`state`                | string  | Yes      | Merchant state                                               |
| └───`zipcode`              | integer | Yes      | Merchant ZIP/postal code                                     |
| └───`country`              | string  | Yes      | Merchant country                                             |
| └───`longitude`            | number  | Yes      | Merchant location longitude (WGS84)                          |
| └───`latitude`             | number  | Yes      | Merchant location latitude (WGS84)                           |
| └───`merchantName`         | string  | Yes      | Merchant display name                                        |
| └───`merchantDescription`  | string  | No       | Merchant description or bio                                  |
| └───`merchantImage`        | string  | No       | URL of merchant image                                        |
| └───`merchantType`         | string  | No       | Type/category of merchant                                    |
| └───`merchantSocialMedia`  | string  | No       | Merchant social media links (e.g., JSON or delimited string) |
| └───`merchantOpeningHours` | string  | Yes      | Merchant opening hours in JSONB (stringified)                |
| └─`client`                 | object  | No       | Client-specific profile (present if `role="client"`)         |
| └───`clientId`             | integer | Yes      | Client unique ID                                             |
| └───`phone`                | string  | Yes      | Client phone number                                          |
| └───`gender`               | string  | Yes      | Client gender code (`"0"`=female, `"1"`=male)                |
| └───`avatar`               | string  | No       | URL of client avatar image                                   |
| └─`rider`                  | object  | No       | Rider-specific profile (present if `role="rider"`)           |
| └───`riderId`              | integer | Yes      | Rider unique ID                                              |
| └───`phone`                | string  | Yes      | Rider phone number                                           |
| └───`gender`               | string  | Yes      | Rider gender code (`"0"`=female, `"1"`=male)                 |
| └───`avatar`               | string  | No       | URL of rider avatar image                                    |

**Respond Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1001,
    "username": "admin",
    "email": "admin@example.com",
    "role": "merchant",
    "profileCompleted": false,
    "createTime": "2025-01-01T00:00:00.000Z",
    "updateTime": "2025-01-01T00:00:00.000Z",
    "merchant": {
      "id": 1001,
      "phone": "12345678901",
      "address": "",
      "city": "",
      "state": "",
      "country": "",
      "zipcode": null,
      "longitude": 0.0,
      "latitude": 0.0,
      "merchantName": "",
      "merchantOpeningHours": "",
    }
  }
}
```

##### 1.4.2. Update profile

**Purpose:** Update current user's profile

**Endpoint:** `/profile`

**Request Method:** `PUT`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `application/json`

**Request Parameter Description:**
| Parameter              | Type    | Required                              | Description                                                  |
| ---------------------- | ------- | ------------------------------------- | ------------------------------------------------------------ |
| `phone`                | string  | Yes                                   | User’s phone number                                          |
| `address`              | string  | Yes if role = `"merchant"`            | Merchant street address                                      |
| `city`                 | string  | Yes if role = `"merchant"`            | Merchant city                                                |
| `state`                | string  | Yes if role = `"merchant"`            | Merchant state                                               |
| `country`              | string  | Yes if role = `"merchant"`            | Merchant country                                             |
| `longitude`            | number  | Yes if role = `"merchant"`            | Merchant location longitude (WGS84)                          |
| `latitude`             | number  | Yes if role = `"merchant"`            | Merchant location latitude (WGS84)                           |
| `merchantName`         | string  | Yes if role = `"merchant"`            | Merchant display name                                        |
| `zipcode`              | integer | No                                    | Merchant ZIP/postal code                                     |
| `merchantDescription`  | string  | No                                    | Merchant description or bio                                  |
| `merchantImage`        | string  | No                                    | URL of merchant image                                        |
| `merchantType`         | string  | No                                    | Type/category of merchant                                    |
| `merchantSocialMedia`  | string  | No                                    | Merchant social media links (e.g., JSON or delimited string) |
| `merchantOpeningHours` | string  | Yes if role = `"merchant"`            | Merchant opening hours in JSON format                        |
| `gender`               | string  | Yes if role = `"client"` or `"rider"` | Gender code: `"0"` = female, `"1"` = male                    |
| `avatar`               | string  | No                                    | URL of user avatar image                                     |

**Request Body Sample:**
```json
{
  "phone": "1234567890",
  "address": "123 Main St",
  "city": "Springfield",
  "state": "IL",
  "country": "USA",
  "longitude": -89.64,
  "latitude": 39.78,
  "merchantName": "Chris's Shop",
  "zipcode": 62704,
  "merchantDescription": "Quality goods.",
  "merchantImage": "https://example.com/shop.png",
  "merchantType": "Retail",
  "merchantSocialMedia": "{\"facebook\":\"fb.com/chris\",\"instagram\":\"@chrisshop\"}",
  "merchantOpeningHours": "{\"mon-fri\":\"9-5\",\"sat\":\"10-4\"}"
}
```

**Respond Parameter Format:** `application/json`

**Respond Parameter Description:**
| Parameter                  | Type    | Required | Description                                                  |
| -------------------------- | ------- | -------- | ------------------------------------------------------------ |
| `code`                     | number  | Yes      | Response code: 1 = success; 0 = failure                      |
| `msg`                      | string  | No       | Message                                                      |
| `data`                     | object  | Yes      | Profile data object                                          |
| └─`username`               | string  | Yes      | User’s login name                                            |
| └─`email`                  | string  | Yes      | User’s email address                                         |
| └─`role`                   | string  | Yes      | User’s role (e.g., `"merchant"`, `"client"`, `"rider"`)      |
| └─`profileCompleted`       | boolean | Yes      | `true` if the user has completed their profile setup         |
| └─`createTime`             | string  | Yes      | Account creation timestamp                                   |
| └─`updateTime`             | string  | Yes      | Last profile update timestamp                                |
| └─`merchant`               | object  | No       | Merchant-specific profile (present if `role="merchant"`)     |
| └───`merchantId`           | integer | Yes      | Merchant unique ID                                           |
| └───`phone`                | string  | Yes      | Merchant phone number                                        |
| └───`address`              | string  | Yes      | Merchant street address                                      |
| └───`city`                 | string  | Yes      | Merchant city                                                |
| └───`state`                | string  | Yes      | Merchant state                                               |
| └───`zipcode`              | integer | Yes      | Merchant ZIP/postal code                                     |
| └───`country`              | string  | Yes      | Merchant country                                             |
| └───`longitude`            | number  | Yes      | Merchant location longitude (WGS84)                          |
| └───`latitude`             | number  | Yes      | Merchant location latitude (WGS84)                           |
| └───`merchantName`         | string  | Yes      | Merchant display name                                        |
| └───`merchantDescription`  | string  | No       | Merchant description or bio                                  |
| └───`merchantImage`        | string  | No       | URL of merchant image                                        |
| └───`merchantType`         | string  | No       | Type/category of merchant                                    |
| └───`merchantSocialMedia`  | string  | No       | Merchant social media links (e.g., JSON or delimited string) |
| └───`merchantOpeningHours` | string  | Yes      | Merchant opening hours in JSONB (stringified)                |
| └─`client`                 | object  | No       | Client-specific profile (present if `role="client"`)         |
| └───`clientId`             | integer | Yes      | Client unique ID                                             |
| └───`phone`                | string  | Yes      | Client phone number                                          |
| └───`gender`               | string  | Yes      | Client gender code (`"0"`=female, `"1"`=male)                |
| └───`avatar`               | string  | No       | URL of client avatar image                                   |
| └─`rider`                  | object  | No       | Rider-specific profile (present if `role="rider"`)           |
| └───`riderId`              | integer | Yes      | Rider unique ID                                              |
| └───`phone`                | string  | Yes      | Rider phone number                                           |
| └───`gender`               | string  | Yes      | Rider gender code (`"0"`=female, `"1"`=male)                 |
| └───`avatar`               | string  | No       | URL of rider avatar image                                    |

**Respond Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "username": "admin",
    "email": "admin@example.com",
    "role": "merchant",
    "profileCompleted": true,
    "createTime": "2025-01-01T00:00:00.000Z",
    "updateTime": "2025-06-11T12:34:56.789Z",
    "merchant": {
      "phone": "1234567890",
      "address": "123 Main St",
      "city": "Springfield",
      "state": "IL",
      "country": "USA",
      "zipcode": 62704,
      "longitude": -89.64,
      "latitude": 39.78,
      "merchantName": "Chris's Shop",
      "merchantDescription": "Quality goods.",
      "merchantImage": "https://example.com/shop.png",
      "merchantType": "Retail",
      "merchantSocialMedia": "{\"facebook\":\"fb.com/chris\",\"instagram\":\"@chrisshop\"}",
      "merchantOpeningHours": "{\"mon-fri\":\"9-5\",\"sat\":\"10-4\"}"
    }
  }
}
```

### 2. Status Module

#### 2.1. Merchant Status Change

**Purpose:** Merchant sets their restaurants opening or closed status.

**Endpoint:** `/merchant/status/{status}`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Body Sample:**
```shell
/merchant/status/0
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description              |
| --------- | ------ | -------- | ------------------------ |
| `code`    | number | Yes      | 1 = success; 0 = failure |
| `msg`     | string | No       | message                  |
| `data`    | null   | No       | Data                     |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Merchant status updated to closed",
  "data": null
}
```

#### 2.2. Rider Status Change

**Purpose:** Rider sets themselves online or offline (start/stop receiving new orders).

**Endpoint:** `/rider/status/{status}`

**Method:** `POST`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** `Path Variable`

**Request Body Sample:**
```shell
/rider/status/0
```

**Response Parameter Description:**
| Parameter | Type   | Required | Description              |
| --------- | ------ | -------- | ------------------------ |
| `code`    | number | Yes      | 1 = success; 0 = failure |
| `msg`     | string | No       | message                  |
| `data`    | null   | No       | Data                     |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "Rider status updated to inactive",
  "data": null
}
```

#### 2.3. Get Rider Current Location

**Purpose:** Allow the dispatch system to fetch the rider's real-time GPS coordinates while they are active, so the server can push nearby orders.

**Endpoint:** `/rider/location`

**Method:** `GET`

**Headers:**
- Authorization: Bearer {accessToken}
- X-Refresh-Token: Bearer {refreshToken}

**Request Parameter Format:** HTTP Header

**Response Parameter Description:**
| Parameter      | Type   | Required | Description                                   |
| -------------- | ------ | -------- | --------------------------------------------- |
| `code`         | number | Yes      | 1 = success; 0 = failure                      |
| `msg`          | string | No       | Human-readable message                        |
| `data`         | object | Yes      | Payload                                       |
| └─ `latitude`  | number | Yes      | Rider's current latitude (WGS84)              |
| └─ `longitude` | number | Yes      | Rider's current longitude (WGS84)             |
| └─ `timestamp` | string | Yes      | ISO timestamp when this position was recorded |

**Response Body Sample:**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "latitude": 39.781234,
    "longitude": -89.642345,
    "timestamp": "2025-06-19T16:05:30.000Z"
  }
}
```