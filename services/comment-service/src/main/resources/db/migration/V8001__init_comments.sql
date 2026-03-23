create table if not exists comment.comments (
    id bigserial primary key,
    post_id bigint not null,
    author_id bigint not null,
    content text not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index if not exists idx_comments_post_id
    on comment.comments(post_id);

create index if not exists idx_comments_author_id
    on comment.comments(author_id);