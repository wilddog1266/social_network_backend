<template>
  <div class="page">
    <div class="topbar">
      <h1>My Posts</h1>
      <button @click="logout">Logout</button>
    </div>

    <div class="card create-card">
      <h2>Create post</h2>

      <textarea
        v-model="newPostContent"
        placeholder="Write something..."
        rows="4"
      />

      <input
        type="file"
        accept="image/png,image/jpeg"
        @change="handleFileChange"
      />

      <p v-if="selectedFile">
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
          <strong>Post #{{ post.id }}</strong>
          <span>{{ formatDate(post.createdAt) }}</span>
        </div>

        <p class="post-content">{{ post.content }}</p>

        <div v-if="post.media && post.media.length > 0" class="media-list">
          <img
            v-for="media in post.media"
            :key="media.id"
            :src="media.url"
            :alt="media.fileName"
            class="post-image"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { createPost, getMyPosts, uploadPostMedia } from '../api/postApi'

const router = useRouter()
const authStore = useAuthStore()

const posts = ref([])
const newPostContent = ref('')
const loadingPosts = ref(false)
const loadingCreate = ref(false)
const error = ref('')
const selectedFile = ref(null)

const logout = () => {
  authStore.logout()
  router.push('/login')
}

const loadPosts = async () => {
  error.value = ''
  loadingPosts.value = true

  try {
    const pageData = await getMyPosts()
    posts.value = pageData.content || []
  } catch (e) {
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
    await loadPosts()
  } catch (e) {
    error.value = 'Failed to create post'
  } finally {
    loadingCreate.value = false
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

.error {
  color: #b91c1c;
  margin-top: 12px;
}

.empty {
  margin-top: 16px;
  color: #6b7280;
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
  color: #6b7280;
  font-size: 14px;
}

.post-content {
  margin-bottom: 12px;
  white-space: pre-wrap;
}

.media-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.post-image {
  width: 220px;
  max-width: 100%;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}
</style>