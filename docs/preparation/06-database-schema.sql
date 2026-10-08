CREATE DATABASE "clothingStore";

CREATE TYPE product_category_enum AS ENUM ('BOTTOMS','TOPS','ACCESSORIES');
CREATE TYPE order_status_enum AS ENUM ('PENDING','CONFIRMED','CANCELLED');
CREATE TYPE product_size_enum AS ENUM ('S','M','L','XL');
CREATE TYPE sale_status_enum AS ENUM ('ACTIVE','INACTIVE');

CREATE TABLE admin(
                      email VARCHAR(255) PRIMARY KEY,
                      password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE products(
                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         name VARCHAR(250) UNIQUE NOT NULL,
                         description VARCHAR(255) NOT NULL,
                         category product_category_enum NOT NULL,
                         price DECIMAL(19,2) NOT NULL CHECK (price >= 0),
                         sale_status sale_status_enum DEFAULT 'INACTIVE'
);

CREATE TABLE product_variants(
                                 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                                 product_id BIGINT NOT NULL,
                                 size product_size_enum NOT NULL,
                                 quantity INTEGER NOT NULL CHECK (quantity >= 0),
                                 image_url VARCHAR(250) NOT NULL,
                                 color VARCHAR(50) NOT NULL
);

ALTER TABLE product_variants
    ADD CONSTRAINT fk_variant_product
        FOREIGN KEY (product_id) REFERENCES products(id);

CREATE TABLE carts(
                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                      guest_id VARCHAR(255) NOT NULL
);

CREATE TABLE cart_items(
                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                           cart_id BIGINT NOT NULL,
                           variant_id BIGINT NOT NULL,
                           quantity INTEGER NOT NULL CHECK ( quantity >= 1 )
);

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_item_cart
        FOREIGN KEY (cart_id) REFERENCES carts(id);

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_item_variant
        FOREIGN KEY (variant_id) REFERENCES product_variants(id);

CREATE TABLE orders(
                       order_code VARCHAR(250) PRIMARY KEY ,
                       customer_name VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(50) NOT NULL,
                       email VARCHAR(250),
                       delivery_address VARCHAR(255) NOT NULL,
                       total_price DECIMAL(19,2) NOT NULL CHECK (total_price >= 0),
                       status order_status_enum DEFAULT 'PENDING',
                       created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE order_items(
                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                            variant_id BIGINT NOT NULL,
                            order_code VARCHAR(250) NOT NULL,
                            product_name VARCHAR(250) NOT NULL,
                            size product_size_enum NOT NULL,
                            quantity INTEGER NOT NULL CHECK (quantity >= 1),
                            color VARCHAR(50) NOT NULL,
                            unit_price DECIMAL(19,2) NOT NULL CHECK (unit_price >= 0)
);

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_item_variant
        FOREIGN KEY (variant_id) REFERENCES product_variants(id);

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_code) REFERENCES orders(order_code);

/* UNIQUE giữa variantId và orderCode */
ALTER TABLE order_items
    ADD CONSTRAINT uq_order_item_variant
        UNIQUE (variant_id,order_code);

