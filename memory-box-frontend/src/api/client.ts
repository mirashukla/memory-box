import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

const API_BASE_URL =  'https://f9dro19exc.execute-api.eu-west-1.amazonaws.com/'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(config => {
  const auth = useAuthStore()

  if (auth.accessToken) {
    config.headers.Authorization = `Bearer ${auth.accessToken}`
  }

  return config
})

api.interceptors.response.use(
  res => res,
  err => {
    if (err?.response?.status === 401) {
      const auth = useAuthStore()
      auth.clearAccessToken()
    }
    return Promise.reject(err)
  }
)

export default api
