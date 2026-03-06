create table if not exists flyway_ping (
    id bigserial primary key,
    created_at timestamptz not null default now()
);