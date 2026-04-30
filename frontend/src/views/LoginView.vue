<template>
  <div class="auth-page">
    <section class="auth-hero">
      <div class="auth-topbar">
        <span class="eyebrow">{{ isRegisterMode ? t('auth.createAccountEyebrow') : t('auth.welcomeBack') }}</span>
        <LanguageSwitcher />
      </div>
      <h1>{{ isRegisterMode ? t('auth.heroTitleRegister') : t('auth.heroTitleLogin') }}</h1>
      <p>
        {{ isRegisterMode ? t('auth.heroCopyRegister') : t('auth.heroCopyLogin') }}
      </p>

      <div class="hero-grid">
        <article class="hero-card surface-card">
          <h2>{{ t('auth.whatChanged') }}</h2>
          <ul>
            <li v-for="item in tm('auth.whatChangedItems')" :key="item">{{ item }}</li>
          </ul>
        </article>

        <article class="hero-card surface-card">
          <h2>{{ t('auth.whyBetter') }}</h2>
          <ul>
            <li v-for="item in tm('auth.whyBetterItems')" :key="item">{{ item }}</li>
          </ul>
        </article>
      </div>
    </section>

    <section class="auth-panel surface-card">
      <div class="auth-panel-header">
        <div>
          <span class="eyebrow">{{ isRegisterMode ? t('auth.registerEyebrow') : t('auth.loginEyebrow') }}</span>
          <h2>{{ isRegisterMode ? t('auth.registerTitle') : t('auth.signInTitle') }}</h2>
          <p>{{ isRegisterMode ? t('auth.registerCopy') : t('auth.signInCopy') }}</p>
        </div>

        <div class="mode-switch">
          <RouterLink
            class="switch-link"
            :class="{ active: !isRegisterMode }"
            to="/login"
          >
            {{ t('auth.signInTab') }}
          </RouterLink>
          <RouterLink
            class="switch-link"
            :class="{ active: isRegisterMode }"
            to="/register"
          >
            {{ t('auth.registerTab') }}
          </RouterLink>
        </div>
      </div>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <label class="field">
          <span>{{ t('auth.username') }}</span>
          <input
            v-model.trim="form.username"
            class="input"
            type="text"
            autocomplete="username"
            :placeholder="t('auth.usernamePlaceholder')"
          />
        </label>

        <label v-if="isRegisterMode" class="field">
          <span>{{ t('auth.email') }}</span>
          <input
            v-model.trim="form.email"
            class="input"
            type="email"
            autocomplete="email"
            :placeholder="t('auth.emailPlaceholder')"
          />
        </label>

        <label class="field">
          <span>{{ t('auth.password') }}</span>
          <input
            v-model="form.password"
            class="input"
            type="password"
            :autocomplete="isRegisterMode ? 'new-password' : 'current-password'"
            :placeholder="t('auth.passwordPlaceholder')"
          />
        </label>

        <InlineMessage v-if="error" tone="error" :message="error" />

        <button type="submit" class="button button-primary submit-button" :disabled="loading">
          {{ loading ? activeButtonLoadingLabel : activeButtonLabel }}
        </button>
      </form>

      <p class="auth-footer">
        {{ isRegisterMode ? t('auth.alreadyHaveAccount') : t('auth.needAccount') }}
        <RouterLink :to="isRegisterMode ? '/login' : '/register'">
          {{ isRegisterMode ? t('auth.signInInstead') : t('auth.createOneNow') }}
        </RouterLink>
      </p>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import InlineMessage from '../components/InlineMessage.vue'
import LanguageSwitcher from '../components/LanguageSwitcher.vue'
import { useI18n } from '../i18n'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const { t, tm } = useI18n()

const form = reactive({
  username: '',
  email: '',
  password: '',
})

const loading = ref(false)
const error = ref('')

const isRegisterMode = computed(() => route.name === 'register')
const activeButtonLabel = computed(() => (isRegisterMode.value ? t('auth.registerButton') : t('auth.signInButton')))
const activeButtonLoadingLabel = computed(() =>
  isRegisterMode.value ? t('auth.registerLoading') : t('auth.signInLoading')
)

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
    return t('auth.errors.requiredUsernamePassword')
  }

  if (isRegisterMode.value && !form.email) {
    return t('auth.errors.emailRequired')
  }

  if (isRegisterMode.value && form.password.length < 4) {
    return t('auth.errors.passwordTooShort')
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
    error.value = isRegisterMode.value ? t('auth.errors.registerFailed') : t('auth.errors.loginFailed')
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

.auth-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
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
  color: #ffe0ef;
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

  .auth-topbar {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
