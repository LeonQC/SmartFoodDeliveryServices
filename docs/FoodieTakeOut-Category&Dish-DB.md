# FoodieTakeOut Database Schema

This document outlines the database schema for the FoodieTakeOut service, which includes tables for account module, category&dish module, order module and map module.

## Category&Dish Module Tables
### Table: categories

| Column       | Type                        | Constraints            | Description                                     |
|--------------|-----------------------------|------------------------|-------------------------------------------------|
| id           | SERIAL                      | PRIMARY KEY            | Unique category identifier (auto-increment)     |
| merchant_id  | BIGINT                      | NOT NULL               | Foreign key reference to the `merchants` table  |
| name         | varchar(32)                 | NOT NULL UNIQUE        | Category name                                   |
| sort         | integer                     | NOT NULL               | Sort order for category records                 |
| status       | smallint                    | NOT NULL DEFAULT 1     | Status (1 = enabled; 0 = disabled)              |
| create_time  | timestamp(0) without time zone | NOT NULL DEFAULT NOW() | Creation timestamp                              |
| update_time  | timestamp(0) without time zone | NOT NULL DEFAULT NOW() | Last modification timestamp                     |

**Indexes:**  
- **PRIMARY KEY** on `id`
- **INDEX** `idx_categories_merchant_id ` on `merchant_id` 

**SQL for creating the users table:**
```sql
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    merchant_id BIGINT NOT NULL REFERENCES merchants(id),
    name varchar(32) NOT NULL UNIQUE,
    sort integer NOT NULL,
    status smallint NOT NULL DEFAULT 1,
    create_time timestamp(0) without time zone NOT NULL DEFAULT NOW(),
    update_time timestamp(0) without time zone NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_categories_merchant_id ON categories (merchant_id);

COMMENT ON TABLE categories IS 'Stores categories for merchants.';
COMMENT ON COLUMN categories.id IS 'Unique category identifier (auto-increment).';
COMMENT ON COLUMN categories.merchant_id IS 'Foreign key reference to the `merchants` table.';
COMMENT ON COLUMN categories.name IS 'Category name.';
COMMENT ON COLUMN categories.sort IS 'Sort order for category records.';
COMMENT ON COLUMN categories.status IS 'Status (1 = enabled; 0 = disabled).';
COMMENT ON COLUMN categories.create_time IS 'Creation timestamp.';
COMMENT ON COLUMN categories.update_time IS 'Last modification timestamp.';

```

### Table: dishes

| Column       | Type                         | Constraints            | Description                                     |
|--------------|------------------------------|------------------------|-------------------------------------------------|
| id           | SERIAL                       | PRIMARY KEY            | Unique dish identifier (auto-increment)         |
| name         | VARCHAR(32)                  | NOT NULL UNIQUE        | Dish name                                       |
| category_id  | INTEGER                      | NOT NULL               | Foreign key reference to the `categories` table |
| price        | NUMERIC(10,2)                | NOT NULL               | Dish price                                      |
| image        | VARCHAR(255)                 |                        | Path or URL of the dish image                   |
| description  | VARCHAR(255)                 |                        | Description of the dish                         |
| status       | SMALLINT                     | NOT NULL               | Sale status (1 = on sale; 0 = off sale)         |
| create_time  | TIMESTAMP WITHOUT TIME ZONE  | NOT NULL DEFAULT NOW() | Creation timestamp                              |
| update_time  | TIMESTAMP WITHOUT TIME ZONE  | NOT NULL DEFAULT NOW() | Last update timestamp                           |

**Indexes:**  
- **PRIMARY KEY** on `id`
- **INDEX** `idx_dishes_category_id` on `category_id`

**SQL for creating the users table:**
```sql
CREATE TABLE dishes (
    id           SERIAL                       PRIMARY KEY,
    name         VARCHAR(32)                  NOT NULL UNIQUE,
    category_id  INTEGER                      NOT NULL REFERENCES categories(id),
    price        NUMERIC(10,2)                NOT NULL,
    image        VARCHAR(255),
    description  VARCHAR(255),
    status       SMALLINT                     NOT NULL,
    create_time  TIMESTAMP(0) WITHOUT TIME ZONE  NOT NULL DEFAULT NOW(),
    update_time  TIMESTAMP(0) WITHOUT TIME ZONE  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_dishes_category_id ON dishes (category_id);

COMMENT ON TABLE dishes IS 'Stores dish entries for each merchant category.';
COMMENT ON COLUMN dishes.id IS 'Unique dish identifier (auto-increment).';
COMMENT ON COLUMN dishes.name IS 'Dish name.';
COMMENT ON COLUMN dishes.category_id IS 'Foreign key reference to the `categories` table.';
COMMENT ON COLUMN dishes.price IS 'Dish price.';
COMMENT ON COLUMN dishes.image IS 'Path or URL of the dish image.';
COMMENT ON COLUMN dishes.description IS 'Description of the dish.';
COMMENT ON COLUMN dishes.status IS 'Sale status (1 = on sale; 0 = off sale).';
COMMENT ON COLUMN dishes.create_time IS 'Creation timestamp.';
COMMENT ON COLUMN dishes.update_time IS 'Last update timestamp.';
COMMENT ON INDEX idx_dishes_category_id IS 'Index to speed up lookups by category_id.';

```

