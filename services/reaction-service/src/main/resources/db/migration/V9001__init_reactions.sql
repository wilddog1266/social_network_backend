create table if not exists reaction.post_reactions (
    id bigserial primary key,
    post_id bigint not null,
    user_id bigint not null,
    reaction_type varchar(20) not null,
    created_at timestamptz not null default now(),
    constraint uk_post_reactions_post_user unique (post_id, user_id)
);

create index if not exists idx_post_reactions_post_id
    on reaction.post_reactions(post_id);

create index if not exists idx_post_reactions_user_id
    on reaction.post_reactions(user_id);