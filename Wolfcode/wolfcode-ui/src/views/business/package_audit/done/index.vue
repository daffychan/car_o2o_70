<template>
  <div class="app-container">
    <!--  表格内容  -->
    <el-table
      ref="tables"
      v-loading="loading"
      :data="list"
      :default-sort="defaultSort"
      @sort-change="handleSortChange"
    >
      <el-table-column label="套餐名称" align="center" prop="serviceItemName" />
      <el-table-column
        label="套餐价格"
        align="center"
        prop="serviceItemPrice"
      />
      <el-table-column
        label="套餐备注"
        align="center"
        prop="serviceItemInfo"
        width="130"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
      />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.bus_package_audit_type"
            :value="scope.row.status"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-info"
            @click="listHistory(scope.row.id)"
            >审批历史
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!--  表格分页栏  -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <!--  审批历史对话框  -->
    <el-dialog
      title="审批历史"
      :visible.sync="historyDialog.open"
      width="1200px"
      append-to-body
    >
      <el-table
        ref="tables"
        v-loading="historyDialog.loading"
        :data="historyDialog.list"
        :default-sort="defaultSort"
      >
        <el-table-column label="任务名称" align="center" prop="taskName" />
        <el-table-column label="开始时间" align="center" prop="startTime"   width="180" />
        <el-table-column
          label="结束时间"
          align="center"
          prop="endTime"
          width="180"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="耗时"
          align="center"
          prop="durationInMillis"
          width="180"
        />
        <el-table-column
          label="审批意见"
          align="center"
          prop="comment"
          width="180"
        />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeHistoryDialog">关 闭</el-button>
      </div>
    </el-dialog>
    <!--  流程进度图对话框  -->
    <el-dialog
      title="流程进度图"
      :visible.sync="processDialog.open"
      width="1200px"
      append-to-body
    >
      <div v-html="processDialog.img"></div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeProcessDialog">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listDone,
  carPackageAuditHistory,
  carPackageAuditProcess,
} from "@/api/audit/audit.js";

export default {
  name: "Audit",
  dicts: ["bus_package_audit_type"],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 日期范围
      dateRange: [],
      // 默认排序
      defaultSort: { prop: "loginTime", order: "descending" },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        status: undefined,
      },
      // 审批历史弹窗
      historyDialog: {
        open: false,
        loading: false,
        list: [],
      },
      // 流程进度图弹窗
      processDialog: {
        open: false,
        img: "",
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 我的已办列表 */
    getList() {
      this.loading = true;
      listDone(this.queryParams).then(
        (response) => {
          this.list = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.$refs.tables.sort(this.defaultSort.prop, this.defaultSort.order);
      this.handleQuery();
    },
    /** 排序触发事件 */
    handleSortChange(column, prop, order) {
      this.queryParams.orderByColumn = column.prop;
      this.queryParams.isAsc = column.order;
      this.getList();
    },
    listHistory(id) {
      console.log(id);
        this.historyDialog.open = true;
        this.historyDialog.loading = true;
        // 请求审批历史接口的方法
        carPackageAuditHistory(id).then((res) => {
          this.historyDialog.list = res.rows;
          this.historyDialog.loading = false;
        });
      },
      viewProcess(id) {
        this.processDialog.open = true;
        carPackageAuditProcess(id).then((res) => {
          this.processDialog.img = res;
        });
      },
      closeHistoryDialog() {
        this.historyDialog.open = false;
      },
      closeProcessDialog() {
        this.processDialog.open = false;
      },
  },
};
</script>