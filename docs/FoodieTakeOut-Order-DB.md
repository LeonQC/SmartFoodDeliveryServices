# FoodieTakeOut Database Schema

This document outlines the database schema for the order module, which includes orders table, order_items table, order_status_log table and rider_assignments table .

## Database Tables

## Table: orders

| Field Name       | Data Type                   | Constraints                          | Description                                                                           |
| ---------------- | --------------------------- | ------------------------------------ | ------------------------------------------------------------------------------------- |
| `id`             | BIGSERIAL                   | PRIMARY KEY                          | Unique order identifier                                                               |
| `client_id`      | BIGINT                      | NOT NULL, REFERENCES `clients(id)`   | The customer who placed the order                                                     |
| `merchant_id`    | BIGINT                      | NOT NULL, REFERENCES `merchants(id)` | The merchant accepting/preparing the order                                            |
| `rider_id`       | BIGINT                      | NULL, REFERENCES `riders(id)`        | The delivery rider (filled in once the order is accepted)                             |
| `status`         | SMALLINT                    | NOT NULL                             | Order status (e.g. 0=pending, 1= paid, 2=accepted, 3=Ready to go, 4=picking up by rider, 5=out of delivery, 6=delivered (completed), 7=canceled) |
| `total_amount`   | NUMERIC(10,2)               | NOT NULL                             | Total amount of the order                                                             |
| `delivery_fee`   | NUMERIC(10,2)               | NOT NULL                             | Delivery fee                                                                          |
| `payment_method` | VARCHAR(20)                 | NOT NULL                             | Payment method (e.g. ApplePay, credit card, balance)                                  |
| `paid_at`        | TIMESTAMP WITHOUT TIME ZONE | NULL                                 | Time when payment was made                                                            |
| `address_id`     | INTEGER                     | NOT NULL, REFERENCES `client_addresses(id)` | Foreign key reference to the customer's shipping address                        |
| `create_time`    | TIMESTAMP WITHOUT TIME ZONE | NOT NULL DEFAULT NOW()               | Order creation timestamp                                                              |
| `update_time`    | TIMESTAMP WITHOUT TIME ZONE | NOT NULL DEFAULT NOW()               | Last update timestamp                                                                 |

**Indexes:**  
- **PRIMARY KEY** on `id`
- **INDEX** `idx_orders_client_id`   on `client_id`;
- **INDEX** `idx_orders_merchant_id` on `merchant_id`;
- **INDEX** `idx_orders_rider_id`    on `rider_id`;
- **INDEX** `idx_orders_status`      on `status`;
- **INDEX** `idx_orders_address_id`  on `address_id`;

**SQL for creating the orders table:**
```sql
-- 1. 建表
CREATE TABLE orders (
  id              BIGSERIAL PRIMARY KEY,
  client_id       BIGINT       NOT NULL REFERENCES clients(id),
  merchant_id     BIGINT       NOT NULL REFERENCES merchants(id),
  rider_id        BIGINT       REFERENCES riders(id),
  status          SMALLINT     NOT NULL,
  total_amount    NUMERIC(10,2) NOT NULL,
  delivery_fee    NUMERIC(10,2) NOT NULL,
  payment_method  VARCHAR(20)  NOT NULL,
  paid_at         TIMESTAMP WITHOUT TIME ZONE,
  address_id      BIGINT        NOT NULL REFERENCES client_addresses(id),
  create_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  update_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- 2. 索引
CREATE INDEX idx_orders_client_id   ON orders(client_id);
CREATE INDEX idx_orders_merchant_id ON orders(merchant_id);
CREATE INDEX idx_orders_rider_id    ON orders(rider_id);
CREATE INDEX idx_orders_status      ON orders(status);
CREATE INDEX idx_orders_address_id  ON orders(address_id);

-- 3. 表注释
COMMENT ON TABLE orders IS 'Orders table representing customer orders';

-- 4. 字段注释
COMMENT ON COLUMN orders.id             IS 'Unique order identifier';
COMMENT ON COLUMN orders.client_id      IS 'The customer who placed the order';
COMMENT ON COLUMN orders.merchant_id    IS 'The merchant accepting/preparing the order';
COMMENT ON COLUMN orders.rider_id       IS 'The delivery rider (filled in once the order is accepted)';
COMMENT ON COLUMN orders.status         IS 'Order status (e.g. 0=pending, 1= paid, 2=accepted (preparing), 3=Ready to go, 4=picking up by rider, 5=out of delivery, 6=delivered (completed), 7=canceled)';
COMMENT ON COLUMN orders.total_amount   IS 'Total amount of the order';
COMMENT ON COLUMN orders.delivery_fee   IS 'Delivery fee';
COMMENT ON COLUMN orders.payment_method IS 'Payment method (e.g. WeChat, Alipay, balance)';
COMMENT ON COLUMN orders.paid_at        IS 'Time when payment was made';
COMMENT ON COLUMN orders.delivery_addr  IS 'Delivery address the customer used for this order';
COMMENT ON COLUMN orders.create_time    IS 'Order creation timestamp';
COMMENT ON COLUMN orders.update_time    IS 'Last update timestamp';
```

### Table: order_items

| Field Name   | Data Type                   | Constraints                       | Description                                           |
| ------------ | --------------------------- | --------------------------------- | ----------------------------------------------------- |
| `id`         | BIGSERIAL                   | PRIMARY KEY                       | Unique detail record identifier                       |
| `order_id`   | BIGINT                      | NOT NULL, REFERENCES `orders(id)` | References the parent order                           |
| `dish_id`    | BIGINT                      | NOT NULL, REFERENCES `dishes(id)` | References the dish                                   |
| `dish_name`  | VARCHAR(100)                | NOT NULL                          | Name of the dish at the time of ordering              |
| `unit_price` | NUMERIC(10,2)               | NOT NULL                          | Unit price                                            |
| `quantity`   | INTEGER                     | NOT NULL                          | Quantity                                              |
| `subtotal`   | NUMERIC(10,2)               | NOT NULL                          | Subtotal (calculated as `unit_price * quantity`)      |
| `remark`     | VARCHAR(255)                |                                   | Remark for the dish (e.g., extra spice, no scallions) |
| `create_time`| TIMESTAMP WITHOUT TIME ZONE | NOT NULL DEFAULT NOW()            | Entry timestamp                                       |

**Indexes:**  
- **PRIMARY KEY** on `id`
- **INDEX** `idx_order_items_order_id`   on `order_id`;
- **INDEX** `idx_order_items_dish_id  ` on `dish_id`;

**SQL for creating the order_items table:**
```sql
-- 1. 创建表
CREATE TABLE order_items (
  id          BIGSERIAL PRIMARY KEY,
  order_id    BIGINT    NOT NULL REFERENCES orders(id),
  dish_id     BIGINT    NOT NULL REFERENCES dishes(id),
  dish_name   VARCHAR(100) NOT NULL,
  unit_price  NUMERIC(10,2) NOT NULL,
  quantity    INTEGER   NOT NULL,
  subtotal    NUMERIC(10,2) NOT NULL,
  remark      VARCHAR(255),
  create_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- 2. 索引
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_dish_id  ON order_items(dish_id);

-- 3. 表注释
COMMENT ON TABLE order_items IS 'Order items detail table linking orders and dishes';

-- 4. 字段注释
COMMENT ON COLUMN order_items.id          IS 'Unique detail record identifier';
COMMENT ON COLUMN order_items.order_id    IS 'References the parent order';
COMMENT ON COLUMN order_items.dish_id     IS 'References the dish';
COMMENT ON COLUMN order_items.dish_name   IS 'Name of the dish at the time of ordering';
COMMENT ON COLUMN order_items.unit_price  IS 'Unit price';
COMMENT ON COLUMN order_items.quantity    IS 'Quantity';
COMMENT ON COLUMN order_items.subtotal    IS 'Subtotal (unit_price * quantity)';
COMMENT ON COLUMN order_items.remark      IS 'Remark for the dish (e.g., extra spice, no scallions)';
COMMENT ON COLUMN order_items.create_time IS 'Entry timestamp';
```

### Table: order_status_log

| Field Name    | Data Type                   | Constraints                       | Description                                                    |
| ------------- | --------------------------- | --------------------------------- | -------------------------------------------------------------- |
| `id`          | BIGSERIAL                   | PRIMARY KEY                       | Unique identifier for each status change record                |
| `order_id`    | BIGINT                      | NOT NULL, REFERENCES `orders(id)` | The order to which this status change applies                  |
| `from_status` | SMALLINT                    | NOT NULL                          | The status code before the change                              |
| `to_status`   | SMALLINT                    | NOT NULL                          | The status code after the change                               |
| `changed_by`  | VARCHAR(50)                 | NOT NULL                          | Who made the change (`user`, `merchant`, `rider`, or `system`) |
| `remark`      | VARCHAR(255)                |                                   | Optional remark or note about the status change                |
| `changed_at`  | TIMESTAMP WITHOUT TIME ZONE | NOT NULL DEFAULT NOW()            | Timestamp when the status change occurred                      |

**Indexes:**  
- **PRIMARY KEY** on `id`
- **INDEX** `idx_order_status_log_order_id`   on `order_id` 
- **INDEX** `idx_order_status_log_changed_at` on `changed_at`
- **INDEX** `idx_order_status_log_changed_by` on `changed_by`

**SQL for creating the order_status_log table:**
```sql
-- 1. 创建表
CREATE TABLE order_status_log (
  id           BIGSERIAL                    PRIMARY KEY,
  order_id     BIGINT                       NOT NULL REFERENCES orders(id),
  from_status  SMALLINT                     NOT NULL,
  to_status    SMALLINT                     NOT NULL,
  changed_by   VARCHAR(50)                  NOT NULL,
  remark       VARCHAR(255),
  changed_at   TIMESTAMP WITHOUT TIME ZONE  NOT NULL DEFAULT NOW()
);

-- 2. 索引
CREATE INDEX idx_order_status_log_order_id   ON order_status_log(order_id);
CREATE INDEX idx_order_status_log_changed_at ON order_status_log(changed_at);
CREATE INDEX idx_order_status_log_changed_by ON order_status_log(changed_by);

-- 3. 表注释
COMMENT ON TABLE order_status_log IS 'Order status change history log';

-- 4. 字段注释
COMMENT ON COLUMN order_status_log.log_id       IS 'Unique identifier for each status change record';
COMMENT ON COLUMN order_status_log.order_id     IS 'The order to which this status change applies';
COMMENT ON COLUMN order_status_log.from_status  IS 'The status code before the change';
COMMENT ON COLUMN order_status_log.to_status    IS 'The status code after the change';
COMMENT ON COLUMN order_status_log.changed_by   IS 'Who made the change (user, merchant, rider, or system)';
COMMENT ON COLUMN order_status_log.remark       IS 'Optional remark or note about the status change';
COMMENT ON COLUMN order_status_log.changed_at   IS 'Timestamp when the status change occurred';

```

### Table: rider_assignments

| Field Name   | Data Type                   | Constraints                       | Description                                                    |
| ------------ | --------------------------- | --------------------------------- | -------------------------------------------------------------- |
| `id`         | BIGSERIAL                   | PRIMARY KEY                       | Unique identifier for each assignment attempt                  |
| `order_id`   | BIGINT                      | NOT NULL, REFERENCES `orders(id)` | The order being assigned                                       |
| `rider_id`   | BIGINT                      | NOT NULL, REFERENCES `riders(id)` | The rider who is being offered this order                      |
| `status`     | SMALLINT                    | NOT NULL                          | Assignment result (0 = no response, 1 = accepted, 2 = refused) |
| `attempt_at` | TIMESTAMP WITHOUT TIME ZONE | NOT NULL DEFAULT NOW()            | Timestamp when the assignment was attempted                    |

**Indexes:**  
- **PRIMARY KEY** on `id`
- **INDEX** `idx_rider_assignments_order_id`   on `order_id`
- **INDEX** `idx_rider_assignments_rider_id`   on `rider_id`
- **INDEX** `idx_rider_assignments_status` on `status`

**SQL for creating the rider_assignments table:**
```sql
-- 1. 创建表
CREATE TABLE rider_assignments (
  id          BIGSERIAL                     PRIMARY KEY,
  order_id    BIGINT                        NOT NULL REFERENCES orders(id),
  rider_id    BIGINT                        NOT NULL REFERENCES riders(id),
  status      SMALLINT                      NOT NULL,
  attempt_at  TIMESTAMP WITHOUT TIME ZONE   NOT NULL DEFAULT NOW()
);

-- 2. 索引
CREATE INDEX idx_rider_assignments_order_id ON rider_assignments(order_id);
CREATE INDEX idx_rider_assignments_rider_id ON rider_assignments(rider_id);
CREATE INDEX idx_rider_assignments_status   ON rider_assignments(status);

-- 3. 表注释
COMMENT ON TABLE rider_assignments IS 'Records each attempt to assign an order to a rider';

-- 4. 字段注释
COMMENT ON COLUMN rider_assignments.id         IS 'Unique identifier for each assignment attempt';
COMMENT ON COLUMN rider_assignments.order_id   IS 'The order being assigned';
COMMENT ON COLUMN rider_assignments.rider_id   IS 'The rider who is being offered this order';
COMMENT ON COLUMN rider_assignments.status     IS 'Assignment result (0 = no response, 1 = accepted, 2 = refused)';
COMMENT ON COLUMN rider_assignments.attempt_at IS 'Timestamp when the assignment was attempted';
```

