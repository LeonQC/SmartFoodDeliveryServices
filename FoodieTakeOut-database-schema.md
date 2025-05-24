# FoodieTakeOut Database Schema

This document outlines the database schema for the FoodieTakeOut service, which includes tables for merchant module, order module, comsumer module and rider module.

## Database Tables

### Table: shop

| Column             | Type              | Constraints     | Description                                                      |
|--------------------|-------------------|-----------------|------------------------------------------------------------------|
| id                 | BIGSERIAL         | PRIMARY KEY     | Unique shop identifier                                           |
| email              | TEXT              | NOT NULL        | Merchant’s email address                                         |
| phone              | VARCHAR(15)       | NOT NULL        | Merchant’s contact phone number (e.g., `"217-555-1234"`)         |
| address            | TEXT              | NOT NULL        | Street address of the shop (e.g., `"106 Healey St"`)             |
| city               | TEXT              | NOT NULL        | City where the shop is located (e.g., `"Champaign"`)             |
| state              | VARCHAR(10)       | NOT NULL        | State or province code (e.g., `"IL"`)                            |
| zipcode            | VARCHAR(20)       | NOT NULL        | Postal code (e.g., `"61820"`)                                    |
| country            | VARCHAR(50)       | NOT NULL        | Country name or code (e.g., `"USA"`)                             |
| x                  | DOUBLE PRECISION  | NOT NULL        | Longitude coordinate                                             |
| y                  | DOUBLE PRECISION  | NOT NULL        | Latitude coordinate                                              |
| shop_name          | VARCHAR(100)      | NOT NULL        | Name of the shop (e.g., `"Chengdu Taste"`)                       |
| shop_description   | TEXT              |                 | Brief description of the shop                                    |
| shop_image         | TEXT              |                 | URL to the shop’s main image or logo                             |
| shop_type          | TEXT              |                 | Type of cuisine or category (e.g., `"Chinese Sichuan Food"`)     |
| shop_social_media  | TEXT              |                 | Comma-separated social media handles (e.g., Instagram, Facebook) |
| shop_opening_hours | JSONB             | NOT NULL        | Mapping of each weekday to open/close hours                      |

**Indexes:**  
- **PRIMARY KEY** on `id`

**SQL for creating the table (PostgreSQL):**
```sql
CREATE TABLE IF NOT EXISTS shop (
  id                  BIGSERIAL        PRIMARY KEY,
  email               TEXT             NOT NULL,
  phone               VARCHAR(15)      NOT NULL,
  address             TEXT             NOT NULL,
  city                TEXT             NOT NULL,
  state               VARCHAR(10)      NOT NULL,
  zipcode             VARCHAR(20)      NOT NULL,
  country             VARCHAR(50)      NOT NULL,
  x                   DOUBLE PRECISION NOT NULL,
  y                   DOUBLE PRECISION NOT NULL,
  shop_name           VARCHAR(100)     NOT NULL,
  shop_description    TEXT,
  shop_image          TEXT,
  shop_type           TEXT,
  shop_social_media   TEXT,
  shop_opening_hours  JSONB            NOT NULL
);

COMMENT ON TABLE shop IS 'Shop information';
COMMENT ON COLUMN shop.id IS 'Unique shop identifier';
COMMENT ON COLUMN shop.email IS 'Merchant’s email address';
COMMENT ON COLUMN shop.phone IS 'Merchant’s contact phone number';
COMMENT ON COLUMN shop.address IS 'Street address of the shop';
COMMENT ON COLUMN shop.city IS 'City where the shop is located';
COMMENT ON COLUMN shop.state IS 'State or province code';
COMMENT ON COLUMN shop.zipcode IS 'Postal code';
COMMENT ON COLUMN shop.country IS 'Country name or code';
COMMENT ON COLUMN shop.x IS 'Longitude coordinate';
COMMENT ON COLUMN shop.y IS 'Latitude coordinate';
COMMENT ON COLUMN shop.shop_name IS 'Name of the shop';
COMMENT ON COLUMN shop.shop_description IS 'Brief description of the shop';
COMMENT ON COLUMN shop.shop_image IS 'URL to the shop’s main image or logo';
COMMENT ON COLUMN shop.shop_type IS 'Type of cuisine or category';
COMMENT ON COLUMN shop.shop_social_media IS 'Comma-separated social media handles';
COMMENT ON COLUMN shop.shop_opening_hours IS 'Mapping of weekdays to open/close hours';
```

### Table: category

| Column       | Type         | Constraints                                | Description                                              |
|--------------|--------------|--------------------------------------------|----------------------------------------------------------|
| id           | BIGSERIAL    | PRIMARY KEY                                | Unique identifier for each category                      |
| type         | INT          |                                            | Category type (1 = dish category; 2 = set-meal category) |
| name         | VARCHAR(32)  | NOT NULL, UNIQUE                           | Category name                                            |
| sort         | INT          | NOT NULL, DEFAULT 0                        | Display order                                            |
| create_time  | TIMESTAMPTZ  |                                            | Timestamp when the record was created                    |
| update_time  | TIMESTAMPTZ  |                                            | Timestamp when the record was last updated               |

**Indexes:**
- **PRIMARY KEY** on `id`  
- **UNIQUE INDEX** `idx_category_name` on `name`  

**SQL for creating the table (comments added afterwards):**

```sql
CREATE TABLE IF NOT EXISTS category (
  id            BIGSERIAL      PRIMARY KEY,
  type          INT,
  name          VARCHAR(32)    NOT NULL,
  sort          INT            NOT NULL DEFAULT 0,
  create_time   TIMESTAMPTZ,
  update_time   TIMESTAMPTZ
);

CREATE UNIQUE INDEX idx_category_name ON category(name);

-- add descriptive comments
COMMENT ON TABLE category IS 'Dish and combo-meal categories';
COMMENT ON COLUMN category.id IS 'Primary key';
COMMENT ON COLUMN category.type IS 'Category type: 1=dish; 2=combo-meal';
COMMENT ON COLUMN category.name IS 'Category name';
COMMENT ON COLUMN category.sort IS 'Display order';
COMMENT ON COLUMN category.create_time IS 'Creation timestamp';
COMMENT ON COLUMN category.update_time IS 'Update timestamp';
```

### Table: dish

| Column        | Type          | Constraints        | Description                    |
|---------------|---------------|--------------------|--------------------------------|
| id            | BIGSERIAL     | PRIMARY KEY        | Unique dish identifier         |
| name          | VARCHAR(32)   | NOT NULL, UNIQUE   | Dish name                      |
| category_id   | BIGINT        | NOT NULL           | Category ID                    |
| price         | DECIMAL(10,2) | NOT NULL           | Dish price                     |
| image         | VARCHAR(255)  |                    | Image URL                      |
| description   | VARCHAR(255)  |                    | Description                    |
| status        | INT           | DEFAULT 1          | Availability (0 = off; 1 = on) |
| create_time   | TIMESTAMPTZ   |                    | Creation timestamp             |
| update_time   | TIMESTAMPTZ   |                    | Update timestamp               |

**Indexes:**
- **PRIMARY KEY** on `id`
- **UNIQUE INDEX** `idx_dish_name` on `name`

**SQL for creating the table (PostgreSQL):**

```sql
CREATE TABLE IF NOT EXISTS dish (
  id            BIGSERIAL     PRIMARY KEY,
  name          VARCHAR(32)   NOT NULL,
  category_id   BIGINT        NOT NULL,
  price         DECIMAL(10,2) NOT NULL,
  image         VARCHAR(255),
  description   VARCHAR(255),
  status        INT           DEFAULT 1,
  create_time   TIMESTAMPTZ,
  update_time   TIMESTAMPTZ
);

CREATE UNIQUE INDEX idx_dish_name ON dish(name);

COMMENT ON TABLE dish IS 'Dishes';
COMMENT ON COLUMN dish.id IS 'Primary key';
COMMENT ON COLUMN dish.name IS 'Dish name';
COMMENT ON COLUMN dish.category_id IS 'Category ID';
COMMENT ON COLUMN dish.price IS 'Dish price';
COMMENT ON COLUMN dish.image IS 'Image URL';
COMMENT ON COLUMN dish.description IS 'Description';
COMMENT ON COLUMN dish.status IS 'Availability status: 0 = off; 1 = on';
COMMENT ON COLUMN dish.create_time IS 'Creation timestamp';
COMMENT ON COLUMN dish.update_time IS 'Update timestamp';
```

### Table: employee

| Column        | Type          | Constraints              | Description                                                                 |
|---------------|---------------|--------------------------|-----------------------------------------------------------------------------|
| id            | BIGSERIAL     | PRIMARY KEY              | Unique employee identifier                                                  |
| name          | VARCHAR(32)   | NOT NULL                 | Employee’s full name                                                        |
| phone         | VARCHAR(11)   | NOT NULL                 | Phone number                                                                |
| gender        | VARCHAR(2)    | NOT NULL                 | Gender (1 = male; 2 = female)                                               |
| image         | VARCHAR(255)  |                          | Employee’s avatar image URL                                                 |
| job           | INT           |                          | Position (1=Manager; 2=Cook; 3=Waiter/Waitress; 4=Food Runner; 5=Dishwasher; 6=Customer Service) |
| salary        | INT           |                          | Salary                                                                      |
| shop_id       | INT           |                          | Shop ID (foreign key to `shop.id`)                                          |
| entry_time    | TIMESTAMPTZ   |                          | Entry time                                                                  |
| create_time   | TIMESTAMPTZ   |                          | Record creation timestamp                                                   |
| update_time   | TIMESTAMPTZ   |                          | Record last update timestamp                                                |

**Indexes:**  
- **PRIMARY KEY** on `id`  
- **INDEX** `idx_employee_shop_id` on `shop_id`  

**SQL for creating the table (PostgreSQL):**

```sql
CREATE TABLE IF NOT EXISTS employee (
  id            BIGSERIAL     PRIMARY KEY,
  name          VARCHAR(32)   NOT NULL,
  phone         VARCHAR(11)   NOT NULL,
  gender        VARCHAR(2)    NOT NULL,
  image         VARCHAR(255),
  job           INT,
  salary        INT,
  shop_id       INT,
  entry_time    TIMESTAMPTZ,
  create_time   TIMESTAMPTZ,
  update_time   TIMESTAMPTZ,
  CONSTRAINT fk_employee_shop FOREIGN KEY (shop_id) REFERENCES shop(id)
);

CREATE INDEX idx_employee_shop_id ON employee(shop_id);

COMMENT ON TABLE employee IS 'Employee information';
COMMENT ON COLUMN employee.id IS 'Primary key';
COMMENT ON COLUMN employee.name IS 'Employee’s full name';
COMMENT ON COLUMN employee.phone IS 'Phone number';
COMMENT ON COLUMN employee.gender IS 'Gender (1 = male; 2 = female)';
COMMENT ON COLUMN employee.image IS 'Employee’s avatar image URL';
COMMENT ON COLUMN employee.job IS 'Position (1=Manager; 2=Cook; 3=Waiter/Waitress; 4=Food Runner; 5=Dishwasher; 6=Customer Service)';
COMMENT ON COLUMN employee.salary IS 'Salary';
COMMENT ON COLUMN employee.shop_id IS 'Shop ID (foreign key to shop.id)';
COMMENT ON COLUMN employee.entry_time IS 'Entry time';
COMMENT ON COLUMN employee.create_time IS 'Record creation timestamp';
COMMENT ON COLUMN employee.update_time IS 'Record last update timestamp';
```

### Table: combomeal

| Column        | Type           | Constraints              | Description                                                         |
|---------------|----------------|--------------------------|---------------------------------------------------------------------|
| id            | BIGSERIAL      | PRIMARY KEY              | Primary key                                                         |
| category_id   | BIGINT         | NOT NULL                 | Category ID (foreign key to `category.id`)                          |
| name          | VARCHAR(32)    | NOT NULL, UNIQUE         | Combo meal name                                                     |
| price         | DECIMAL(10,2)  | NOT NULL                 | Combo meal price                                                    |
| status        | INT            | DEFAULT 1                | Sale status (0 = unavaliable; 1 = avaliable)                        |
| description   | VARCHAR(255)   |                          | Description                                                         |
| image         | VARCHAR(255)   |                          | Image URL                                                           |
| create_time   | TIMESTAMPTZ    |                          | Record creation timestamp                                           |
| update_time   | TIMESTAMPTZ    |                          | Record last update timestamp                                        |

**Indexes:**  
- **PRIMARY KEY** on `id`  
- **UNIQUE INDEX** `idx_combomeal_name` on `name`  
- **INDEX** `idx_combomeal_category_id` on `category_id`  

**SQL for creating the table (PostgreSQL):**

```sql
CREATE TABLE IF NOT EXISTS combomeal (
  id            BIGSERIAL      PRIMARY KEY,
  category_id   BIGINT         NOT NULL,
  name          VARCHAR(32)    NOT NULL,
  price         DECIMAL(10,2)  NOT NULL,
  status        INT            DEFAULT 1,
  description   VARCHAR(255),
  image         VARCHAR(255),
  create_time   TIMESTAMPTZ,
  update_time   TIMESTAMPTZ,
  CONSTRAINT fk_combomeal_category FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE UNIQUE INDEX idx_combomeal_name ON combomeal(name);
CREATE INDEX idx_combomeal_category_id ON combomeal(category_id);

COMMENT ON TABLE combomeal IS 'Combo meals';
COMMENT ON COLUMN combomeal.id IS 'Primary key';
COMMENT ON COLUMN combomeal.category_id IS 'Category ID (foreign key to category.id)';
COMMENT ON COLUMN combomeal.name IS 'Combo meal name';
COMMENT ON COLUMN combomeal.price IS 'Combo meal price';
COMMENT ON COLUMN combomeal.status IS 'Sale status: 0 = not for sale; 1 = for sale';
COMMENT ON COLUMN combomeal.description IS 'Description';
COMMENT ON COLUMN combomeal.image IS 'Image URL';
COMMENT ON COLUMN combomeal.create_time IS 'Record creation timestamp';
COMMENT ON COLUMN combomeal.update_time IS 'Record last update timestamp';
```

### Table: combomeal_dishes

| Column         | Type           | Constraints                       | Description                                  |
|----------------|----------------|-----------------------------------|----------------------------------------------|
| id             | BIGSERIAL      | PRIMARY KEY                       | Unique identifier                            |
| combomeal_id   | BIGINT         |                                   | Combo meal ID (foreign key to combomeal.id)  |
| dish_id        | BIGINT         |                                   | Dish ID (foreign key to dish.id)             |
| name           | VARCHAR(32)    |                                   | Dish name (redundant)                        |
| price          | DECIMAL(10,2)  |                                   | Dish unit price (redundant)                  |
| copies         | INT            |                                   | Number of dish copies                        |

**Indexes:**  
- **PRIMARY KEY** on `id`  
- **INDEX** `idx_combomeal_dish_combomeal_id` on `combomeal_id`  
- **INDEX** `idx_combomeal_dish_dish_id` on `dish_id`  

**SQL for creating the table (PostgreSQL):**

```sql
CREATE TABLE IF NOT EXISTS combomeal_dishes (
  id             BIGSERIAL      PRIMARY KEY,
  combomeal_id   BIGINT,
  dish_id        BIGINT,
  name           VARCHAR(32),
  price          DECIMAL(10,2),
  copies         INT,
  CONSTRAINT fk_cmd_combomeal FOREIGN KEY (combomeal_id) REFERENCES combomeal(id),
  CONSTRAINT fk_cmd_dish      FOREIGN KEY (dish_id)      REFERENCES dish(id)
);

CREATE INDEX idx_combomeal_dish_combomeal_id ON combomeal_dish(combomeal_id);
CREATE INDEX idx_combomeal_dish_dish_id      ON combomeal_dish(dish_id);

COMMENT ON TABLE combomeal_dish IS 'Combo meal–dish relationships';
COMMENT ON COLUMN combomeal_dish.id           IS 'Primary key';
COMMENT ON COLUMN combomeal_dish.combomeal_id IS 'Combo meal ID (foreign key to combomeal.id)';
COMMENT ON COLUMN combomeal_dish.dish_id      IS 'Dish ID (foreign key to dish.id)';
COMMENT ON COLUMN combomeal_dish.name         IS 'Dish name (redundant)';
COMMENT ON COLUMN combomeal_dish.price        IS 'Dish unit price (redundant)';
COMMENT ON COLUMN combomeal_dish.copies       IS 'Number of dish copies';
```