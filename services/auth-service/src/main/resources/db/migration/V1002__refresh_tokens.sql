create table if not exists auth.refresh_tokens (
    id bigserial primary key,
    user_id bigint not null,
    token_hash text not null,
    expires_at timestamptz not null,
    created_at timestamptz not null default now(),
    revoked boolean not null default false,

    constraint fk_refresh_tokens_user
        foreign key (user_id)
        references auth.users(id)
        on delete cascade
);

create index if not exists idx_refresh_tokens_user
    on auth.refresh_tokens(user_id);

create unique index if not exists idx_refresh_tokens_token_hash
    on auth.refresh_tokens(token_hash);