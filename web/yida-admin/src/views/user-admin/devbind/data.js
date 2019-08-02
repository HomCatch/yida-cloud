/*
 * label：栏目名称
 * key：对应值
 * width：宽度
 * formatter：回调函数，参数为row:该行数据，cellVal：单元格数据，返回格式化值
 */
export const tableColumn = [
  { label: "设备ID", key: "devCode" },
  { label: "绑定时间", key: "bindTime" },
  {
    label: "在线状态",
    key: "online",
    formatter: function(row, cellVal) {
      if (cellVal === 0) {
        return '<span style="color:#909399">离线</span>';
      } else if (cellVal === 1) {
        return '<span style="color:#67C23A">在线</span>';
      } else {
        return "/";
      }
    }
  }
];

/*
 * 表格功能
 * select：第一列选中框
 * del：操作栏删除功能
 * view：操作栏详情功能
 */
export const tableFunc = ["select", "unbind", "del", "mulDel"];

/*
 * 查询栏数据
 */
export const queryItem = [
  { label: "设备ID：", type: "text", key: "devCode" },
  {
    label: "在线状态：",
    type: "select",
    key: "online",
    placeholder: "请选择设备状态"
  }
];

/*
 * 存储查询栏数据
 */
// export class QueryParams {
//     constructor({ id = null }) {
//         this.id = id;
//     }
// }
