CREATE TABLE IF NOT EXISTS accounts
(
    id               VARCHAR(255) PRIMARY KEY,
    customer_id      VARCHAR(255)                NOT NULL,
    country          VARCHAR(255)                NOT NULL,
    create_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS balances
(
    id               VARCHAR(255) PRIMARY KEY,
    account_id       VARCHAR(255)                                       NOT NULL,
    available_amount NUMERIC(19, 2)                                     NOT NULL,
    currency         VARCHAR(3)                                         NOT NULL,
    create_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT account_fk FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS transactions
(
    id               VARCHAR(255) PRIMARY KEY,
    account_id       VARCHAR(255)                NOT NULL,
    amount           NUMERIC(19, 2)              NOT NULL,
    currency         VARCHAR(3)                  NOT NULL,
    direction        VARCHAR(3)                  NOT NULL CHECK (direction IN ('IN', 'OUT')),
    description      VARCHAR(255),
    transaction_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT account_fk FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE SET NULL
);
