<template>
  <div class="box">
    <!-- 搜索 -->
    <z-search :searchItems="searchItems" @search="search"></z-search>
    <!-- 功能+table -->
    <z-table
      :tableData="tableData"
      :tableColumns="tableColumns"
      :page="page"
      @handleCurrentChange="handleCurrentChange"
      v-loading="tableLoading"
    ></z-table>
  </div>
</template>

<script>
import ZTable from "@/components/z-table/index";
import ZSearch from "@/components/z-search/index";
import { getList, del } from "./api.js";
import { tableColumns, searchItems } from "./data.js";

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
    this.getList();
  },
  methods: {
    //获取列表
    getList() {
      this.tableLoading = true;
      const params = { page: this.page.currentPage, pageSize:10, fmId:this.$route.query.fmId, ...this.searchForm };
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
    // 页码改变
    handleCurrentChange(val) {
      this.page.currentPage = val;
      this.getList();
    }
  },
  components: { ZTable, ZSearch }
};
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
