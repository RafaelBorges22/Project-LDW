CREATE TABLE clients (
    id BIGINT not null auto_increment,
    name VARCHAR(150) not null,
    email VARCHAR(100) not null,
    -- Futura implementação do hash do BCrypt do Spring Security
    password VARCHAR(255) not null,
    address VARCHAR(250) not null,
    PRIMARY KEY(id)
)engine=InnoDB default charset=utf8;