import request from '@/utils/request'

// 查询流程定义列表
export function listInfo(query) {
  return request({
    url: '/flowdefinition/info/list',
    method: 'get',
    params: query
  })
}

// 查询流程定义详细
export function getInfo(id) {
  return request({
    url: '/flowdefinition/info/' + id,
    method: 'get'
  })
}

// 新增流程定义
export function addInfo(data) {
  return request({
    url: '/flowdefinition/info',
    method: 'post',
    data: data
  })
}

// 修改流程定义
export function updateInfo(data) {
  return request({
    url: '/flowdefinition/info',
    method: 'put',
    data: data
  })
}

// 删除流程定义
export function delInfo(id) {
  return request({
    url: '/flowdefinition/info/' + id,
    method: 'delete'
  })
}
// 审批流程文件提交
export function deployBpmnInfo(data){
    return request(
      {
        url:"/flowdefinition/info/deploy",
        method: 'post',
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        data:data
      }
    )
  }
  // 查看流程文件和图片
export function getBpmnInfoFile(params){
    return request({
      url:`/flowdefinition/info/read/${params.type}/${params.id}`,
      method:'get'
    })
  }