CREATE TABLE verification_tokens
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    credential_id BIGINT REFERENCES credentials (id) ON DELETE CASCADE,
    verif_token   VARCHAR(255),
    expire_date   TIMESTAMP,
    created_at    TIMESTAMP ,
    updated_at    TIMESTAMP,
    CONSTRAINT fk_verification_credential
        FOREIGN KEY (credential_id)
            REFERENCES credentials(id)
            ON DELETE CASCADE
);
