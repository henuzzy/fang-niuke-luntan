import request from './request'

/**
 * 获取帖子列表
 */
export const getPostList = (params) => {
  return request({
    url: '/posts',
    method: 'get',
    params
  })
}

/**
 * 获取帖子详情
 */
export const getPostDetail = (id) => {
  return request({
    url: `/posts/${id}`,
    method: 'get'
  })
}

/**
 * 发布帖子
 */
export const publishPost = (data) => {
  return request({
    url: '/posts',
    method: 'post',
    data
  })
}

/**
 * 评论帖子
 */
export const commentPost = (postId, data) => {
  return request({
    url: `/posts/${postId}/comments`,
    method: 'post',
    data
  })
}

/**
 * 点赞帖子
 */
export const likePost = (postId) => {
  return request({
    url: `/posts/${postId}/like`,
    method: 'post'
  })
}

