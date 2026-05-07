alter table profile.profiles
    drop column if exists avatar_url;

alter table profile.profiles
    add column if not exists avatar_id bigint;

create index if not exists idx_profiles_avatar_id
    on profile.profiles(avatar_id);