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
export const getPostDetail = (id, params) => {
  return request({
    url: `/posts/${id}`,
    method: 'get',
    params
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

/**
 * 点赞评论/回复
 */
export const likeComment = (commentId) => {
  return request({
    url: `/posts/comments/${commentId}/like`,
    method: 'post'
  })
}

/**
 * 回复评论
 */
export const replyComment = (postId, commentId, data) => {
  return request({
    url: `/posts/${postId}/comments/${commentId}/replies`,
    method: 'post',
    data
  })
}

