<template>
  <AppShell
    :eyebrow="t('notifications.eyebrow')"
    :title="t('notifications.title')"
    :description="t('notifications.description')"
    :username="authStore.username"
    @logout="logout"
  >
    <template #actions>
      <button type="button" class="button button-secondary" :disabled="loading" @click="loadNotifications">
        {{ loading ? t('notifications.refreshing') : t('notifications.refresh') }}
      </button>
      <button
        type="button"
        class="button button-primary"
        :disabled="loading || !notifications.length || unreadCount === 0"
        @click="handleMarkAllAsRead"
      >
        {{ t('notifications.markAll') }}
      </button>
    </template>

    <InlineMessage v-if="error" tone="error" :message="error" />

    <div v-if="loading" class="stack">
      <LoadingCard v-for="index in 3" :key="index" />
    </div>

    <EmptyState
      v-else-if="!notifications.length"
      icon="◍"
      :title="t('notifications.emptyTitle')"
      :description="t('notifications.emptyDescription')"
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
                {{ notification.read ? t('notifications.read') : t('notifications.unread') }}
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
          {{ t('notifications.markOne') }}
        </button>
      </article>
    </div>

    <template #aside>
      <FollowUserCard />

      <section class="surface-card side-card">
        <div class="side-card-header">
          <h2 class="section-title">{{ t('notifications.overviewTitle') }}</h2>
          <p class="section-subtitle">{{ t('notifications.overviewCopy') }}</p>
        </div>

        <div class="stats-grid">
          <div class="stat-card">
            <span class="meta-label">{{ t('notifications.unread') }}</span>
            <strong class="stat-value">{{ unreadCount }}</strong>
          </div>
          <div class="stat-card">
            <span class="meta-label">{{ t('notifications.totalLoaded') }}</span>
            <strong class="stat-value">{{ notifications.length }}</strong>
          </div>
        </div>
      </section>

      <section class="surface-card side-card">
        <div class="side-card-header">
          <h2 class="section-title">{{ t('notifications.typesTitle') }}</h2>
          <p class="section-subtitle">{{ t('notifications.typesCopy') }}</p>
        </div>
        <ul class="type-list">
          <li><span>◎</span> {{ t('notifications.newFollowers') }}</li>
          <li><span>◌</span> {{ t('notifications.newComments') }}</li>
          <li><span>◍</span> {{ t('notifications.postReactions') }}</li>
        </ul>
      </section>
    </template>
  </AppShell>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '../i18n'
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
const { t } = useI18n()

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
    error.value = t('notifications.errors.loadFailed')
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
    error.value = t('notifications.errors.markRead')
  }
}

async function handleMarkAllAsRead() {
  error.value = ''

  try {
    await markAllNotificationsAsRead()
    await loadNotifications()
  } catch {
    error.value = t('notifications.errors.markAll')
  }
}

function formatNotificationText(notification) {
  switch (notification.type) {
    case 'FOLLOWED':
      return t('notifications.notificationFollowed', { actorId: notification.actorId })
    case 'COMMENTED':
      return t('notifications.notificationCommented', { actorId: notification.actorId })
    case 'POST_LIKED':
      return t('notifications.notificationLiked', { actorId: notification.actorId })
    case 'POST_DISLIKED':
      return t('notifications.notificationDisliked', { actorId: notification.actorId })
    default:
      return t('notifications.notificationFallback', { actorId: notification.actorId })
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
  border-color: rgba(255, 181, 214, 0.3);
  background: linear-gradient(180deg, rgba(70, 31, 60, 0.95), rgba(86, 39, 74, 0.88));
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
  color: #fff1f8;
  background: rgba(244, 143, 177, 0.18);
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
