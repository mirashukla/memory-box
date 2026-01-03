// import { defineStore } from 'pinia'

// export const useAuthStore = defineStore('auth', {
//   state: () => ({
//     accessToken: null as string | null
//   }),

//   getters: {
//     isAuthenticated: (state) => !!state.accessToken
//   },

//   actions: {
//     setAccessToken(token: string) {
//       this.accessToken = token
//     },

//     clearAccessToken() {
//       this.accessToken = null
//     }
//   }
// })
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  // State
  state: () => ({
    accessToken: localStorage.getItem('accessToken') || null as string | null
  }),

  // Getters
  getters: {
    isAuthenticated: (state) => !!state.accessToken,

    authHeader: (state) => state.accessToken ? { Authorization: `Bearer ${state.accessToken}` } : {}
  },

  // Actions
  actions: {
    setAccessToken(token: string) {
      this.accessToken = token
      localStorage.setItem('accessToken', token)
    },

    clearAccessToken() {
      this.accessToken = null
      localStorage.removeItem('accessToken')
    },

    logout() {
      this.clearAccessToken()
    }
  }
})

