import http from "@/service/http";

// 列表
export function getList(data) {
  return http({
    url: `/yida/firmwareStatics/pages`,
    method: "post",
    data
  });
}

