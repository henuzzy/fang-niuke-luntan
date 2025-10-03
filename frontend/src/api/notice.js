import request from './request'

export const getNoticeSummary = () => {
  return request({ url: '/notices/summary', method: 'get' })
}

export const getNoticeList = (topic, params) => {
  return request({ url: `/notices/${topic}`, method: 'get', params })
}


