CREATE INDEX IF NOT EXISTS idx_comments_post_id_created_at_desc
ON comment.comments (post_id, created_at DESC);