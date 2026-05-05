CREATE INDEX IF NOT EXISTS idx_posts_author_id_created_at_desc
ON post.posts (author_id, created_at DESC);