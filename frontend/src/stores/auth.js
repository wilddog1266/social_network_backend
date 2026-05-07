import { defineStore } from 'pinia'
import { decodeJwtPayload } from '../utils/formatters'
import { loginRequest, registerRequest } from '../api/authApi'
import { createProfile } from '../api/userProfileApi'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
  }),

  getters: {
    profile(state) {
      return decodeJwtPayload(state.token) ?? {}
    },
    username() {
      return this.profile.username || ''
    },
    userId() {
      return this.profile.sub || ''
    },
  },

  actions: {
    async login(username, password) {
      const response = await loginRequest(username, password)
      this.token = response.accessToken
      localStorage.setItem('token', this.token)
    },

    async register(payload) {
      const response = await registerRequest(payload)
      this.token = response.accessToken
      localStorage.setItem('token', this.token)

      try {
        await createProfile({
          displayName: payload.username.trim(),
          bio: '',
          avatarId: null,
        })
      } catch (error) {
        if (error?.response?.status !== 409) {
          throw error
        }
      }
    },

    logout() {
      this.token = ''
      localStorage.removeItem('token')
    },
  },
})
