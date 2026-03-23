import api from './axios'

const FEED_API_URL = 'http://localhost:8082'

export async function getFeed(page = 0, size = 10) {
  const response = await api.get(`${FEED_API_URL}/api/feed?page=${page}&size=${size}`)
  return response.data
}