create table post.posts (
    id bigserial primary key,
    author_id bigint not null,
    content text not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);