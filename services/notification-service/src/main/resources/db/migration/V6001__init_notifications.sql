create table if not exists notification.notifications (
    id bigserial primary key,
    user_id bigint not null,
    actor_id bigint not null,
    type varchar(50) not null,
    is_read boolean not null default false,
    created_at timestamptz not null default now()
);