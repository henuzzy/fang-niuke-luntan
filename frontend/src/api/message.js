import request from './request'

export const getConversations = (params) => {
  return request({ url: '/messages', method: 'get', params })
}

export const getLetters = (params) => {
  return request({ url: '/messages/letters', method: 'get', params })
}

export const sendMessage = (data) => {
  return request({ url: '/messages', method: 'post', params: data })
}


