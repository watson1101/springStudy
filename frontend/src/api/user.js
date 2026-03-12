import request from '@/utils/request'

export const register = (data) => {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export const login = (data) => {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export const logout = () => {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

export const getUserList = (params) => {
  return request({
    url: '/user/admin/list',
    method: 'get',
    params
  })
}

export const createUser = (data) => {
  return request({
    url: '/user/admin/create',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/user/admin/update',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/user/admin/delete/${id}`,
    method: 'delete'
  })
}

export const toggleUserStatus = (id) => {
  return request({
    url: `/user/admin/toggle-status/${id}`,
    method: 'put'
  })
}
