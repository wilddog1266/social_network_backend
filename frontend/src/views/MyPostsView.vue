<template>
  <div class="page">
    <div class="topbar">
      <h1>My Posts</h1>

      <div class="topbar-actions">
        <button @click="goToFeed">Feed</button>
        <button @click="goToNotifications">Notifications</button>
        <button @click="logout">Logout</button>
      </div>
    </div>

    <div class="card create-card">
      <h2>Create post</h2>

      <textarea
        v-model="newPostContent"
        placeholder="Write something..."
        rows="4"
      />

      <input
        ref="fileInput"
        type="file"
        accept="image/png,image/jpeg"
        @change="handleFileChange"
      />

      <p v-if="selectedFile" class="selected-file">
        Selected file: {{ selectedFile.name }}
      </p>

      <button @click="handleCreatePost" :disabled="loadingCreate">
        {{ loadingCreate ? 'Creating...' : 'Create post' }}
      </button>
    </div>

    <div class="card">
      <h2>Your posts</h2>

      <button @click="loadPosts" :disabled="loadingPosts">
        {{ loadingPosts ? 'Loading...' : 'Reload posts' }}
      </button>

      <p v-if="error" class="error">{{ error }}</p>

      <div v-if="posts.length === 0 && !loadingPosts" class="empty">
        No posts yet
      </div>

      <div v-for="post in posts" :key="post.id" class="post">
        <div class="post-header">
          <div>
            <strong>Post #{{ post.id }}</strong>
            <div class="post-date">{{ formatDate(post.createdAt) }}</div>
          </div>

          <div class="post-actions">
            <button class="danger-btn" @click="handleDeletePost(post.id)">
              Delete post
            </button>
          </div>
        </div>

        <p class="post-content">{{ post.content }}</p>

        <div class="reaction-bar">
          <button
            class="reaction-btn"
            :class="{ active: reactionsByPostId[post.id]?.myReaction === 'LIKE' }"
            @click="handleLike(post.id)"
          >
            👍 Like ({{ reactionsByPostId[post.id]?.likeCount ?? 0 }})
          </button>

          <button
            class="reaction-btn"
            :class="{ active: reactionsByPostId[post.id]?.myReaction === 'DISLIKE' }"
            @click="handleDislike(post.id)"
          >
            👎 Dislike ({{ reactionsByPostId[post.id]?.dislikeCount ?? 0 }})
          </button>

          <button
            v-if="reactionsByPostId[post.id]?.myReaction"
            class="reaction-remove-btn"
            @click="handleRemoveReaction(post.id)"
          >
            Remove reaction
          </button>
        </div>

        <div v-if="post.media && post.media.length > 0" class="media-list">
          <div
            v-for="media in post.media"
            :key="media.id"
            class="media-item"
          >
            <img
              :src="media.url"
              :alt="media.fileName"
              class="post-image"
            />
            <button
              class="danger-btn small-btn"
              @click="handleDeleteMedia(post.id, media.id)"
            >
              Delete image
            </button>
          </div>
        </div>

        <div class="comments-section">
          <h3>Comments</h3>

          <div class="comment-form">
            <input
              v-model="newCommentByPostId[post.id]"
              type="text"
              placeholder="Write a comment..."
            />
            <button @click="handleCreateComment(post.id)">
              Add comment
            </button>
          </div>

          <div
            v-if="commentsByPostId[post.id] && commentsByPostId[post.id].length > 0"
            class="comments-list"
          >
            <div
              v-for="comment in commentsByPostId[post.id]"
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-header">
                <strong>Comment #{{ comment.id }}</strong>
                <span>{{ formatDate(comment.createdAt) }}</span>
              </div>

              <p class="comment-content">{{ comment.content }}</p>

              <button
                class="danger-btn small-btn"
                @click="handleDeleteComment(post.id, comment.id)"
              >
                Delete comment
              </button>
            </div>
          </div>

          <div v-else class="empty-comments">
            No comments yet
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  createPost,
  deletePost,
  deletePostMedia,
  getMyPosts,
  uploadPostMedia,
} from '../api/postApi'
import {
  createComment,
  deleteComment,
  getCommentsByPostId,
} from '../api/commentApi'
import {
  dislikePost,
  getPostReactionSummary,
  likePost,
  removePostReaction,
} from '../api/reactionApi'

const router = useRouter()
const authStore = useAuthStore()

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

const logout = () => {
  authStore.logout()
  router.push('/login')
}

const goToFeed = () => {
  router.push('/feed')
}

const goToNotifications = () => {
  router.push({ name: 'notifications' })
}

const loadCommentsForPost = async (postId) => {
  try {
    const comments = await getCommentsByPostId(postId)
    commentsByPostId.value[postId] = comments
  } catch {
    commentsByPostId.value[postId] = []
  }
}

const loadReactionSummaryForPost = async (postId) => {
  try {
    const summary = await getPostReactionSummary(postId)
    reactionsByPostId.value[postId] = summary
  } catch {
    reactionsByPostId.value[postId] = {
      postId,
      likeCount: 0,
      dislikeCount: 0,
      myReaction: null,
    }
  }
}

const loadPosts = async () => {
  error.value = ''
  loadingPosts.value = true

  try {
    const pageData = await getMyPosts()
    posts.value = pageData.content || []

    for (const post of posts.value) {
      await loadCommentsForPost(post.id)
      await loadReactionSummaryForPost(post.id)
    }
  } catch {
    error.value = 'Failed to load posts'
  } finally {
    loadingPosts.value = false
  }
}

const handleCreatePost = async () => {
  if (!newPostContent.value.trim()) {
    error.value = 'Post content cannot be empty'
    return
  }

  error.value = ''
  loadingCreate.value = true

  try {
    const createdPost = await createPost(newPostContent.value)

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
    error.value = 'Failed to create post'
  } finally {
    loadingCreate.value = false
  }
}

const handleDeletePost = async (postId) => {
  error.value = ''

  try {
    await deletePost(postId)
    await loadPosts()
  } catch {
    error.value = 'Failed to delete post'
  }
}

const handleDeleteMedia = async (postId, mediaId) => {
  error.value = ''

  try {
    await deletePostMedia(postId, mediaId)
    await loadPosts()
  } catch {
    error.value = 'Failed to delete image'
  }
}

const handleCreateComment = async (postId) => {
  const content = newCommentByPostId.value[postId]?.trim()

  if (!content) {
    error.value = 'Comment content cannot be empty'
    return
  }

  error.value = ''

  try {
    await createComment(postId, content)
    newCommentByPostId.value[postId] = ''
    await loadCommentsForPost(postId)
  } catch {
    error.value = 'Failed to create comment'
  }
}

const handleDeleteComment = async (postId, commentId) => {
  error.value = ''

  try {
    await deleteComment(commentId)
    await loadCommentsForPost(postId)
  } catch {
    error.value = 'Failed to delete comment'
  }
}

const handleLike = async (postId) => {
  error.value = ''

  try {
    const summary = await likePost(postId)
    reactionsByPostId.value[postId] = summary
  } catch {
    error.value = 'Failed to like post'
  }
}

const handleDislike = async (postId) => {
  error.value = ''

  try {
    const summary = await dislikePost(postId)
    reactionsByPostId.value[postId] = summary
  } catch {
    error.value = 'Failed to dislike post'
  }
}

const handleRemoveReaction = async (postId) => {
  error.value = ''

  try {
    const summary = await removePostReaction(postId)
    reactionsByPostId.value[postId] = summary
  } catch {
    error.value = 'Failed to remove reaction'
  }
}

const handleFileChange = (event) => {
  const file = event.target.files?.[0] || null
  selectedFile.value = file
}

const formatDate = (value) => {
  if (!value) return ''
  return new Date(value).toLocaleString()
}

onMounted(() => {
  loadPosts()
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
  margin-bottom: 20px;
}

.create-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d0d7e2;
  border-radius: 8px;
  resize: vertical;
  font: inherit;
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

.danger-btn {
  background: #b91c1c;
}

.small-btn {
  margin-top: 8px;
  font-size: 13px;
  padding: 8px 10px;
}

.error {
  color: #b91c1c;
  margin-top: 12px;
}

.empty {
  margin-top: 16px;
  color: #6b7280;
}

.selected-file {
  color: #374151;
  font-size: 14px;
}

.post {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.post-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.post-date {
  color: #6b7280;
  font-size: 14px;
  margin-top: 4px;
}

.post-actions {
  display: flex;
  align-items: flex-start;
}

.post-content {
  margin-bottom: 12px;
  white-space: pre-wrap;
}

.reaction-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 14px;
}

.reaction-btn {
  background: #374151;
}

.reaction-btn.active {
  background: #2563eb;
}

.reaction-remove-btn {
  background: #6b7280;
}

.media-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.media-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.post-image {
  width: 220px;
  max-width: 100%;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}

.comments-section {
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.comment-form {
  display: flex;
  gap: 10px;
  margin-bottom: 14px;
}

.comment-form input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #d0d7e2;
  border-radius: 8px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-item {
  padding: 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
  color: #6b7280;
  font-size: 13px;
}

.comment-content {
  margin-bottom: 8px;
  white-space: pre-wrap;
}

.empty-comments {
  color: #6b7280;
  font-size: 14px;
}
</style>