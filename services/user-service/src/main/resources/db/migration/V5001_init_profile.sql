create table if not exists profile.profiles (
    id bigserial primary key,
    user_id bigint not null unique,
    username varchar(50) not null unique,
    display_name varchar(100) not null,
    bio text,
    avatar_url text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);