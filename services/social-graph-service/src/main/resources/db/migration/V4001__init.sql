create table if not exists follows (
    follower_id bigint not null,
    followee_id bigint not null,
    created_at timestamp with time zone not null default now(),

    primary key (follower_id, followee_id),

    constraint check_no_self_follow
        check (follower_id <> followee_id)
);

create index if not exists idx_follows_follower
    on follows(follower_id);

create index if not exists idx_follows_followee
    on follows(followee_id);