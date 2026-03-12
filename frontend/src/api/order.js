import request from '@/utils/request'

export const getOrderList = (params) => {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

export const createOrder = (data) => {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

export const updateOrder = (data) => {
  return request({
    url: '/order/update',
    method: 'put',
    data
  })
}

export const deleteOrder = (id) => {
  return request({
    url: `/order/delete/${id}`,
    method: 'delete'
  })
}

export const getOrderDetail = (id) => {
  return request({
    url: `/order/detail/${id}`,
    method: 'get'
  })
}

export const getOrderItemList = (params) => {
  return request({
    url: '/order/item/list',
    method: 'get',
    params
  })
}
