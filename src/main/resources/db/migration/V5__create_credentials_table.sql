CREATE TABLE credentials (
                             id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                             user_id BIGINT,
                             username VARCHAR(255),
                             password TEXT,
                             role VARCHAR(50),         -- ADMIN / OWNER / USER
                             provider VARCHAR(50),     -- LOCAL / GOOGLE / FACEBOOK
                             is_enabled BOOLEAN DEFAULT TRUE,
                             is_account_non_expired BOOLEAN DEFAULT TRUE,
                             is_account_non_locked BOOLEAN DEFAULT TRUE,
                             is_credentials_non_expired BOOLEAN DEFAULT TRUE,
                             created_at TIMESTAMP ,
                             updated_at TIMESTAMP,
                             CONSTRAINT fk_credential_user
                                 FOREIGN KEY (user_id)
                                     REFERENCES users(id)
                                     ON DELETE CASCADE
);