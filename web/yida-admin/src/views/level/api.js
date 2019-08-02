import http from "@/service/http";

// 列表
export function getList(data) {
  return http({
    url: `/yida/firmwareTasks/pages `,
    method: "post",
    data
  });
}

// 删除
export function del({ id }) {
  return http({
    url: `/yida/firmwareTasks/${id}`,
    method: "delete"
  });
}

export function getRate() {
  return http({
    url: `/yida/firmwareStatics/rate`,
    method: "get"
  });
}

