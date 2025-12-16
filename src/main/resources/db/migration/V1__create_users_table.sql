CREATE TABLE users
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    full_name  VARCHAR(255),
    email      VARCHAR(255),
    phone      VARCHAR(50),
    image_url  TEXT,
    address    VARCHAR(255),
    bio        TEXT,
    created_at TIMESTAMP ,
    updated_at TIMESTAMP
);

