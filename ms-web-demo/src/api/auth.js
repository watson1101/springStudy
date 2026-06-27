import request from './index'

/**
 * 用户登录
 * POST  /user-service/user/login
 */
export function loginAPI(data) {
  return request({
    url: '/user-service/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * POST  /user-service/user/register
 */
export function registerAPI(data) {
  return request({
    url: '/user-service/user/register',
    method: 'post',
    data
  })
}

/**
 * 获取当前用户信息
 * GET  /user-service/user/info
 */
export function getUserInfoAPI() {
  return request({
    url: '/user-service/user/info',
    method: 'get'
  })
}
