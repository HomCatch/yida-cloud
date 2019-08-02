export const breadMenu = {
  "/total/admin": [{ name: "设备总览" }],
  "/user/admin": [{ name: "用户管理" }],
  "/user/mp": [{ name: "用户管理" }, { name: "微信小程序" }],
  "/user/info": [{ name: "用户管理" }, { name: "用户信息" }],
  "/dev/admin": [{ name: "设备管理" }, { name: "净水器" }],
  "/dev/filter": [{ name: "设备管理" }, { name: "滤芯" }],
  "/dev/firmware": [{ name: "设备管理" }, { name: "固件管理" }],
  "/dev/level": [{ name: "设备管理" }, { name: "升级任务" }],
  "/filter/admin": [{ name: "滤芯管理" }],
  "/serve/banner": [{ name: "增值服务管理" }, { name: "小程序banner" }],
  "/serve/goods": [{ name: "增值服务管理" }, { name: "小程序滤芯购买" }],
  "/serve/ad": [{ name: "增值服务管理" }, { name: "设备待机广告" }],
  "/user/devbind": [
    { name: "用户管理", path: "/user" },
    { name: "微信小程序", path: "/user/mp" },
    { name: "设备管理" }
  ],
  "/serve/banner/edit": [
    { name: "增值服务管理" },
    { name: "小程序banner", path: "/serve/banner" },
    { name: "小程序banner编辑" }
  ],
  "/serve/ad/edit": [
    { name: "增值服务管理" },
    { name: "设备待机广告", path: "/serve/ad" },
    { name: "设备待机广告编辑" }
  ],
  "/serve/goods/edit": [
    { name: "增值服务管理" },
    { name: "小程序滤芯购买", path: "/serve/goods" },
    { name: "小程序滤芯购买编辑" }
  ],
  "/serve/ad-reply": [
    { name: "增值服务管理" },
    { name: "广告应答", path: "/serve/ad-reply" }
  ],
  "/log/loginLog": [{ name: "日志管理" }, { name: "登录日志" }],
  "/log/operateLog": [{ name: "日志管理" }, { name: "操作日志" }],
  "/log/errorLog": [{ name: "日志管理" }, { name: "异常日志" }],
  "/system/role": [{ name: "设置" }, { name: "角色管理" }],
  "/system/account": [{ name: "设置" }, { name: "账号管理" }]
};
