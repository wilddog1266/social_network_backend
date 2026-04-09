import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import MyPostsView from '../views/MyPostsView.vue'
import NotificationsView from '../views/NotificationsView.vue'
import FeedView from '../views/FeedView.vue'
import ProfileView from '../views/ProfileView.vue'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    redirect: '/feed',
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
  {
    path: '/register',
    name: 'register',
    component: LoginView,
  },
  {
    path: '/posts/me',
    name: 'my-posts',
    component: MyPostsView,
    meta: { requiresAuth: true },
  },
  {
    path: '/feed',
    name: 'feed',
    component: FeedView,
    meta: { requiresAuth: true },
  },
  {
    path: '/notifications',
    name: 'notifications',
    component: NotificationsView,
    meta: { requiresAuth: true },
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { requiresAuth: true },
  },
  {
    path: '/users/:userId',
    name: 'user-profile',
    component: ProfileView,
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.token) {
    return '/login'
  }

  if ((to.path === '/login' || to.path === '/register') && authStore.token) {
    return '/feed'
  }
})

export default router
