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
      :lineFuncs="[]"
      @func="func"
      @handleCurrentChange="handleCurrentChange"
      v-loading="tableLoading"
    ></z-table>
    <!-- 新增/编辑dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible">
      <el-form :model="dialogData" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <!--不生成创建时间字段-->
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="dialogData.userName"></el-input>
        </el-form-item>
        <el-form-item label="操作类型:" prop="operateType">
          <el-input v-model="dialogData.operateType"></el-input>
        </el-form-item>
        <el-form-item label="状态:" prop="status">
          <el-select v-model="dialogData.status">
            <el-option :value="0" label="失败"></el-option>
            <el-option :value="1" label="成功"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="操作IP:" prop="ip">
          <el-input v-model="dialogData.ip"></el-input>
        </el-form-item>
        <el-form-item label="用户代理:" prop="userAgent">
          <el-input v-model="dialogData.userAgent"></el-input>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="dialogData.startTime"
            type="datetime"
            placeholder="选择时间"
            value-format="yyyy-MM-dd HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="dialogData.endTime"
            type="datetime"
            placeholder="选择时间"
            value-format="yyyy-MM-dd HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm('ruleForm')" :loading="btnLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import ZTable from "@/components/z-table/index";
import ZSearch from "@/components/z-search/index";
import { getList, del, add, edit } from "./api.js";
import { tableColumns, searchItems, funcs } from "./data";
export default {
  data() {
    return {
      tableData: [],
      page: {
        total: 0,
        currentPage: 1
      },
      tableColumns,
      searchItems,
      funcs,
      tableLoading: false,
      btnLoading: false,
      searchForm: {},
      dialogTitle: "新增",
      dialogVisible: false,
      labelWidth: "100px",
      dialogData: {
        schemalist: []
      },
      allDev: []
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.tableLoading = true;
      const params = {
        page: this.page.currentPage,
        ...this.searchForm
      };
      getList(params).then(
        res => {
          this.tableLoading = false;
          this.tableData = res.datas.list;
          this.page.total = res.datas.itemCounts;
        },
        () => {
          this.$message({
            message: "获取列表失败",
            type: "error"
          });
          this.tableLoading = false;
        }
      );
    },
    search(searchForm) {
      if (searchForm.startTime > searchForm.endTime) {
        this.$message({
          message: "结束时间不应早于开始时间",
          type: "error"
        });
        return;
      }
      this.page.currentPage = 1;
      this.searchForm = searchForm;
      this.getList();
    },
    func({ opera, row }) {
      switch (opera) {
        case "修改":
          this.edit(row);
          break;
        case "新增":
          this.add();
          break;
        case "删除":
          this.del(row);
          break;
      }
    },
    add() {
      this.dialogTitle = "新增";
      this.dialogData = {};
      this.dialogVisible = true;
    },
    edit(row) {
      this.dialogTitle = "编辑";
      this.dialogData = { ...row };
      this.dialogVisible = true;
    },
    del(row) {
      this.$confirm("此操作将永久删除所选数据, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          del({ id: row.id }).then(res => {
            console.log("shanchu ");
            this.$message({
              message: res.data.ret === 0 ? "删除成功" : res.data.msg,
              type: res.data.ret === 0 ? "success" : "error"
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
    handleCurrentChange(val) {
      this.page.currentPage = val;
      this.getList();
    },
    submitForm(formName) {
      //不解析下面代码

      this.$refs[formName].validate(valid => {
        if (valid) {
          const params = this.dialogData;
          if (this.dialogTitle === "编辑") {
            edit({
              id: this.dialogData.id,
              data: { ...this.dialogData }
            }).then(res => {
              if (res.data.ret == 0) {
                this.$message({
                  message: "修改成功",
                  type: "success"
                });
                this.dialogVisible = false;
                this.getList();
              } else {
                this.$message({
                  message: res.data.msg,
                  type: "error"
                });
              }
            });
          } else if (this.dialogTitle === "新增") {
            add(params).then(res => {
              if (res.data.ret == 0) {
                this.$message({
                  message: "新增成功",
                  type: "success"
                });
                this.dialogVisible = false;
                this.getList();
              } else {
                this.$message({
                  message: res.data.msg,
                  type: "error"
                });
              }
            });
          }
        } else {
          return false;
        }
      });
    }
  },
  computed: {
    // funcs() {
    //     return this.$store.state.user.funcs;
    // }
  },
  components: { ZTable, ZSearch }
};
</script>

<style scoped lang="less">
@import "./index.less";
</style>
