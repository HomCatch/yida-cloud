// 搜索框项目
export const searchItems = [
  {
    label: "固件名称",
    value: "name",
    type: "input",
    placeholder: "固件名称"
  }
];

// 功能按钮
export const funcs = ["新增", "删除"];

// 行内功能按钮
export const lineFuncs = ["全量推送", "定向推送", "删除"];

// 表格项目
export const tableColumns = [
  { label: "固件名称", value: "name" },
  { label: "版本号", value: "version" },
  // { label: "固件下载地址", value: "ossUrl" },
  {
    label: "固件描述",
    value: "remark",
    formatter: function(row) {
      if (!row.remark) {
        return "/";
      } else {
        return row.remark;
      }
    }
  },
  { label: "创建时间", value: "uploadTime" }
];
