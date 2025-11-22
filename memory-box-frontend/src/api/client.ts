import axios from 'axios'

const API_BASE_URL = 'https://wfvv0oiane.execute-api.eu-west-1.amazonaws.com'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Optional: add JWT token automatically if stored
// api.interceptors.request.use(config => {
//   const token = localStorage.getItem('jwt')
//   if (token) {
//     config.headers['Authorization'] = `Bearer ${token}`
//   }
//   return config
// })

export default api
