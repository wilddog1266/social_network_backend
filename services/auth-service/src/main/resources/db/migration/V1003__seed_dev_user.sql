insert into auth.users (username, email, password_hash)
select 'admin', 'admin@test.local', '$2a$12$NE5tUwtSpfC8bB4eMII7neFoSYlUB5qOz0f3fGAD0i69rcCr/QqTq'
where not exists (
    select 1
    from auth.users
    where username = 'admin'
);