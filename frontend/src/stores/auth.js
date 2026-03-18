import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
  }),

  actions: {
    async login(username, password) {
      const response = await axios.post('http://localhost:8081/api/auth/login', {
        username,
        password,
      })

      this.token = response.data.accessToken
      localStorage.setItem('token', this.token)
    },

    logout() {
      this.token = ''
      localStorage.removeItem('token')
    },
  },
})