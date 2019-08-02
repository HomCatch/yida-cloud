// 搜索框项目
export const searchItems = [
  {
    label: "固件名称",
    value: "fmName",
    type: "input",
    placeholder: "固件名称"
  }
];

// 功能按钮
export const funcs = ["删除"];

// 行内功能按钮
export const lineFuncs = ["升级详情", "删除"];

// 表格项目
export const tableColumns = [
  { label: "固件名称", value: "fmName" },
  {
    label: "版本号",
    value: "fmVersion",
    formatter: function(row) {
      if (!row.fmVersion) {
        return "/";
      } else {
        return row.fmVersion;
      }
    }
  },
  {
    label: "推送时间",
    value: "pushTime",
    formatter: function(row) {
      if (!row.pushTime) {
        return "/";
      } else {
        return row.pushTime;
      }
    }
  },
  {
    label: "推送成功率",
    value: "pushRate",
    formatter: function(row) {
      if (row.pushRate) {
        return `${row.pushRate}%`;
      } else {
        return "/";
      }
    }
  }
];
