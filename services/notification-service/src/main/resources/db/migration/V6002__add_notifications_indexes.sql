CREATE INDEX IF NOT EXISTS idx_notifications_user_id_created_at_desc
ON notification.notifications (user_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_notifications_user_id_is_read
ON notification.notifications (user_id, is_read);