<template>
  <AppShell
    :eyebrow="t('feed.eyebrow')"
    :title="t('feed.title')"
    :description="t('feed.description')"
    :username="authStore.username"
    @logout="logout"
  >
    <template #actions>
      <button type="button" class="button button-secondary" :disabled="loading" @click="loadFeed">
        {{ loading ? t('feed.refreshing') : t('feed.refresh') }}
      </button>
      <RouterLink class="button button-primary" to="/posts/me">{{ t('feed.createPost') }}</RouterLink>
    </template>

    <InlineMessage v-if="error" tone="error" :message="error" />

    <div v-if="loading" class="stack">
      <LoadingCard v-for="index in 3" :key="index" />
    </div>

    <EmptyState
      v-else-if="!posts.length"
      icon="◎"
      :title="t('feed.emptyTitle')"
      :description="t('feed.emptyDescription')"
      :action-label="t('feed.goToMyPosts')"
      @action="router.push('/posts/me')"
    />

    <div v-else class="stack">
      <PostCard
        v-for="post in posts"
        :key="post.id"
        :post="post"
        :comments="commentsByPostId[post.id] || []"
        :reaction="reactionsByPostId[post.id]"
        :draft-comment="newCommentByPostId[post.id] || ''"
        :author-prefix="t('common.authoredBy', { id: post.authorId }).replace(` #${post.authorId}`, '')"
        @update:draft-comment="(value) => updateCommentDraft(post.id, value)"
        @submit-comment="handleCreateComment"
        @delete-comment="handleDeleteComment"
        @like="handleLike"
        @dislike="handleDislike"
        @remove-reaction="handleRemoveReaction"
      />
    </div>

    <template #aside>
      <FollowUserCard />

      <section class="surface-card side-card">
        <div class="side-card-header">
          <h2 class="section-title">{{ t('feed.summaryTitle') }}</h2>
          <p class="section-subtitle">{{ t('feed.summaryCopy') }}</p>
        </div>
        <div class="stats-grid">
          <div class="stat-card">
            <span class="meta-label">{{ t('feed.postsLoaded') }}</span>
            <strong class="stat-value">{{ posts.length }}</strong>
          </div>
          <div class="stat-card">
            <span class="meta-label">{{ t('feed.commentsVisible') }}</span>
            <strong class="stat-value">{{ totalComments }}</strong>
          </div>
        </div>
      </section>

      <section class="surface-card side-card">
        <div class="side-card-header">
          <h2 class="section-title">{{ t('feed.tipsTitle') }}</h2>
          <p class="section-subtitle">{{ t('feed.tipsCopy') }}</p>
        </div>
        <ul class="tip-list">
          <li v-for="item in tm('feed.tips')" :key="item">{{ item }}</li>
        </ul>
      </section>
    </template>
  </AppShell>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useI18n } from '../i18n'
import { useAuthStore } from '../stores/auth'
import { getFeed } from '../api/feedApi'
import { createComment, deleteComment, getCommentsByPostId } from '../api/commentApi'
import {
  dislikePost,
  getPostReactionSummary,
  likePost,
  removePostReaction,
} from '../api/reactionApi'
import AppShell from '../components/AppShell.vue'
import EmptyState from '../components/EmptyState.vue'
import FollowUserCard from '../components/FollowUserCard.vue'
import InlineMessage from '../components/InlineMessage.vue'
import LoadingCard from '../components/LoadingCard.vue'
import PostCard from '../components/PostCard.vue'

const router = useRouter()
const authStore = useAuthStore()
const { t, tm } = useI18n()

const posts = ref([])
const loading = ref(false)
const error = ref('')
const commentsByPostId = ref({})
const newCommentByPostId = ref({})
const reactionsByPostId = ref({})

const totalComments = computed(() =>
  Object.values(commentsByPostId.value).reduce((sum, comments) => sum + comments.length, 0)
)

function updateCommentDraft(postId, value) {
  newCommentByPostId.value = {
    ...newCommentByPostId.value,
    [postId]: value,
  }
}

async function hydratePost(postId) {
  const [comments, reaction] = await Promise.all([
    getCommentsByPostId(postId).catch(() => []),
    getPostReactionSummary(postId).catch(() => ({
      postId,
      likeCount: 0,
      dislikeCount: 0,
      myReaction: null,
    })),
  ])

  commentsByPostId.value = {
    ...commentsByPostId.value,
    [postId]: comments,
  }

  reactionsByPostId.value = {
    ...reactionsByPostId.value,
    [postId]: reaction,
  }
}

async function loadFeed() {
  error.value = ''
  loading.value = true

  try {
    const pageData = await getFeed(0, 20)
    const nextPosts = pageData.content || []

    posts.value = nextPosts
    commentsByPostId.value = {}
    reactionsByPostId.value = {}

    await Promise.all(nextPosts.map((post) => hydratePost(post.id)))
  } catch {
    error.value = t('feed.errors.loadFailed')
  } finally {
    loading.value = false
  }
}

async function handleCreateComment(postId) {
  const content = (newCommentByPostId.value[postId] || '').trim()

  if (!content) {
    error.value = t('feed.errors.emptyComment')
    return
  }

  error.value = ''

  try {
    await createComment(postId, content)
    updateCommentDraft(postId, '')
    await hydratePost(postId)
  } catch {
    error.value = t('feed.errors.createComment')
  }
}

async function handleDeleteComment({ postId, commentId }) {
  error.value = ''

  try {
    await deleteComment(commentId)
    await hydratePost(postId)
  } catch {
    error.value = t('feed.errors.deleteComment')
  }
}

async function handleLike(postId) {
  error.value = ''

  try {
    reactionsByPostId.value = {
      ...reactionsByPostId.value,
      [postId]: await likePost(postId),
    }
  } catch {
    error.value = t('feed.errors.like')
  }
}

async function handleDislike(postId) {
  error.value = ''

  try {
    reactionsByPostId.value = {
      ...reactionsByPostId.value,
      [postId]: await dislikePost(postId),
    }
  } catch {
    error.value = t('feed.errors.dislike')
  }
}

async function handleRemoveReaction(postId) {
  error.value = ''

  try {
    reactionsByPostId.value = {
      ...reactionsByPostId.value,
      [postId]: await removePostReaction(postId),
    }
  } catch {
    error.value = t('feed.errors.clearReaction')
  }
}

function logout() {
  authStore.logout()
  router.push('/login')
}

onMounted(loadFeed)
</script>

<style scoped>
.stack {
  display: grid;
  gap: 1.25rem;
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

.stats-grid {
  display: grid;
  gap: 0.9rem;
}

.stat-card {
  padding: 1rem;
  border-radius: 18px;
  background: rgba(148, 163, 184, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.08);
}

.tip-list {
  margin: 0;
  padding-left: 1rem;
  color: var(--text-soft);
}

.tip-list li + li {
  margin-top: 0.7rem;
}
</style>
