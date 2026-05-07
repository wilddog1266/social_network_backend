alter table post.post_media
    drop constraint if exists post_media_object_key_key;

alter table post.post_media
    drop column if exists object_key,
    drop column if exists file_name,
    drop column if exists content_type,
    drop column if exists file_size;

alter table post.post_media
    add column if not exists media_id bigint;

delete from post.post_media
where media_id is null;

alter table post.post_media
    alter column media_id set not null;

alter table post.post_media
    add constraint uq_post_media_post_id_media_id unique (post_id, media_id);

create index if not exists idx_post_media_media_id
    on post.post_media(media_id);