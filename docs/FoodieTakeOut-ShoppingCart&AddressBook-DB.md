# FoodieTakeOut Database Schema

This document outlines the database schema for the shopping cart module and address book module, which includes cart_items table and client_addresses table.

## Database Tables

## Table: carts

| Field Name   | Data Type                   | Constraints                        | Description                                  |
| ------------ | --------------------------- | ---------------------------------- | -------------------------------------------- |
| `id`         | BIGSERIAL                   | PRIMARY KEY                        | Unique identifier for each cart item         |
| `client_id`  | BIGINT                      | NOT NULL, REFERENCES `clients(id)` | The client who owns this cart item           |
| `dish_id`    | BIGINT                      | NOT NULL, REFERENCES `dishes(id)`  | The dish being added                         |
| `dish_name`  | VARCHAR(100)                | NOT NULL                           | Snapshot of the dish name at time of adding  |
| `unit_price` | NUMERIC(10,2)               | NOT NULL                           | Snapshot of the dish price at time of adding |
| `quantity`   | INTEGER                     | NOT NULL                           | Quantity the client intends to order         |
| `create_time`| TIMESTAMP(0) WITHOUT TIME ZONE | NOT NULL DEFAULT NOW()          | When this item was first added               |
| `update_time`| TIMESTAMP(0) WITHOUT TIME ZONE | NOT NULL DEFAULT NOW()          | Last time quantity or remark was updated     |

**Indexes:**
- **PRIMARY KEY** on `id`
- **INDEX** `idx_cart_items_client_id` on `client_id`;
- **UNIQUE** `uq_cart_item_client_dish` on `client_id, dish_id`;

**SQL for creating the cart_items table:**
```sql
-- 1. 创建表
CREATE TABLE carts (
  id          BIGSERIAL PRIMARY KEY,
  client_id   BIGINT    NOT NULL REFERENCES clients(id),
  dish_id     BIGINT    NOT NULL REFERENCES dishes(id),
  dish_name   VARCHAR(100) NOT NULL,
  unit_price  NUMERIC(10,2) NOT NULL,
  quantity    INTEGER   NOT NULL,
  create_time TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  update_time TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
-- 2. 索引
CREATE INDEX idx_cart_items_client_id ON carts(client_id);
CREATE UNIQUE INDEX uq_cart_item_client_dish ON carts(client_id, dish_id);
-- 3. 注释
COMMENT ON TABLE carts IS 'Shopping cart items for clients';
COMMENT ON COLUMN carts.id          IS 'Unique identifier for each cart item';
COMMENT ON COLUMN carts.client_id   IS 'Client who owns this cart item';
COMMENT ON COLUMN carts.dish_id     IS 'The dish being added';
COMMENT ON COLUMN carts.dish_name   IS 'Snapshot of dish name when added';
COMMENT ON COLUMN carts.unit_price  IS 'Snapshot of dish price when added';
COMMENT ON COLUMN carts.quantity    IS 'Desired quantity';
COMMENT ON COLUMN carts.create_time IS 'Time item was added';
COMMENT ON COLUMN carts.update_time IS 'Time item was last updated';
```

## Table: client_addresses

| Field Name      | Data Type                   | Constraints                        | Description                              |
| --------------- | --------------------------- | ---------------------------------- | ---------------------------------------- |
| `id`            | BIGSERIAL                   | PRIMARY KEY                        | Unique identifier for each address entry |
| `client_id`     | BIGINT                      | NOT NULL, REFERENCES `clients(id)` | The client who owns this address         |
| `label`         | VARCHAR(50)                 | NOT NULL                           | Address label (e.g., “Home”, “Office”)   |
| `recipient`     | VARCHAR(100)                | NOT NULL                           | Name of person receiving at this address |
| `phone`         | VARCHAR(20)                 | NOT NULL                           | Contact phone number                     |
| `address_line1` | VARCHAR(255)                | NOT NULL                           | Street address / building                |
| `address_line2` | VARCHAR(255)                |                                    | Additional info (suite, floor, etc.)     |
| `city`          | VARCHAR(100)                | NOT NULL                           | City                                     |
| `state`         | VARCHAR(50)                 | NOT NULL                           | State / Province                         |
| `zipcode`       | VARCHAR(20)                 |                                    | Postal code                              |
| `country`       | VARCHAR(50)                 | NOT NULL                           | Country                                  |
| `is_default`    | BOOLEAN                     | NOT NULL DEFAULT FALSE             | Whether this is the default address      |
| `create_time`   | TIMESTAMP(0) WITHOUT TIME ZONE | NOT NULL DEFAULT NOW()          | When this address was added              |
| `update_time`   | TIMESTAMP(0) WITHOUT TIME ZONE | NOT NULL DEFAULT NOW()          | When this address was last updated       |

**Indexes:**
- **PRIMARY KEY** on `id`
- **INDEX** `idx_client_addresses_client_id` on `client_id`;
- **UNIQUE** `uq_client_default_addr` on `client_id` WHERE `is_default`;

**SQL for creating the client_addresses table:**
```sql
-- 1. 创建表
CREATE TABLE client_addresses (
  id             BIGSERIAL PRIMARY KEY,
  client_id      BIGINT    NOT NULL REFERENCES clients(id),
  label          VARCHAR(50)  NOT NULL,
  recipient      VARCHAR(100) NOT NULL,
  phone          VARCHAR(20)  NOT NULL,
  address_line1  VARCHAR(255) NOT NULL,
  address_line2  VARCHAR(255),
  city           VARCHAR(100) NOT NULL,
  state          VARCHAR(50)  NOT NULL,
  zipcode        VARCHAR(20),
  country        VARCHAR(50)  NOT NULL,
  is_default     BOOLEAN     NOT NULL DEFAULT FALSE,
  create_time    TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  update_time    TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
-- 2. 索引
CREATE INDEX idx_client_addresses_client_id ON client_addresses(client_id);
CREATE UNIQUE INDEX uq_client_default_addr ON client_addresses(client_id) WHERE is_default;
-- 3. 注释
COMMENT ON TABLE client_addresses IS 'Client address book entries';
COMMENT ON COLUMN client_addresses.id             IS 'Unique identifier for each address';
COMMENT ON COLUMN client_addresses.client_id      IS 'Client who owns this address';
COMMENT ON COLUMN client_addresses.label          IS 'User-defined label (Home, Office, etc.)';
COMMENT ON COLUMN client_addresses.recipient      IS 'Name of the recipient';
COMMENT ON COLUMN client_addresses.phone          IS 'Contact phone number';
COMMENT ON COLUMN client_addresses.address_line1  IS 'Primary street address';
COMMENT ON COLUMN client_addresses.address_line2  IS 'Secondary address details';
COMMENT ON COLUMN client_addresses.city           IS 'City';
COMMENT ON COLUMN client_addresses.state          IS 'State or province';
COMMENT ON COLUMN client_addresses.zipcode        IS 'Postal code';
COMMENT ON COLUMN client_addresses.country        IS 'Country';
COMMENT ON COLUMN client_addresses.is_default     IS 'Marks default address';
COMMENT ON COLUMN client_addresses.create_time    IS 'Time address was added';
COMMENT ON COLUMN client_addresses.update_time    IS 'Time address was last updated';
```