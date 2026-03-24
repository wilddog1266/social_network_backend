import api from './axios'

const NOTIFICATION_API_URL = 'http://localhost:8086'

export async function getNotifications(page = 0, size = 15) {
  const response = await api.get(
    `${NOTIFICATION_API_URL}/api/notifications?page=${page}&size=${size}`
  )
  return response.data
}

export async function markNotificationAsRead(id) {
  await api.patch(`${NOTIFICATION_API_URL}/api/notifications/${id}/read`)
}

export async function markAllNotificationsAsRead() {
  await api.patch(`${NOTIFICATION_API_URL}/api/notifications/read-all`)
}