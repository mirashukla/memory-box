import axios from 'axios'

const API_BASE_URL =  'https://b9wyuzm3l6.execute-api.eu-west-1.amazonaws.com/'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export default api
