create table merchant_projection
(
    merchant_id            uuid   not null,
    data                   jsonb  not null,
    latest_sequence_number bigint not null,
    primary key (merchant_id)
);
