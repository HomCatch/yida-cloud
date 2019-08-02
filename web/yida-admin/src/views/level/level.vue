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
  </div>
</template>

<script>
import ZTable from "@/components/z-table/index";
import ZSearch from "@/components/z-search/index";
import { getList, del, getRate } from "./api.js";
import { tableColumns, searchItems, lineFuncs, funcs } from "./data.js";
import * as util from "@/common/js/util";

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
      tableLoading: false,
      searchForm: {}
    };
  },
  created() {
    getRate();
    // .then(res=>{
    //   if (res.ret === 0) {
    //     this.getList();
    //   }else{
    //     this.$message({
    //       type:"error",
    //       message:"获取列表失败"
    //     })
    //   }
    // })
    this.getList();
  },
  methods: {
    //获取列表
    getList() {
      this.tableLoading = true;
      const params = { page: this.page.currentPage, pageSize:10, ...this.searchForm };
      getList(params).then(res => {
        console.log("res", res);
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
        case "删除":
          this.del(row);
          break;
      }
    },
    // 行内功能按钮
    lineFunc({ opera, row }) {
      switch (opera) {
        case "升级详情":
          this.levelDesc(row);
          break;
        case "删除":
          this.del(row);
          break;
      }
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
    // 页码改变
    handleCurrentChange(val) {
      this.page.currentPage = val;
      this.getList();
    },
    // 升级详情
    levelDesc(row) {
      this.$router.push({
        path:"/dev/level/desc",
        query:row
      })
    }
  },
  computed: {
    funcs() {
      return funcs;
    },
    lineFuncs() {
      return lineFuncs;
    }
  },
  components: { ZTable, ZSearch }
};
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
