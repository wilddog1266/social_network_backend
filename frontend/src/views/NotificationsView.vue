<template>
  <div class="page">
    <div class="topbar">
      <h1>Notifications</h1>

      <div class="topbar-actions">
        <button @click="goToFeed">Feed</button>
        <button @click="goToMyPosts">My Posts</button>
        <button @click="logout">Logout</button>
      </div>
    </div>

    <div class="card">
      <div class="header-row">
        <h2>Your notifications</h2>

        <div class="header-actions">
          <button @click="loadNotifications" :disabled="loading">
            {{ loading ? 'Loading...' : 'Reload' }}
          </button>

          <button @click="handleMarkAllAsRead" :disabled="loading || notifications.length === 0">
            Mark all as read
          </button>
        </div>
      </div>

      <p v-if="error" class="error">{{ error }}</p>

      <div v-if="notifications.length === 0 && !loading" class="empty">
        No notifications yet
      </div>

      <div
        v-for="notification in notifications"
        :key="notification.id"
        class="notification"
        :class="{ unread: !notification.read }"
      >
        <div class="notification-main">
          <div class="notification-text">
            {{ formatNotificationText(notification) }}
          </div>

          <div class="notification-meta">
            <span>{{ formatDate(notification.createdAt) }}</span>
            <span v-if="notification.read" class="read-badge">Read</span>
            <span v-else class="unread-badge">Unread</span>
          </div>
        </div>

        <button
          v-if="!notification.read"
          class="small-btn"
          @click="handleMarkAsRead(notification.id)"
        >
          Mark as read
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  getNotifications,
  markAllNotificationsAsRead,
  markNotificationAsRead,
} from '../api/notificationApi'

const router = useRouter()
const authStore = useAuthStore()

const notifications = ref([])
const loading = ref(false)
const error = ref('')

const loadNotifications = async () => {
  error.value = ''
  loading.value = true

  try {
    const pageData = await getNotifications(0, 20)
    notifications.value = pageData.content || []
  } catch {
    error.value = 'Failed to load notifications'
  } finally {
    loading.value = false
  }
}

const handleMarkAsRead = async (id) => {
  error.value = ''

  try {
    await markNotificationAsRead(id)
    await loadNotifications()
  } catch {
    error.value = 'Failed to mark notification as read'
  }
}

const handleMarkAllAsRead = async () => {
  error.value = ''

  try {
    await markAllNotificationsAsRead()
    await loadNotifications()
  } catch {
    error.value = 'Failed to mark all notifications as read'
  }
}

const formatNotificationText = (notification) => {
  switch (notification.type) {
    case 'FOLLOWED':
      return `User #${notification.actorId} followed you`
    case 'COMMENTED':
      return `User #${notification.actorId} commented on your post`
    case 'POST_LIKED':
      return `User #${notification.actorId} liked your post`
    case 'POST_DISLIKED':
      return `User #${notification.actorId} disliked your post`
    default:
      return `User #${notification.actorId} did something`
  }
}

const formatDate = (value) => {
  if (!value) return ''
  return new Date(value).toLocaleString()
}

const goToFeed = () => {
  router.push({ name: 'feed' })
}

const goToMyPosts = () => {
  router.push({ name: 'my-posts' })
}

const logout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 32px;
  background: #f5f7fb;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.topbar-actions {
  display: flex;
  gap: 10px;
}

.card {
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

button {
  width: fit-content;
  padding: 10px 12px;
  border: none;
  border-radius: 8px;
  background: #111827;
  color: white;
  cursor: pointer;
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.small-btn {
  padding: 8px 10px;
  font-size: 13px;
}

.error {
  color: #b91c1c;
  margin-top: 12px;
}

.empty {
  margin-top: 16px;
  color: #6b7280;
}

.notification {
  margin-top: 14px;
  padding: 14px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.notification.unread {
  border-color: #bfdbfe;
  background: #eff6ff;
}

.notification-main {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.notification-text {
  font-size: 15px;
}

.notification-meta {
  display: flex;
  gap: 10px;
  align-items: center;
  color: #6b7280;
  font-size: 13px;
}

.read-badge {
  color: #065f46;
}

.unread-badge {
  color: #1d4ed8;
}
</style>