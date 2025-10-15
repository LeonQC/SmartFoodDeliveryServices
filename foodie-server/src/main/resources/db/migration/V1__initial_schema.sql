-- ----------------------------------- Account Tables ----------------------------------- --
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username TEXT NOT NULL UNIQUE,
                       password TEXT,
                       email TEXT NOT NULL UNIQUE,
                       profile_completed BOOLEAN NOT NULL,
                       role TEXT NOT NULL,
                       create_time TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                       update_time TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
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
CREATE INDEX merchants_location_gist ON merchants USING GIST (location);

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
COMMENT ON COLUMN riders.rider_status IS 'Rider status ("0" = inactive, "1" = active).';


-- ----------------------------------- Category&Dish Tables ----------------------------------- --
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

-- ----------------------------------- Client Exclusive Tables ----------------------------------- --
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
CREATE UNIQUE INDEX uq_client_default_addr ON client_addresses(client_id) WHERE is_default = TRUE;
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

-- ----------------------------------- Order Related Tables ----------------------------------- --
-- 1. 建表
CREATE TABLE orders (
                        id              BIGSERIAL PRIMARY KEY,
                        client_id       BIGINT       NOT NULL REFERENCES clients(id),
                        merchant_id     BIGINT       NOT NULL REFERENCES merchants(id),
                        status          SMALLINT     NOT NULL,
                        total_amount    NUMERIC(10,2) NOT NULL,
                        delivery_fee    NUMERIC(10,2) NOT NULL,
                        payment_method  VARCHAR(20),
                        paid_at         TIMESTAMP(0) WITHOUT TIME ZONE,
                        pay_status      SMALLINT     NOT NULL,
                        estimate_delivery_time TIMESTAMP(0) WITHOUT TIME ZONE,
                        address_id      BIGINT        NOT NULL REFERENCES client_addresses(id),
                        remark          TEXT,
                        create_time     TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                        update_time     TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                        payment_intent_id VARCHAR(64)
);

-- 2. 索引
CREATE INDEX idx_orders_client_id   ON orders(client_id);
CREATE INDEX idx_orders_merchant_id ON orders(merchant_id);
CREATE INDEX idx_orders_status      ON orders(status);
CREATE INDEX idx_orders_address_id  ON orders(address_id);

-- 3. 表注释
COMMENT ON TABLE orders IS 'Orders table representing customer orders';

-- 4. 字段注释
COMMENT ON COLUMN orders.id             IS 'Unique order identifier';
COMMENT ON COLUMN orders.client_id      IS 'The customer who placed the order';
COMMENT ON COLUMN orders.merchant_id    IS 'The merchant accepting/preparing the order';
COMMENT ON COLUMN orders.status         IS 'Order status (e.g. 0=pending, 1= paid, 2=accepted (preparing), 3=Ready to go, 4=picking up by rider, 5=out of delivery, 6=delivered (completed), 7=canceled)';
COMMENT ON COLUMN orders.total_amount   IS 'Total amount of the order';
COMMENT ON COLUMN orders.delivery_fee   IS 'Delivery fee';
COMMENT ON COLUMN orders.payment_method IS 'Payment method (e.g. WeChat, Alipay, balance)';
COMMENT ON COLUMN orders.paid_at        IS 'Time when payment was made';
COMMENT ON COLUMN orders.pay_status     IS 'Payment status (e.g. 0=pending, 1=paid, 2=refund)';
COMMENT ON COLUMN orders.estimate_delivery_time IS 'Estimated time of delivered';
COMMENT ON COLUMN orders.address_id     IS 'Delivery address the customer used for this order';
COMMENT ON COLUMN orders.remark         IS 'Any additional notes or instructions for the order';
COMMENT ON COLUMN orders.create_time    IS 'Order creation timestamp';
COMMENT ON COLUMN orders.update_time    IS 'Last update timestamp';
COMMENT ON COLUMN orders.payment_intent_id IS 'Payment intent ID for the order';

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
                             create_time TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
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


-- 1. 创建表
CREATE TABLE order_status_log (
                                  id           BIGSERIAL                    PRIMARY KEY,
                                  order_id     BIGINT                       NOT NULL REFERENCES orders(id),
                                  from_status  SMALLINT                     ,
                                  to_status    SMALLINT                     NOT NULL,
                                  changed_by   VARCHAR(50)                  NOT NULL,
                                  remark       VARCHAR(255),
                                  changed_at   TIMESTAMP(0) WITHOUT TIME ZONE  NOT NULL DEFAULT NOW()
);

-- 2. 索引
CREATE INDEX idx_order_status_log_order_id   ON order_status_log(order_id);
CREATE INDEX idx_order_status_log_changed_at ON order_status_log(changed_at);
CREATE INDEX idx_order_status_log_changed_by ON order_status_log(changed_by);

-- 3. 表注释
COMMENT ON TABLE order_status_log IS 'Order status change history log';

-- 4. 字段注释
COMMENT ON COLUMN order_status_log.id       IS 'Unique identifier for each status change record';
COMMENT ON COLUMN order_status_log.order_id     IS 'The order to which this status change applies';
COMMENT ON COLUMN order_status_log.from_status  IS 'The status code before the change';
COMMENT ON COLUMN order_status_log.to_status    IS 'The status code after the change';
COMMENT ON COLUMN order_status_log.changed_by   IS 'Who made the change (user, merchant, rider, or system)';
COMMENT ON COLUMN order_status_log.remark       IS 'Optional remark or note about the status change';
COMMENT ON COLUMN order_status_log.changed_at   IS 'Timestamp when the status change occurred';

-- 1. 创建表
CREATE TABLE rider_assignments (
                                   id          BIGSERIAL                     PRIMARY KEY,
                                   order_id    BIGINT                        NOT NULL REFERENCES orders(id),
                                   rider_id    BIGINT                        NOT NULL REFERENCES riders(id),
                                   status      SMALLINT,
                                   attempt_at  TIMESTAMP(0) WITHOUT TIME ZONE   NOT NULL DEFAULT NOW()
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
COMMENT ON COLUMN rider_assignments.status     IS 'Assignment result (1 = refused, 2 = no response, 3 = accepted, NULL = waiting for response)';
COMMENT ON COLUMN rider_assignments.attempt_at IS 'Timestamp when the assignment was attempted';