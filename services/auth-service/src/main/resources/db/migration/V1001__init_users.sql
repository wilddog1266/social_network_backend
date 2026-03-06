create schema if not exists auth;

create table if not exists auth.users (
    id bigserial primary key,
    username varchar(50) not null unique,
    email varchar(255) not null unique,
    password_hash text not null,
    created_at timestamptz not null default now()
);