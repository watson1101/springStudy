import request from '@/utils/request'

export const getGoodsList = (params) => {
  return request({
    url: '/goods/list',
    method: 'get',
    params
  })
}

export const createGoods = (data) => {
  return request({
    url: '/goods/create',
    method: 'post',
    data
  })
}

export const updateGoods = (data) => {
  return request({
    url: '/goods/update',
    method: 'put',
    data
  })
}

export const deleteGoods = (id) => {
  return request({
    url: `/goods/delete/${id}`,
    method: 'delete'
  })
}

export const getGoodsPriceList = (params) => {
  return request({
    url: '/goods/price/list',
    method: 'get',
    params
  })
}

export const createGoodsPrice = (data) => {
  return request({
    url: '/goods/price/create',
    method: 'post',
    data
  })
}
