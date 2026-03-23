import api from './axios'

const COMMENT_API_URL = 'http://localhost:8088'

export async function getCommentsByPostId(postId) {
  const response = await api.get(`${COMMENT_API_URL}/api/comments/post/${postId}`)
  return response.data
}

export async function createComment(postId, content) {
  const response = await api.post(`${COMMENT_API_URL}/api/comments`, {
    postId,
    content,
  })
  return response.data
}

export async function deleteComment(commentId) {
  await api.delete(`${COMMENT_API_URL}/api/comments/${commentId}`)
}