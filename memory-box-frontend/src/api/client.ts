import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

const API_BASE_URL =  'https://f9dro19exc.execute-api.eu-west-1.amazonaws.com/'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export default api
