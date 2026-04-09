<template>
  <AppShell
    eyebrow="Inbox"
    title="Notifications"
    description="A cleaner activity inbox that separates unread items, gives better hierarchy, and keeps bulk actions easy to reach."
    :username="authStore.username"
    @logout="logout"
  >
    <template #actions>
      <button type="button" class="button button-secondary" :disabled="loading" @click="loadNotifications">
        {{ loading ? 'Refreshing...' : 'Refresh inbox' }}
      </button>
      <button
        type="button"
        class="button button-primary"
        :disabled="loading || !notifications.length || unreadCount === 0"
        @click="handleMarkAllAsRead"
      >
        Mark all as read
      </button>
    </template>

    <InlineMessage v-if="error" tone="error" :message="error" />

    <div v-if="loading" class="stack">
      <LoadingCard v-for="index in 3" :key="index" />
    </div>

    <EmptyState
      v-else-if="!notifications.length"
      icon="◍"
      title="No notifications yet"
      description="New follows, comments, and reactions will appear here as soon as the backend produces them."
    />

    <div v-else class="stack">
      <article
        v-for="notification in notifications"
        :key="notification.id"
        class="notification-card surface-card"
        :class="{ unread: !notification.read }"
      >
        <div class="notification-main">
          <div class="notification-icon" :class="{ unread: !notification.read }">
            {{ notificationIcon(notification.type) }}
          </div>

          <div class="notification-content">
            <div class="notification-top">
              <p class="notification-title">{{ formatNotificationText(notification) }}</p>
              <span class="pill" :class="{ unread: !notification.read }">
                {{ notification.read ? 'Read' : 'Unread' }}
              </span>
            </div>

            <p class="notification-meta">
              <span>{{ formatRelativeTime(notification.createdAt) }}</span>
              <span>{{ formatDateTime(notification.createdAt) }}</span>
            </p>
          </div>
        </div>

        <button
          v-if="!notification.read"
          type="button"
          class="button button-ghost"
          @click="handleMarkAsRead(notification.id)"
        >
          Mark as read
        </button>
      </article>
    </div>

    <template #aside>
      <FollowUserCard />

      <section class="surface-card side-card">
        <div class="side-card-header">
          <h2 class="section-title">Inbox overview</h2>
          <p class="section-subtitle">Useful at-a-glance counts for attention management.</p>
        </div>

        <div class="stats-grid">
          <div class="stat-card">
            <span class="meta-label">Unread</span>
            <strong class="stat-value">{{ unreadCount }}</strong>
          </div>
          <div class="stat-card">
            <span class="meta-label">Total loaded</span>
            <strong class="stat-value">{{ notifications.length }}</strong>
          </div>
        </div>
      </section>

      <section class="surface-card side-card">
        <div class="side-card-header">
          <h2 class="section-title">Notification types</h2>
          <p class="section-subtitle">Current backend events represented in the UI.</p>
        </div>
        <ul class="type-list">
          <li><span>◎</span> New followers</li>
          <li><span>◌</span> New comments</li>
          <li><span>◍</span> Post reactions</li>
        </ul>
      </section>
    </template>
  </AppShell>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  getNotifications,
  markAllNotificationsAsRead,
  markNotificationAsRead,
} from '../api/notificationApi'
import AppShell from '../components/AppShell.vue'
import EmptyState from '../components/EmptyState.vue'
import FollowUserCard from '../components/FollowUserCard.vue'
import InlineMessage from '../components/InlineMessage.vue'
import LoadingCard from '../components/LoadingCard.vue'
import { formatDateTime, formatRelativeTime } from '../utils/formatters'

const router = useRouter()
const authStore = useAuthStore()

const notifications = ref([])
const loading = ref(false)
const error = ref('')

const unreadCount = computed(() => notifications.value.filter((notification) => !notification.read).length)

async function loadNotifications() {
  error.value = ''
  loading.value = true

  try {
    const pageData = await getNotifications(0, 20)
    notifications.value = pageData.content || []
  } catch {
    error.value = 'Notifications could not be loaded. Verify that the notification service is available.'
  } finally {
    loading.value = false
  }
}

async function handleMarkAsRead(id) {
  error.value = ''

  try {
    await markNotificationAsRead(id)
    await loadNotifications()
  } catch {
    error.value = 'This notification could not be marked as read.'
  }
}

async function handleMarkAllAsRead() {
  error.value = ''

  try {
    await markAllNotificationsAsRead()
    await loadNotifications()
  } catch {
    error.value = 'Notifications could not be updated.'
  }
}

function formatNotificationText(notification) {
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
      return `User #${notification.actorId} interacted with your profile`
  }
}

function notificationIcon(type) {
  if (type === 'FOLLOWED') return '◎'
  if (type === 'COMMENTED') return '◌'
  return '◍'
}

function logout() {
  authStore.logout()
  router.push('/login')
}

onMounted(loadNotifications)
</script>

<style scoped>
.stack,
.stats-grid {
  display: grid;
  gap: 1rem;
}

.notification-card,
.notification-main,
.notification-top,
.notification-meta {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.notification-card {
  align-items: center;
  padding: 1.2rem 1.4rem;
}

.notification-card.unread {
  border-color: rgba(96, 165, 250, 0.28);
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.95), rgba(20, 33, 61, 0.88));
}

.notification-main {
  align-items: center;
  flex: 1;
}

.notification-icon {
  width: 50px;
  height: 50px;
  display: grid;
  place-items: center;
  border-radius: 16px;
  background: rgba(148, 163, 184, 0.1);
  color: var(--text-soft);
  font-weight: 700;
}

.notification-icon.unread,
.pill.unread {
  color: #dbeafe;
  background: rgba(59, 130, 246, 0.16);
}

.notification-content {
  flex: 1;
  display: grid;
  gap: 0.45rem;
}

.notification-title,
.notification-meta {
  margin: 0;
}

.notification-title {
  font-weight: 700;
}

.notification-meta {
  color: var(--text-muted);
  font-size: 0.88rem;
  flex-wrap: wrap;
  justify-content: start;
}

.side-card {
  padding: 1.4rem;
  display: grid;
  gap: 1rem;
}

.side-card-header {
  display: grid;
  gap: 0.35rem;
}

.stat-card {
  padding: 1rem;
  border-radius: 18px;
  background: rgba(148, 163, 184, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.08);
}

.type-list {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 0.8rem;
  color: var(--text-soft);
}

.type-list li {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.type-list span {
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: rgba(148, 163, 184, 0.08);
}

@media (max-width: 768px) {
  .notification-card,
  .notification-main,
  .notification-top {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
