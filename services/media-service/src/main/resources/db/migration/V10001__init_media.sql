create schema if not exists media;

create table media.medias (
    id bigserial primary key,
    owner_user_id bigint not null,
    object_key varchar(500) not null unique,
    bucket varchar(255) not null,
    original_file_name varchar(255),
    size bigint not null,
    type varchar(50) not null,
    status varchar(50) not null,
    created_at timestamptz not null default now()
);

create index idx_medias_owner_user_id
    on media.medias(owner_user_id);

create index idx_medias_status
    on media.medias(status);