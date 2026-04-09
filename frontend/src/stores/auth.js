import { defineStore } from 'pinia'
import { decodeJwtPayload } from '../utils/formatters'
import { loginRequest, registerRequest } from '../api/authApi'

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
    },

    logout() {
      this.token = ''
      localStorage.removeItem('token')
    },
  },
})
