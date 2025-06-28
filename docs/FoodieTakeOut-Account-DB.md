# FoodieTakeOut Database Schema

This document outlines the database schema for the FoodieTakeOut service, which includes tables for account module, category&dish module, order module and map module.

## Account Module Tables
### Table: users

| Column                 | Type              | Constraints     | Description                                                     |
|------------------------|-------------------|-----------------|-----------------------------------------------------------------|
| id                     | BIGSERIAL         | PRIMARY KEY     | Primary key ID of the user                                      |
| username               | TEXT              | NOT NULL UNIQUE | Merchant’s username (e.g., `"admin"`)                           |
| password               | TEXT              |                 | Merchant’s password (e.g., `"123456"`)                          |
| email                  | TEXT              | NOT NULL UNIQUE | Merchant’s email address                                        |
| role                   | TEXT              | NOT NULL        | Role of the user (e.g., `"merchant"`, `"consumer"`, `"rider"`)  |
| profile_completed      | BOOLEAN           | NOT NULL        | Whether the merchant has completed their profile                |
| create_time            | TIMESTAMP(0)         | NOT NULL DEFAULT NOW() | Date and time when the record was created                |
| update_time            | TIMESTAMP(0)         | NOT NULL DEFAULT NOW() | Date and time when the record was last updated           |

**Indexes:**  
- **PRIMARY KEY** on `id`

**SQL for creating the users table:**

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT,
    email TEXT NOT NULL UNIQUE,
    profile_completed BOOLEAN NOT NULL,
    role TEXT NOT NULL,
    create_time TIMESTAMP(0) NOT NULL DEFAULT NOW(),
    update_time TIMESTAMP(0) NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE users IS 'Stores general account information for all user types.';
COMMENT ON COLUMN users.id IS 'Primary key ID of the user.';
COMMENT ON COLUMN users.username IS 'Merchant’s username (e.g., "admin").';
COMMENT ON COLUMN users.password IS 'Merchant’s password (e.g., "123456").';
COMMENT ON COLUMN users.email IS 'Merchant’s email address.';
COMMENT ON COLUMN users.profile_completed IS 'Whether the merchant has completed their profile.';
COMMENT ON COLUMN users.role IS 'Role of the user (e.g., "merchant", "consumer", "rider").';
COMMENT ON COLUMN users.create_time IS 'Date and time when the record was created.';
COMMENT ON COLUMN users.update_time IS 'Date and time when the record was last updated.';
```
### Table: merchants

| Column                 | Type              | Constraints     | Description                                                      |
|------------------------|-------------------|-----------------|------------------------------------------------------------------|
| id                     | BIGSERIAL         | PRIMARY KEY     | Unique merchant identifier                                       |
| user_id                | BIGINT            | NOT NULL        | Foreign key reference to the `users` table                       |
| phone                  | VARCHAR(15)       | NOT NULL        | Merchant’s contact phone number (e.g., `"217-555-1234"`)         |
| address                | TEXT              | NOT NULL        | Street address of the shop (e.g., `"106 Healey St"`)             |
| city                   | TEXT              | NOT NULL        | City where the shop is located (e.g., `"Champaign"`)             |
| state                  | VARCHAR(10)       | NOT NULL        | State or province code (e.g., `"IL"`)                            |
| zipcode                | TEXT              |                 | Postal code (e.g., `"61820"`)                                    |
| country                | VARCHAR(20)       | NOT NULL        | Country name or code (e.g., `"USA"`)                             |
| x                      | DOUBLE PRECISION  | NOT NULL        | Longitude coordinate                                             |
| y                      | DOUBLE PRECISION  | NOT NULL        | Latitude coordinate                                              |
| merchant_name          | VARCHAR(100)      | NOT NULL        | Name of the shop (e.g., `"Chengdu Taste"`)                       |
| merchant_description   | TEXT              |                 | Brief description of the shop                                    |
| merchant_image         | TEXT              |                 | URL to the shop’s main image or logo                             |
| merchant_type          | TEXT              |                 | Type of cuisine or category (e.g., `"Chinese Sichuan Food"`)     |
| merchant_social_media  | TEXT              |                 | Comma-separated social media handles (e.g., Instagram, Facebook) |
| merchant_opening_hours | JSONB             | NOT NULL        | Mapping of each weekday to open/close hours                      |
| merchant_status        | SMALLINT          | NOT NULL DEFAULT 0 | Whether the shop is open or closed (0 = closed, 1 = open)     |
| location               | GEOMETRY(Point, 4326)  | NOT NULL      | Geographic point representing the shop’s location             |

**Indexes:**  
- **PRIMARY KEY** on `id`
- **INDEX** `idx_merchants_user_id` on `user_id` 

**SQL for creating the merchants table:**

```sql
CREATE TABLE merchants (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    phone VARCHAR(15) NOT NULL,
    address TEXT NOT NULL,
    city TEXT NOT NULL,
    state VARCHAR(10) NOT NULL,
    zipcode TEXT,
    country VARCHAR(20) NOT NULL,
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION NOT NULL,
    merchant_name VARCHAR(100) NOT NULL,
    merchant_description TEXT,
    merchant_image TEXT,
    merchant_type TEXT,
    merchant_social_media TEXT,
    merchant_opening_hours JSONB NOT NULL,
    merchant_status SMALLINT NOT NULL DEFAULT 0,
    location GEOMETRY(Point, 4326) NOT NULL
);

CREATE INDEX idx_merchants_user_id ON merchants(user_id);

COMMENT ON TABLE merchants IS 'Contains detailed profile and metadata for merchant accounts.';
COMMENT ON COLUMN merchants.id IS 'Unique merchant identifier.';
COMMENT ON COLUMN merchants.user_id IS 'Foreign key reference to the users table.';
COMMENT ON COLUMN merchants.phone IS 'Merchant’s contact phone number (e.g., "217-555-1234").';
COMMENT ON COLUMN merchants.address IS 'Street address of the shop.';
COMMENT ON COLUMN merchants.city IS 'City where the shop is located.';
COMMENT ON COLUMN merchants.state IS 'State or province code (e.g., "IL").';
COMMENT ON COLUMN merchants.zipcode IS 'Postal code (e.g., "61820").';
COMMENT ON COLUMN merchants.country IS 'Country name or code (e.g., "USA").';
COMMENT ON COLUMN merchants.x IS 'Longitude coordinate.';
COMMENT ON COLUMN merchants.y IS 'Latitude coordinate.';
COMMENT ON COLUMN merchants.merchant_name IS 'Name of the shop (e.g., "Chengdu Taste").';
COMMENT ON COLUMN merchants.merchant_description IS 'Brief description of the shop.';
COMMENT ON COLUMN merchants.merchant_image IS 'URL to the shop’s main image or logo.';
COMMENT ON COLUMN merchants.merchant_type IS 'Type of cuisine or business category.';
COMMENT ON COLUMN merchants.merchant_social_media IS 'Comma-separated social media handles.';
COMMENT ON COLUMN merchants.merchant_opening_hours IS 'JSON mapping of each weekday to opening and closing hours.';
COMMENT ON COLUMN merchants.merchant_status IS 'Whether the shop is open or closed.';
COMMENT ON COLUMN merchants.location IS 'Geographic point representing the shop’s location.';
```

### Table: clients

| Column       | Type        | Constraints            | Description                                  |
| ------------ | ----------- | ---------------------- | -------------------------------------------- |
| id           | BIGSERIAL   | PRIMARY KEY            | Unique client identifier                     |
| user_id      | BIGINT      | NOT NULL               | Foreign key reference to the `users` table   |
| phone        | VARCHAR(15) | NOT NULL               | Client’s contact phone number                |
| gender       | VARCHAR(6)  | NOT NULL               | Client’s gender (`"0"` = female, `"1"` = male) |
| avatar       | TEXT        |                        | URL to the client’s avatar                   |

**Indexes:**  
- **PRIMARY KEY** on `id`
- **INDEX** `idx_clients_user_id` on `user_id` 

```sql
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    phone VARCHAR(15) NOT NULL,
    gender VARCHAR(6) NOT NULL,
    avatar TEXT
);

CREATE INDEX idx_clients_user_id ON clients(user_id);

COMMENT ON TABLE clients IS 'Stores profile details for client users.';
COMMENT ON COLUMN clients.id IS 'Unique client identifier.';
COMMENT ON COLUMN clients.user_id IS 'Foreign key reference to the users table.';
COMMENT ON COLUMN clients.phone IS 'Client’s contact phone number.';
COMMENT ON COLUMN clients.gender IS 'Client’s gender ("0" = female, "1" = male).';
COMMENT ON COLUMN clients.avatar IS 'URL to the client’s avatar image.';
```



### Table: riders

| Column       | Type        | Constraints            | Description                                    |
| ------------ | ----------- | ---------------------- | ---------------------------------------------- |
| id           | BIGSERIAL   | PRIMARY KEY            | Unique rider identifier                        |
| user_id      | BIGINT      | NOT NULL               | Foreign key reference to the `users` table     |
| phone        | VARCHAR(15) | NOT NULL               | Rider’s contact phone number                   |
| gender       | VARCHAR(6)  | NOT NULL               | Rider’s gender (`0` = female, `1` = male)      |
| avatar       | TEXT        |                        | URL to the rider’s avatar                      |
| rider_status | SMALLINT    | NOT NULL DEFAULT 0     | Whether the rider is available (0 = inactive, 1 = active) |

**Indexes:**  
- **PRIMARY KEY** on `id`
- **INDEX** `idx_riders_user_id` on `user_id` 

```sql
CREATE TABLE riders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    phone VARCHAR(15) NOT NULL,
    gender VARCHAR(6) NOT NULL,
    avatar TEXT,
    rider_status SMALLINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_riders_user_id ON riders(user_id);

COMMENT ON TABLE riders IS 'Stores profile details for rider users.';
COMMENT ON COLUMN riders.id IS 'Unique rider identifier.';
COMMENT ON COLUMN riders.user_id IS 'Foreign key reference to the users table.';
COMMENT ON COLUMN riders.phone IS 'Rider’s contact phone number.';
COMMENT ON COLUMN riders.gender IS 'Rider’s gender ("0" = female, "1" = male).';
COMMENT ON COLUMN riders.avatar IS 'URL to the rider’s avatar image.';
COMMENT ON COLUMN riders.status IS 'Rider status ("0" = inactive, "1" = active).';
```