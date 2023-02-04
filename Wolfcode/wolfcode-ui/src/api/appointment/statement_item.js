import request from '@/utils/request'

// 查询明细单列表
export function listStatementItem(query) {
  return request({
    url: '/appointment/statementitem/list',
    method: 'get',
    params: query
  })
}

// 查询明细单详细
export function getItem(id) {
  return request({
    url: '/appointment/statementitem/' + id,
    method: 'get'
  })
}

// 新增明细单
export function addStatementItem(data) {
  return request({
    url: '/appointment/statementitem',
    method: 'post',
    data: data
  })
}
// 支付
export function payStatement(data) {
  return request({
    url: '/appointment/statementitem/pay',
    method: 'post',
    data: data
  })
}

// 修改明细单
export function updateItem(data) {
  return request({
    url: '/appointment/statementitem',
    method: 'put',
    data: data
  })
}

// 删除明细单
export function delItem(id) {
  return request({
    url: '/appointment/statementitem/' + id,
    method: 'delete'
  })
}
