<template>
  <div id="boxbox">
    <div class="top">
      <div class="dev">
        <div class="left">
          <h3>总设备(台)</h3>
          <p>{{totalData.totalCount}}</p>
          <div class="lifting">
            <span :class="{noAdd:distance.total<0}">
              <img v-if="distance.total>=0" src="@/common/img/icon_rise.png" alt />
              <img v-else src="@/common/img/icon_decline.png" alt />
              {{rate.totalRate}}
            </span>
            <span>{{distance.total}}</span>
          </div>
        </div>
        <div class="center">
          <h3>绑定设备(台)</h3>
          <p>{{totalData.statusCount}}</p>
          <div class="lifting">
            <span :class="{noAdd:distance.bind<0}">
              <img v-if="distance.bind>=0" src="@/common/img/icon_rise.png" alt />
              <img v-else src="@/common/img/icon_decline.png" alt />
              {{rate.bindRate}}
            </span>
            <span>{{distance.bind}}</span>
          </div>
        </div>
        <div class="right">
          <img src="@/common/img/icon_device.png" alt />
        </div>
      </div>
      <div class="isLine">
        <div class="left">
          <h3>
            在线
            <img
              style="margin-left:6px;vertical-align:middle"
              src="@/common/img/icon_online.png"
              alt
            />
          </h3>
          <p>{{totalData.onlineCount}}</p>
          <span>{{rate.onRate}}%</span>
        </div>
        <div class="echarts">
          <v-echart :options="isLineOption" style="width:100%;hight:100%"></v-echart>
        </div>
        <div class="right">
          <h3>
            <img
              style="margin-right:6px;vertical-align:middle"
              src="@/common/img/icon_offline.png"
              alt
            />离线
          </h3>
          <p>{{totalData.outlineCount}}</p>
          <span>{{rate.offRate}}%</span>
        </div>
      </div>
      <div class="wechat">
        <div class="left">
          <h3>微信用户(个)</h3>
          <p>{{totalData.userNum}}</p>
          <div class="lifting">
            <span :class="{noAdd:distance.addNum<0}">
              <img v-if="totalData.addNum>=0" src="@/common/img/icon_rise.png" alt />
              <img v-else src="@/common/img/icon_decline.png" alt />
              {{rate.userRate}}
            </span>
          </div>
        </div>
        <div class="center">
          <h3>今日新增(个)</h3>
          <p>{{totalData.addNum}}</p>
          <div class="lifting">
            <span></span>
          </div>
        </div>
        <div class="right">
          <img src="@/common/img/icon_user.png" alt />
        </div>
      </div>
    </div>
    <div class="trendMap">
      <div class="title">
        <h3>趋势图</h3>
        <div class="date">
          <ul>
            <li>
              <a :class="{active:isClicked}" href="javascript:void(0)">7天</a>
            </li>
            <li>
              <a href="javascript:void(0)">14天</a>
            </li>
            <li>
              <a href="javascript:void(0)">30天</a>
            </li>
          </ul>
          <el-date-picker
            v-model="value"
            type="daterange"
            value-format="yyyy-MM-dd HH:mm:ss"
            align="right"
            unlink-panels
            :picker-options="pickerOptions"
            @change="changeDate"
            :clearable="false"
            range-separator="至"
            :start-placeholder="start"
            :end-placeholder="end"
          ></el-date-picker>
        </div>
      </div>
      <el-menu
        :default-active="activeIndex"
        class="el-menu-demo"
        mode="horizontal"
        @select="handleSelect"
      >
        <el-menu-item index="bindDev">绑定设备（每日）</el-menu-item>
        <el-menu-item index="wechatUser">微信用户（每日）</el-menu-item>
      </el-menu>
      <v-echart :options="option" style="width:100%;height:240px"></v-echart>
    </div>
  </div>
</template>

<script>
import echarts from "echarts";
import Echarts from "vue-echarts/components/ECharts";
import { geneOptions, isLineData } from "./data";
import { getUser, getTotal, getRegister, getBindDev } from "./api";
export default {
  data() {
    return {
      isLineOption: {},
      value: null,
      start: "",
      end: "",
      activeIndex: "bindDev",
      totalData: {
        onlineCount: "",
        outlineCount: "",
        totalCount: "",
        statusCount: "",
        userNum: "",
        addNum: ""
      },
      rate: {
        offRate: "",
        onRate: "",
        totalRate: "",
        bindRate: "",
        userRate: ""
      },
      distance: {
        total: "",
        bind: "",
        user: ""
      },
      x: [],
      y: [],
      date: [],
      name: "设备绑定",
      devList: [],
      userList: [],
      option: {},
      params: {},
      pickerOptions: {
        disabledDate(time) {
          let curDate = new Date().getTime();
          let three = 90 * 24 * 3600 * 1000;
          let threeMonths = curDate - three;
          return time.getTime() > Date.now() || time.getTime() < threeMonths;
        }
      },
      isClicked: true
    };
  },
  created() {
    this.getDevNum();
  },
  methods: {
    //获取设备总览数据
    getDevNum() {
      getTotal().then(res => {
        console.log(res);
        if (res.ret === 0) {
          // 设备在线离线
          this.totalData.onlineCount = res.datas.devOverview.onlineCount;
          this.totalData.outlineCount =
            res.datas.devOverview.totalCount -
            res.datas.devOverview.onlineCount;
          this.rate.offRate =
            (
              this.totalData.outlineCount / res.datas.devOverview.totalCount
            ).toFixed(2) * 100;
          this.rate.onRate =
            (
              this.totalData.onlineCount / res.datas.devOverview.totalCount
            ).toFixed(2) * 100;
          this.isLineOption = isLineData(
            this.totalData.onlineCount,
            this.totalData.outlineCount
          );
          this.devList = res.datas.devDays;
          this.userList = res.datas.userDays;
          this.getOption(this.devList);

          // 总设备数 绑定设备数
          let totalLen = this.devList.length;
          let userLen = this.userList.length;
          let todayObj = this.devList[totalLen - 1];
          let yesterdayObj = this.devList[totalLen - 2];
          this.totalData.totalCount = this.devList[totalLen - 1].totalCount;
          this.distance.total = this.addNumber(
            todayObj.totalCount,
            yesterdayObj.totalCount
          );
          this.rate.totalRate = this.formatRate(
            this.distance.total,
            this.totalData.totalCount
          );
          this.totalData.statusCount = this.devList[totalLen - 1].statusCount;
          this.distance.bind = this.addNumber(
            todayObj.statusCount,
            yesterdayObj.statusCount
          );
          this.rate.bindRate = this.formatRate(
            this.distance.bind,
            this.totalData.statusCount
          );
          this.totalData.userNum = this.userList[userLen - 1].totalNum;
          this.totalData.addNum = this.userList[userLen - 1].dailyNum;
          this.rate.userRate = this.formatRate(
            this.totalData.addNum,
            this.totalData.userNum
          );
        } else {
          this.$message({
            type: "error",
            message: "获取数据失败"
          });
        }
      });
    },
    // 增长数值
    addNumber(today, yesterday) {
      let distance = today - yesterday;
      if (distance >= 0) {
        return `+${distance}`;
      } else {
        return `${distance}`;
      }
    },
    // 格式化增长率
    formatRate(distance, total) {
      if (total === 0) {
        return "0%";
      }
      let rate = (Math.abs(distance) / total).toFixed(3) * 100;
      return `${rate}%`;
    },
    // 获取对应的option
    getOption(data) {
      data.forEach(v => {
        if (v.date) {
          this.date.push(v.date);
          this.x.push(v.date);
          this.y.push(v.statusCount);
        } else {
          this.date.push(v.countTime);
          this.x.push(v.countTime);
          this.y.push(v.dailyNum);
        }
      });
      this.option = geneOptions(this.x, this.y, this.name);
      this.start = this.date.shift();
      this.end = this.date.pop();
    },
    changeDate() {
      this.params.startTime = this.value[0];
      this.params.endTime = this.value[1];
      this.x = [];
      this.y = [];
      this.date = [];
      this.isClicked = false;
      if (this.name === "设备绑定") {
        getBindDev(this.params).then(res => {
          console.log("bind", res);
          if (res.ret === 0) {
            this.getOption(res.datas);
          }
        });
      } else {
        getRegister(this.params).then(res => {
          console.log("reg", res);
          if (res.ret === 0) {
            this.getOption(res.datas);
          }
        });
      }
    },
    handleSelect(value) {
      this.x = [];
      this.y = [];
      this.date = [];
      this.value = null;
      this.isClicked = true;
      if (value === "bindDev") {
        this.name = "绑定设备";
        this.getOption(this.devList);
      } else {
        this.name = "微信用户";
        this.getOption(this.userList);
      }
    }
  },
  components: {
    "v-echart": Echarts
  }
};
</script>

<style lang="less" scoped>
@import "./index.less";
</style>
