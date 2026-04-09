<template>
  <div class="auth-page">
    <section class="auth-hero">
      <span class="eyebrow">{{ isRegisterMode ? 'Create account' : 'Welcome back' }}</span>
      <h1>{{ isRegisterMode ? 'Launch your profile with a polished first step.' : 'A cleaner social space for focused conversations.' }}</h1>
      <p>
        {{ isRegisterMode
          ? 'Set up your account once, then move straight into publishing posts, reacting to updates, and tracking notifications.'
          : 'Sign in to manage your posts, review notifications, and stay close to the backend-powered feed you already have.' }}
      </p>

      <div class="hero-grid">
        <article class="hero-card surface-card">
          <h2>What changed</h2>
          <ul>
            <li>Unified visual system with shared surfaces, spacing, and actions</li>
            <li>Responsive dashboard layout for feed, posts, and notifications</li>
            <li>More robust empty, loading, and error states throughout the app</li>
          </ul>
        </article>

        <article class="hero-card surface-card">
          <h2>Why it feels better</h2>
          <ul>
            <li>Clearer typography and hierarchy for fast scanning</li>
            <li>Consistent forms and buttons instead of page-specific prototypes</li>
            <li>Practical structure that stays easy to extend</li>
          </ul>
        </article>
      </div>
    </section>

    <section class="auth-panel surface-card">
      <div class="auth-panel-header">
        <div>
          <span class="eyebrow">{{ isRegisterMode ? 'Register' : 'Login' }}</span>
          <h2>{{ isRegisterMode ? 'Create your account' : 'Sign in to continue' }}</h2>
          <p>{{ isRegisterMode ? 'Use the backend registration endpoint and land directly in the app.' : 'Your existing backend login remains the source of truth.' }}</p>
        </div>

        <div class="mode-switch">
          <RouterLink
            class="switch-link"
            :class="{ active: !isRegisterMode }"
            to="/login"
          >
            Sign in
          </RouterLink>
          <RouterLink
            class="switch-link"
            :class="{ active: isRegisterMode }"
            to="/register"
          >
            Register
          </RouterLink>
        </div>
      </div>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <label class="field">
          <span>Username</span>
          <input
            v-model.trim="form.username"
            class="input"
            type="text"
            autocomplete="username"
            placeholder="Choose a username"
          />
        </label>

        <label v-if="isRegisterMode" class="field">
          <span>Email</span>
          <input
            v-model.trim="form.email"
            class="input"
            type="email"
            autocomplete="email"
            placeholder="you@example.com"
          />
        </label>

        <label class="field">
          <span>Password</span>
          <input
            v-model="form.password"
            class="input"
            type="password"
            :autocomplete="isRegisterMode ? 'new-password' : 'current-password'"
            placeholder="Enter your password"
          />
        </label>

        <InlineMessage v-if="error" tone="error" :message="error" />

        <button type="submit" class="button button-primary submit-button" :disabled="loading">
          {{ loading ? activeButtonLoadingLabel : activeButtonLabel }}
        </button>
      </form>

      <p class="auth-footer">
        {{ isRegisterMode ? 'Already have an account?' : 'Need an account?' }}
        <RouterLink :to="isRegisterMode ? '/login' : '/register'">
          {{ isRegisterMode ? 'Sign in instead' : 'Create one now' }}
        </RouterLink>
      </p>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import InlineMessage from '../components/InlineMessage.vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const form = reactive({
  username: '',
  email: '',
  password: '',
})

const loading = ref(false)
const error = ref('')

const isRegisterMode = computed(() => route.name === 'register')
const activeButtonLabel = computed(() => (isRegisterMode.value ? 'Create account' : 'Sign in'))
const activeButtonLoadingLabel = computed(() => (isRegisterMode.value ? 'Creating account...' : 'Signing in...'))

function resetFeedback() {
  error.value = ''
}

watch(
  () => route.name,
  () => {
    resetFeedback()
  }
)

function validateForm() {
  if (!form.username || !form.password) {
    return 'Username and password are required.'
  }

  if (isRegisterMode.value && !form.email) {
    return 'Email is required to create an account.'
  }

  if (isRegisterMode.value && form.password.length < 4) {
    return 'Choose a password with at least 4 characters.'
  }

  return ''
}

async function handleSubmit() {
  error.value = validateForm()

  if (error.value) {
    return
  }

  loading.value = true

  try {
    if (isRegisterMode.value) {
      await authStore.register({
        username: form.username,
        email: form.email,
        password: form.password,
      })
    } else {
      await authStore.login(form.username, form.password)
    }

    router.push('/feed')
  } catch (requestError) {
    error.value =
      requestError?.response?.data?.message ||
      (isRegisterMode.value ? 'Account creation failed. Please verify your details.' : 'Login failed. Check your username and password.')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  width: min(calc(100% - 32px), 1240px);
  margin: 0 auto;
  padding: 2rem 0;
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(380px, 460px);
  gap: 1.5rem;
  align-items: stretch;
}

.auth-hero,
.auth-panel {
  padding: 2rem;
}

.auth-hero {
  display: grid;
  align-content: center;
  gap: 1rem;
}

.auth-hero h1,
.auth-panel h2 {
  margin: 0;
  line-height: 1;
  letter-spacing: -0.05em;
}

.auth-hero h1 {
  font-size: clamp(2.6rem, 5vw, 4.75rem);
  max-width: 10ch;
}

.auth-hero p,
.auth-panel p,
.auth-footer {
  margin: 0;
  color: var(--text-muted);
}

.hero-grid {
  margin-top: 1rem;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.hero-card {
  padding: 1.5rem;
}

.hero-card h2 {
  margin: 0 0 0.9rem;
  font-size: 1.1rem;
}

.hero-card ul {
  margin: 0;
  padding-left: 1rem;
  color: var(--text-soft);
}

.hero-card li + li {
  margin-top: 0.7rem;
}

.auth-panel {
  display: grid;
  align-content: center;
  gap: 1.5rem;
}

.auth-panel-header {
  display: grid;
  gap: 1rem;
}

.mode-switch {
  display: inline-grid;
  grid-template-columns: repeat(2, 1fr);
  padding: 0.25rem;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.08);
}

.switch-link {
  display: inline-flex;
  justify-content: center;
  padding: 0.75rem 1rem;
  border-radius: 999px;
  color: var(--text-muted);
  font-weight: 600;
}

.switch-link.active {
  color: var(--text);
  background: rgba(15, 23, 42, 0.82);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.12);
}

.auth-form {
  display: grid;
  gap: 1rem;
}

.field {
  display: grid;
  gap: 0.55rem;
}

.field span {
  color: var(--text-soft);
  font-size: 0.92rem;
}

.submit-button {
  width: 100%;
}

.auth-footer a {
  color: #bfdbfe;
  font-weight: 600;
}

@media (max-width: 1040px) {
  .auth-page {
    grid-template-columns: 1fr;
  }

  .auth-hero h1 {
    max-width: 13ch;
  }
}

@media (max-width: 720px) {
  .auth-page {
    width: min(calc(100% - 20px), 1240px);
    padding: 1rem 0;
  }

  .auth-hero,
  .auth-panel {
    padding: 1.4rem;
  }

  .hero-grid {
    grid-template-columns: 1fr;
  }
}
</style>
