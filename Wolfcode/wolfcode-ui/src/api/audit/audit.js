import request from '@/utils/request'

// 查询套餐审核列表
export function listAudit(query) {
  return request({
    url: '/audit/audit/list',
    method: 'get',
    params: query
  })
}

// 查询套餐审核详细
export function getAudit(id) {
  return request({
    url: '/audit/audit/' + id,
    method: 'get'
  })
}

// 新增套餐审核
export function addAudit(data) {
  return request({
    url: '/audit/audit',
    method: 'post',
    data: data
  })
}

// 修改套餐审核
export function updateAudit(data) {
  return request({
    url: '/audit/audit',
    method: 'put',
    data: data
  })
}

// 删除套餐审核
export function delAudit(id) {
  return request({
    url: '/audit/audit/' + id,
    method: 'delete'
  })
}

// 我的待办
export function listTodo(params){
  return request({
    url: '/audit/audit/todoQuery',
    method: 'get',
    params
  })
}

// 审核
export function carPackageAudit(data){
  return request({
    url: '/audit/audit/audit',
    method: 'post',
    data
  })
}

// 审批历史
export function carPackageAuditHistory(instanceId) {
  return request({
    url: '/audit/audit/listHistory/' + instanceId,
    method: 'get',
  })
}

// 查看进度
export function carPackageAuditProcess(id) {
  return request({
    url: '/audit/audit/process/' + id,
    method: 'get',
  })
}

// 我的已办
export function listDone(params) {
  return request({
    url: '/audit/audit/done',
    method: 'get',
    params
  })
}
// 撤销套餐审核
export function cancelCarPackageAudit(id){
  return request({
    url: '/audit/audit/cancel/'+id,
    method: 'put'
  })
}