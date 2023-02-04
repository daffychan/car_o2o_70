import request from '@/utils/request'

// 查询养修预约信息列表
export function listAppointment(query) {
  return request({
    url: '/appointment/appointment/list',
    method: 'get',
    params: query
  })
}

// 查询养修预约信息详细
export function getAppointment(id) {
  return request({
    url: '/appointment/appointment/' + id,
    method: 'get'
  })
}

// 新增养修预约信息
export function addAppointment(data) {
  return request({
    url: '/appointment/appointment',
    method: 'post',
    data: data
  })
}

// 修改养修预约信息
export function updateAppointment(data) {
  return request({
    url: '/appointment/appointment',
    method: 'put',
    data: data
  })
}

// 删除养修预约信息
export function delAppointment(id) {
  return request({
    url: '/appointment/appointment/' + id,
    method: 'delete'
  })
}
// 到店
export function arrival(id) {
    return request({
      url: '/appointment/appointment/arrival/' + id,
      method: 'put'
    })
  }

  // 取消
export function cancelAppointment(id) {
    return request({
      url: '/appointment/appointment/cancel/' + id,
      method: 'put'
    })
  }
  //提交结算单
export function generateStatement(id) {
  return request({
    url: '/appointment/appointment/generate/' + id,
    method: 'post',
  })
}