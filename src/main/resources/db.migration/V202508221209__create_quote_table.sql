CREATE TABLE quotes (
    id BIGINT not null auto_increment,
    is_colored BOOLEAN not null,
    description VARCHAR(300) not null,
    additional_cost DECIMAL(10,2),
    final_value DECIMAL(10,2) not null,
    size VARCHAR(20) not null,
    body_part VARCHAR(20) not null,
    state VARCHAR(20) not null,
    PRIMARY KEY(id)
)