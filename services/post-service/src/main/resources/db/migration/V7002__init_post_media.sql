create table if not exists post.post_media (
    id bigserial primary key,
    post_id bigint not null,
    object_key varchar(255) not null unique,
    file_name varchar(255) not null,
    content_type varchar(100) not null,
    file_size bigint not null,
    sort_order integer not null default 0 check (sort_order >= 0),
    created_at timestamptz not null default now(),
    constraint fk_post_media_post
        foreign key (post_id) references post.posts(id) on delete cascade
);

create index if not exists idx_post_media_post_id
    on post.post_media(post_id);