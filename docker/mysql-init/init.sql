CREATE TABLE IF NOT EXISTS clients (
    id BINARY(16) NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    address VARCHAR(255),
    phone VARCHAR(255),
    role VARCHAR(50),
    reset_password_token VARCHAR(255),
    reset_password_token_expiry DATETIME
);

-- Inserir usuário ADMIN Rafael Borges
INSERT INTO clients (
    id, name, email, password, address, phone, role
)
SELECT
    UNHEX(REPLACE(UUID(), '-', '')),
    'Rafael Borges',
    'rafaelmascarenhasborges@gmail.com',
    '$2a$10$pTcDnICe9Hie5l7Ame.ICu/rdFRWZ2CZT64mO4ioRuhD1ZnCHL4mm', -- SenhaAdmin1!
    'Rua Estudio',
    '11 970332140',
    'ADMIN'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM clients WHERE email = 'rafaelmascarenhasborges@gmail.com'
);

-- Inserir usuário CLIENT Kevyn Cavalcanti
INSERT INTO clients (
    id, name, email, password, address, phone, role
)
SELECT
    UNHEX(REPLACE(UUID(), '-', '')),
    'Kevyn Cavalcanti',
    'contacontaconta0002@gmail.com',
    '$2a$10$pTcDnICe9Hie5l7Ame.ICu/rdFRWZ2CZT64mO4ioRuhD1ZnCHL4mm', -- mesma senha do admin
    'Rua Estudio',
    '11 970332140',
    'CLIENT'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM clients WHERE email = 'contacontaconta0002@gmail.com'
);
