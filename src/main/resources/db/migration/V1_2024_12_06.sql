create table conta (
    id  bigserial not null,
    data_vencimento timestamp,
    data_pagamento timestamp,
    valor decimal,
    descricao varchar(255),
    situacao varchar(255),
    primary key (id)
)