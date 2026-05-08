import api from './axios'

const POST_API_URL = 'http://localhost:8087'

export async function getMyPosts(page = 0, size = 15) {
  const response = await api.get(`${POST_API_URL}/api/posts/me?page=${page}&size=${size}`)
  return response.data
}

export async function getPostsByUserId(userId, page = 0, size = 15) {
  const response = await api.get(`${POST_API_URL}/api/posts/user/${userId}?page=${page}&size=${size}`)
  return response.data
}

export async function createPost(content) {
  const response = await api.post(`${POST_API_URL}/api/posts`, {
    content,
  })
  return response.data
}

export async function attachMediaToPost(postId, mediaId) {
  const response = await api.post(`${POST_API_URL}/api/posts/${postId}/media/${mediaId}`)
  return response.data
}

export async function deletePost(postId) {
  await api.delete(`${POST_API_URL}/api/posts/${postId}`)
}

export async function deletePostMedia(postId, mediaId) {
  const response = await api.delete(`${POST_API_URL}/api/posts/${postId}/media/${mediaId}`)
  return response.data
}
