<template>
  <div class="box">
    <!-- 搜索 -->
    <z-search :searchItems="searchItems" @search="search"></z-search>
    <!-- 功能+table -->
    <z-table
      :tableData="tableData"
      :tableColumns="tableColumns"
      :page="page"
      :funcs="funcs"
      @func="func"
      :lineFuncs="lineFuncs"
      @lineFunc="lineFunc"
      @handleCurrentChange="handleCurrentChange"
      v-loading="tableLoading"
    ></z-table>
    <!-- 新增/编辑dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" @close="closeDialog">
      <el-form
        :model="dialogData"
        ref="ruleForm"
        :rules="rules"
        label-width="100px"
        class="demo-ruleForm"
      >
        <!--不生成创建时间字段-->
        <el-form-item
          label="选择固件:"
          :rules="[
      { required: true, message: '', trigger: 'change' }]"
        >
          <el-upload
            class="upload-demo"
            action="/mng/yida/file/firmware"
            :headers="headers"
            :on-success="unloadSuccess"
            :before-upload="beforeUpload"
            :limit="1"
            :on-exceed="handleExceed"
            :on-remove="handleRemove"
            :before-remove="beforeRemove"
            :file-list="fileList"
          >
            <el-button size="small" type="primary">上传固件</el-button>
            <div slot="tip" class="el-upload__tip">仅支持bin类型的文件，固件大小不得超过10MB</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="固件描述:" prop="remark">
          <el-input type="textarea" v-model="dialogData.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm('ruleForm')" :loading="btnLoading">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 下发至指定设备弹窗 -->
    <el-dialog title="定向推送" :visible.sync="testVisible" @close="closeDialog">
      <el-form :model="testForm" :rules="testRules" :show-message="false">
        <el-form-item :prop="value==='1'?'phone':'devCode'">
          <el-row>
            <el-col :span="10">
              <el-radio v-model="radio" label="1" @change="change">手机号码</el-radio>
              <el-input
                placeholder="请输入有效手机号"
                v-model="testForm.phone"
                autocomplete="off"
                :disabled="value!=1"
              ></el-input>
            </el-col>
            <el-col :span="10">
              <el-radio v-model="radio" label="2" @change="change">设备ID</el-radio>
              <el-input
                placeholder="请输入15位设备ID"
                v-model="testForm.devCode"
                autocomplete="off"
                :disabled="value!=2"
              ></el-input>
            </el-col>
            <el-col :span="2">
              <el-button @click="find" icon="el-icon-search" type="primary">搜索</el-button>
            </el-col>
            <el-col :span="2">
              <el-button @click="put" icon="el-icon-caret-bottom" type="danger">推送</el-button>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item>
          <el-table
            :data="testAd"
            style="width: 100%"
            :default-sort="{prop: 'date', order: 'descending'}"
            @selection-change="handleSelectionChange"
            v-loading="loading"
          >
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="phone" label="手机号码" sortable></el-table-column>
            <el-table-column prop="devCode" label="设备ID" sortable></el-table-column>
            <el-table-column prop="online" label="在线状态" sortable>
              <template scope="scope">
                <span v-if="scope.row.online=== 0">离线</span>
                <span v-else style="color:#67C23A">在线</span>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item>
          <Page @pageChange="pageChange" :pageParams="pageParams"></Page>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import ZTable from "@/components/z-table/index";
import ZSearch from "@/components/z-search/index";
import Page from "@/components/crud/page/page";
import { getList, del, add, firmwarePut, putToDev, getDev } from "./api.js";
import { tableColumns, searchItems, lineFuncs, funcs } from "./data.js";
import * as util from "@/common/js/util";

export default {
  data() {
    let validPhone = (rule, value, callback) => {
      let reg = /^1[3|4|5|6|7|8|9][0-9]{9}$/;
      if (!reg.test(value)) {
        callback(new Error("电话号码格式有误！请重新输入"));
      } else {
        callback();
      }
    };
    let validDev = (rule, value, callback) => {
      let reg = /^\d{15}$/;
      if (!reg.test(value)) {
        callback(new Error("设备ID格式有误！请重新输入"));
      } else {
        callback();
      }
    };
    return {
      tableData: [],
      page: {
        total: 0,
        currentPage: 1
      },
      tableColumns,
      searchItems,
      tableLoading: false,
      btnLoading: false,
      searchForm: {},
      dialogTitle: "新增",
      dialogVisible: false,
      labelWidth: "100px",
      dialogData: {},
      rules: {
        remark: [
          { required: true, message: "固件描述不能为空", trigger: "blur" }
        ]
      },
      allDev: [],
      fileList: [],
      testVisible: false,
      testForm: {
        phone: "",
        devCode: ""
      },
      radio: "1",
      testAd: [],
      value: "1",
      testRules: {
        phone: { validator: validPhone, trigger: "blur" },
        devCode: { validator: validDev, trigger: "blur" }
      },
      pageParams: {
        page: 1,
        pageSize: 5,
        total: 0
      },
      sendData: {
        key: "",
        page: 1,
        pageSize: 5
      },
      putData: {
        devCodes: [],
        ossUrl: "",
        size: ""
      },
      loading: false
    };
  },
  created() {
    this.getList();
  },
  methods: {
    //获取裂变
    getList() {
      this.tableLoading = true;
      const params = { page: this.page.currentPage, ...this.searchForm };
      getList(params).then(res => {
        // console.log("res", res);
        if (res.ret === 0) {
          this.tableLoading = false;
          this.tableData = res.datas.list;
          this.page.total = res.datas.itemCounts;
          if (
            res.datas.list &&
            res.datas.list.length == 0 &&
            this.page.currentPage != 1
          ) {
            this.page.currentPage--;
            this.getList();
          }
        } else {
          this.$message({
            type: "error",
            message: res.msg,
            duration: 1000
          });
          this.tableLoading = false;
        }
      });
    },
    //条件查询
    search(searchForm) {
      this.page.currentPage = 1;
      this.searchForm = searchForm;
      this.getList();
    },
    // 功能按钮
    func({ opera, row }) {
      switch (opera) {
        case "新增":
          this.add();
          break;
        case "删除":
          this.del(row);
          break;
      }
    },
    // 行内功能按钮
    lineFunc({ opera, row }) {
      switch (opera) {
        case "全量推送":
          this.firmwarePut(row);
          break;
        case "定向推送":
          this.putDev(row);
          break;
        case "删除":
          this.del(row);
          break;
      }
    },
    // 删除文件
    handleRemove(file, fileList) {
      console.log("删除", file, fileList);
      this.fileList = [];
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`);
    },
    // 限制文件
    handleExceed(files, fileList) {
      this.$message.warning(
        `当前限制选择 1 个文件，本次选择了 ${
          files.length
        } 个文件，共选择了 ${files.length + fileList.length} 个文件`
      );
    },
    // 文件格式校验
    beforeUpload(file) {
      // console.log(file);
      // 文件格式校验
      const isBin = file.type === "application/octet-stream";
      const is10M = file.size / 1024 / 1024 < 100;
      if (!isBin) {
        this.$message.error("上传固件只能是 bin 格式!");
      }
      if (!is10M) {
        this.$message.error("上传固件大小不能超过 10MB!");
      }
      return isBin && is10M;
    },
    // 上传固件
    unloadSuccess(res, file) {
      console.log(res, file);
      if (res.ret === 0) {
        if (this.fileList.length < 1) {
          let binObj = {};
          binObj.name = file.name;
          binObj.url = res.datas.ossUrl;
          this.fileList.push(binObj);
        }
        let fileArr = file.name.split("_");
        this.dialogData.ossUrl = res.datas.ossUrl;
        this.dialogData.size = res.datas.size;
        this.dialogData.name = fileArr[0];
        this.dialogData.version = fileArr[1];
      } else {
        this.$message({
          message: res.msg,
          type: "error"
        });
      }
    },
    // 新增
    add() {
      this.dialogTitle = "新增";
      this.dialogData = {};
      this.fileList = [];
      this.dialogVisible = true;
    },
    // 删除
    del(row) {
      // console.log(row);
      if (row.id) {
        row = row.id;
      } else {
        row = row.map(item => item.id);
      }
      this.$confirm("此操作将永久删除所选数据, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          del({ id: row }).then(res => {
            // console.log("shanchu ");
            this.$message({
              message: res.ret === 0 ? "删除成功" : res.msg,
              type: res.ret === 0 ? "success" : "error"
            });
            this.getList();
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },
    // 全量推送
    firmwarePut(row) {
      // console.log(row);
      this.$confirm(
        "<h1 style='font-size:20px;text-align:center;color:#333'>您确定全量推送吗？</h1><p style='text-align:center;color:#333'>为确保固件稳定性,请在全量升级前针对多台设备进行测试。</p>",
        "全量推送",
        {
          confirmButtonText: "推送",
          cancelButtonText: "取消",
          dangerouslyUseHTMLString: true,
          showClose: false
          // center:true
        }
      )
        .then(() => {
          firmwarePut(row.id).then(res => {
            // console.log(res);
            this.$message({
              message: res.ret === 0 ? "固件已推送" : res.msg,
              type: res.ret === 0 ? "success" : "error"
            });
            this.getList();
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消"
          });
        });
    },
    // 定向推送
    putDev(row) {
      // console.log(row);
      this.testVisible = true;
      this.putData.ossUrl = row.ossUrl;
      this.putData.size = row.size;
    },
    // 页码改变
    handleCurrentChange(val) {
      this.page.currentPage = val;
      this.getList();
    },
    change(value) {
      this.value = value;
      if (value === "1") {
        this.testForm.devCode = "";
      } else {
        this.testForm.phone = "";
      }
    },
    pageChange(page) {
      // console.log(page);
      this.sendData.page = page;
      this.find();
    },
    handleSelectionChange(row) {
      // console.log(row);
      let arr = [];
      row.forEach(v => {
        arr.push(v.devCode);
      });
      this.putData.devCodes = arr;
    },
    put() {
      if (this.putData.devCodes.length) {
        putToDev(this.putData).then(res => {
          // console.log(res);
          this.$message({
            type: res.ret === 0 ? "success" : "error",
            message: res.ret === 0 ? "固件已推送" : res.msg
          });
          this.testVisible = false;
        });
      } else {
        this.$message({
          type: "warning",
          message: "请选择指定的设备"
        });
        return;
      }
    },
    find() {
      this.loading = true;
      this.testAd = [];
      if (this.testForm.phone) {
        this.sendData.key = this.testForm.phone;
      } else if (this.testForm.devCode) {
        this.sendData.key = this.testForm.devCode;
      } else {
        this.$message({
          message: "手机号或设备ID不能为空",
          type: "error"
        });
        this.loading = false;
        return false;
      }
      getDev(this.sendData).then(res => {
        // console.log(res);
        if (res.ret === 0) {
          this.testAd = res.datas.list;
          this.pageParams.total = res.datas.itemCounts;
          this.loading = false;
        } else {
          if (this.testForm.phone) {
            this.$message({
              message: "电话号码输入格式有误，请重新输入合法的电话号码",
              type: "error"
            });
            this.loading = false;
          } else if (this.testForm.devCode) {
            this.$message({
              message: "设备ID输入格式有误，请重新输入15位数字的设备ID",
              type: "error"
            });
            this.loading = false;
          } else {
            return false;
          }
        }
      });
    },
    // 表单提交
    submitForm(formName) {
      //不解析下面代码
      this.$refs[formName].validate(valid => {
        if (valid) {
          const params = this.dialogData;
          console.log(this.fileList.length);
          if (this.fileList.length === 0) {
            this.$message({
              type: "error",
              message: "请选择固件上传"
            });
          } else {
            add(params).then(res => {
              if (res.ret == 0) {
                this.$message({
                  message: "新增成功",
                  type: "success"
                });
                this.dialogVisible = false;
                this.getList();
                this.fileList = [];
              } else {
                this.$message({
                  message: res.msg,
                  type: "error"
                });
              }
            });
          }
        } else {
          return false;
        }
      });
    },
    // 关闭弹框
    closeDialog() {
      this.fileList = [];
    }
  },
  computed: {
    funcs() {
      return funcs;
    },
    lineFuncs() {
      return lineFuncs;
    },
    headers() {
      return {
        token: localStorage.getItem("token")
      };
    }
  },
  components: { ZTable, ZSearch, Page }
};
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
