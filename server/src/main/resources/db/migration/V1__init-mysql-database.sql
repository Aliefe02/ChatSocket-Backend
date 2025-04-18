DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id CHAR(36) NOT NULL,
    username VARCHAR(36) NOT NULL UNIQUE,
    email VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    version INT DEFAULT 0,
    jwt_token_code INT DEFAULT 0,
    first_name VARCHAR(36) DEFAULT NULL,
    last_name VARCHAR(36) DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

