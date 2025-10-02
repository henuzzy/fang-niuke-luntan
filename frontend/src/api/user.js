import request from './request'

/**
 * 用户注册
 */
export const register = (data) => {
  return request({
    url: '/users/register',
    method: 'post',
    data
  })
}

/**
 * 用户登录
 */
export const login = (data) => {
  return request({
    url: '/users/login',
    method: 'post',
    data
  })
}

/**
 * 退出登录
 */
export const logout = (ticket) => {
  return request({
    url: '/users/logout',
    method: 'post',
    params: { ticket }
  })
}

/**
 * 获取用户信息
 */
export const getUserProfile = (userId) => {
  return request({
    url: `/users/${userId}/profile`,
    method: 'get'
  })
}

// 获取验证码(base64)
export const getCaptchaBase64 = () => {
  return request({
    url: '/captcha/base64',
    method: 'get'
  })
}

/**
 * 修改密码
 */
export const updatePassword = (userId, data) => {
  return request({
    url: `/users/password/${userId}`,
    method: 'put',
    data
  })
}

/**
 * 上传头像（传 headerUrl 字符串，可为文件上传后的URL或Base64）
 */
export const uploadAvatar = (userId, headerUrl) => {
  return request({
    url: `/users/avatar/${userId}`,
    method: 'post',
    params: { headerUrl }
  })
}

/**
 * 关注用户
 */
export const followUser = (userId) => {
  return request({
    url: `/users/follow/${userId}`,
    method: 'post'
  })
}

/**
 * 取消关注用户
 */
export const unfollowUser = (userId) => {
  return request({
    url: `/users/unfollow/${userId}`,
    method: 'post'
  })
}

