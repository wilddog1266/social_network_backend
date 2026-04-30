<template>
  <AppShell
    :eyebrow="t('posts.eyebrow')"
    :title="t('posts.title')"
    :description="t('posts.description')"
    :username="authStore.username"
    @logout="logout"
  >
    <template #actions>
      <button type="button" class="button button-secondary" :disabled="loadingPosts" @click="loadPosts">
        {{ loadingPosts ? t('posts.refreshing') : t('posts.refresh') }}
      </button>
      <RouterLink class="button button-ghost" to="/feed">{{ t('posts.openFeed') }}</RouterLink>
    </template>

    <section class="surface-card composer-card">
      <div class="section-heading">
        <div>
          <span class="eyebrow">{{ t('posts.newPost') }}</span>
          <h2 class="section-title">{{ t('posts.shareUpdate') }}</h2>
          <p class="section-subtitle">{{ t('posts.shareUpdateCopy') }}</p>
        </div>
        <span class="pill">{{ contentLength }}/1000</span>
      </div>

      <textarea
        v-model="newPostContent"
        class="textarea"
        rows="5"
        maxlength="1000"
        :placeholder="t('posts.composerPlaceholder')"
      />

      <div class="composer-footer">
        <label class="upload-field">
          <span class="upload-label">{{ t('posts.attachment') }}</span>
          <input
            ref="fileInput"
            class="input"
            type="file"
            accept="image/png,image/jpeg"
            @change="handleFileChange"
          />
        </label>

        <div class="composer-actions">
          <p v-if="selectedFile" class="selected-file">{{ selectedFile.name }}</p>
          <button type="button" class="button button-primary" :disabled="loadingCreate" @click="handleCreatePost">
            {{ loadingCreate ? t('posts.publishing') : t('posts.publish') }}
          </button>
        </div>
      </div>
    </section>

    <InlineMessage v-if="error" tone="error" :message="error" />

    <div v-if="loadingPosts" class="stack">
      <LoadingCard v-for="index in 2" :key="index" />
    </div>

    <EmptyState
      v-else-if="!posts.length"
      icon="◌"
      :title="t('posts.emptyTitle')"
      :description="t('posts.emptyDescription')"
    />

    <div v-else class="stack">
      <PostCard
        v-for="post in posts"
        :key="post.id"
        :post="post"
        :comments="commentsByPostId[post.id] || []"
        :reaction="reactionsByPostId[post.id]"
        :draft-comment="newCommentByPostId[post.id] || ''"
        :allow-delete="true"
        :allow-delete-media="true"
        :author-prefix="t('common.youNumber', { id: post.authorId }).replace(` #${post.authorId}`, '')"
        @update:draft-comment="(value) => updateCommentDraft(post.id, value)"
        @submit-comment="handleCreateComment"
        @delete-comment="handleDeleteComment"
        @delete-post="handleDeletePost"
        @delete-media="handleDeleteMedia"
        @like="handleLike"
        @dislike="handleDislike"
        @remove-reaction="handleRemoveReaction"
      />
    </div>

    <template #aside>
      <FollowUserCard />

      <section class="surface-card side-card">
        <div class="side-card-header">
          <h2 class="section-title">{{ t('posts.snapshotTitle') }}</h2>
          <p class="section-subtitle">{{ t('posts.snapshotCopy') }}</p>
        </div>

        <div class="stats-grid">
          <div class="stat-card">
            <span class="meta-label">{{ t('posts.publishedPosts') }}</span>
            <strong class="stat-value">{{ posts.length }}</strong>
          </div>
          <div class="stat-card">
            <span class="meta-label">{{ t('posts.attachedImages') }}</span>
            <strong class="stat-value">{{ totalMedia }}</strong>
          </div>
          <div class="stat-card">
            <span class="meta-label">{{ t('posts.discussionEntries') }}</span>
            <strong class="stat-value">{{ totalComments }}</strong>
          </div>
        </div>
      </section>

      <section class="surface-card side-card">
        <div class="side-card-header">
          <h2 class="section-title">{{ t('posts.guidanceTitle') }}</h2>
          <p class="section-subtitle">{{ t('posts.guidanceCopy') }}</p>
        </div>
        <ul class="tip-list">
          <li v-for="item in tm('posts.guidance')" :key="item">{{ item }}</li>
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
import { createPost, deletePost, deletePostMedia, getMyPosts, uploadPostMedia } from '../api/postApi'
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
const newPostContent = ref('')
const loadingPosts = ref(false)
const loadingCreate = ref(false)
const error = ref('')
const selectedFile = ref(null)
const fileInput = ref(null)
const commentsByPostId = ref({})
const newCommentByPostId = ref({})
const reactionsByPostId = ref({})

const contentLength = computed(() => newPostContent.value.trim().length)
const totalMedia = computed(() => posts.value.reduce((sum, post) => sum + (post.media?.length || 0), 0))
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

async function loadPosts() {
  error.value = ''
  loadingPosts.value = true

  try {
    const pageData = await getMyPosts()
    const nextPosts = pageData.content || []

    posts.value = nextPosts
    commentsByPostId.value = {}
    reactionsByPostId.value = {}

    await Promise.all(nextPosts.map((post) => hydratePost(post.id)))
  } catch {
    error.value = t('posts.errors.loadFailed')
  } finally {
    loadingPosts.value = false
  }
}

async function handleCreatePost() {
  if (!newPostContent.value.trim()) {
    error.value = t('posts.errors.emptyPost')
    return
  }

  error.value = ''
  loadingCreate.value = true

  try {
    const createdPost = await createPost(newPostContent.value.trim())

    if (selectedFile.value) {
      await uploadPostMedia(createdPost.id, selectedFile.value)
    }

    newPostContent.value = ''
    selectedFile.value = null

    if (fileInput.value) {
      fileInput.value.value = ''
    }

    await loadPosts()
  } catch {
    error.value = t('posts.errors.publish')
  } finally {
    loadingCreate.value = false
  }
}

async function handleDeletePost(postId) {
  error.value = ''

  try {
    await deletePost(postId)
    await loadPosts()
  } catch {
    error.value = t('posts.errors.deletePost')
  }
}

async function handleDeleteMedia({ postId, mediaId }) {
  error.value = ''

  try {
    await deletePostMedia(postId, mediaId)
    await loadPosts()
  } catch {
    error.value = t('posts.errors.deleteImage')
  }
}

async function handleCreateComment(postId) {
  const content = (newCommentByPostId.value[postId] || '').trim()

  if (!content) {
    error.value = t('posts.errors.emptyComment')
    return
  }

  error.value = ''

  try {
    await createComment(postId, content)
    updateCommentDraft(postId, '')
    await hydratePost(postId)
  } catch {
    error.value = t('posts.errors.createComment')
  }
}

async function handleDeleteComment({ postId, commentId }) {
  error.value = ''

  try {
    await deleteComment(commentId)
    await hydratePost(postId)
  } catch {
    error.value = t('posts.errors.deleteComment')
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
    error.value = t('posts.errors.like')
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
    error.value = t('posts.errors.dislike')
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
    error.value = t('posts.errors.clearReaction')
  }
}

function handleFileChange(event) {
  selectedFile.value = event.target.files?.[0] || null
}

function logout() {
  authStore.logout()
  router.push('/login')
}

onMounted(loadPosts)
</script>

<style scoped>
.composer-card,
.side-card {
  padding: 1.5rem;
}

.composer-card {
  display: grid;
  gap: 1rem;
}

.section-heading,
.composer-footer {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.composer-footer {
  align-items: end;
  flex-wrap: wrap;
}

.upload-field {
  display: grid;
  gap: 0.45rem;
  flex: 1 1 280px;
}

.upload-label,
.selected-file {
  color: var(--text-muted);
  font-size: 0.9rem;
}

.composer-actions {
  display: grid;
  justify-items: end;
  gap: 0.75rem;
}

.stack,
.stats-grid {
  display: grid;
  gap: 1.25rem;
}

.side-card {
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

.tip-list {
  margin: 0;
  padding-left: 1rem;
  color: var(--text-soft);
}

.tip-list li + li {
  margin-top: 0.7rem;
}

@media (max-width: 768px) {
  .section-heading,
  .composer-footer {
    flex-direction: column;
    align-items: stretch;
  }

  .composer-actions {
    justify-items: stretch;
  }
}
</style>
