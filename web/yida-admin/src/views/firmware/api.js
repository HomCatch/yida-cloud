import http from "@/service/http";

// 列表
export function getList(data) {
  return http({
    url: `/yida/firmwares/pages`,
    method: "post",
    data
  });
}

// 新增
export function add(data) {
  return http({
    url: `/yida/firmwares/`,
    method: "post",
    data
  });
}

// 修改
export function edit({ id, data }) {
  return http({
    url: `/yida/firmwares/${id}`,
    method: "put",
    data
  });
}

// 删除
export function del({ id }) {
  return http({
    url: `/yida/firmwares/${id}`,
    method: "delete"
  });
}

// 详情
export function getDetail({ id }) {
  return http({
    url: `/yida/firmwares/${id}`,
    method: "get"
  });
}

//固件下发;
export function firmwarePut(firmId) {
  return http({
    url: `/yida/firmwares/broadcast/${firmId}`,
    method: "get"
  });
}

// 下发至指定设备
export function putToDev(data) {
  return http({
    url: `/yida/firmwares/send/dev`,
    method: "post",
    data
  });
}

// 根据特定条件查询
export function getDev(params) {
  return http({
    url: `/yida/dev/search/msg`,
    method: "get",
    params
  });
}
