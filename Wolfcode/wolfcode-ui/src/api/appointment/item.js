import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listItem(query) {
  return request({
    url: '/appointment/item/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getItem(id) {
  return request({
    url: '/appointment/item/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addItem(data) {
  return request({
    url: '/appointment/item',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateItem(data) {
  return request({
    url: '/appointment/item',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delItem(id) {
  return request({
    url: '/appointment/item/' + id,
    method: 'delete'
  })
}
//养修服务单项上架
export function serviceItemSaleOn(params) {
  return request({
    url: '/appointment/item/saleOn/' + params.id,
    method: 'put'
  })
}

//养修服务单项下架
export function serviceItemSaleOff(params) {
  return request({
    url: '/appointment/item/saleOff/' + params.id,
    method: 'put'
  })
}
//发起审核按钮操作
export function serviceItemAuditInfo(params){
  return request({
    url: '/appointment/item/auditInfo/' +params.id,
    method: 'get'
  });
}
//审核提交操作
export function serviceItemStartAudit(params) {
  return request({
    url: '/appointment/item/startAudit',
    method: 'post',
    data: params
  })
}