// 搜索框项目
export const searchItems = [
  {
    label: "设备ID",
    value: "devCode",
    type: "input",
    placeholder: ""
  }
];

// 功能按钮
// export const funcs = ["删除"];

// 行内功能按钮
// export const lineFuncs = ["升级详情", "删除"];

// 表格项目
export const tableColumns = [
  { label: "设备ID", value: "devCode" },
  {
    label: "在线状态",
    value: "online",
    formatter: function(row) {
      if (row.online === 0) {
        return '<span style="color:#909399">离线</span>';
      } else if (row.online === 1) {
        return '<span style="color:#67C23A">在线</span>';
      } else {
        return "/";
      }
    }
  },
  {
    label: "应答时间",
    value: "reportTime",
    formatter: function(row) {
      if (!row.reportTime) {
        return "/";
      } else {
        return row.reportTime;
      }
    }
  },
  {
    label: "升级状态",
    value: "upStatus",
    formatter: function(row) {
      if (row.upStatus === 0) {
        return '<span style="color:#ed3f14">未升级</span>';
      } else if (row.upStatus === 1) {
        return '<span style="color:#67C23A">已升级</span>';
      } else{
        return '<span style="color:#ed3f14">未升级</span>';
      }
      // if (!row.upStatus) {
      //   return "/";
      // } else {
      //   return row.upStatus;
      // }
    }
  }
];
