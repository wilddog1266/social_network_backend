import api from './axios'

const REACTION_API_URL = 'http://localhost:8090'

export async function getPostReactionSummary(postId) {
  const response = await api.get(`${REACTION_API_URL}/api/reactions/posts/${postId}`)
  return response.data
}

export async function likePost(postId) {
  const response = await api.post(`${REACTION_API_URL}/api/reactions/posts/${postId}/like`)
  return response.data
}

export async function dislikePost(postId) {
  const response = await api.post(`${REACTION_API_URL}/api/reactions/posts/${postId}/dislike`)
  return response.data
}

export async function removePostReaction(postId) {
  const response = await api.delete(`${REACTION_API_URL}/api/reactions/posts/${postId}`)
  return response.data
}