<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="客户姓名" prop="customerName">
        <el-input
          v-model="queryParams.customerName"
          placeholder="客户姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="电话号码" prop="customerPhone">
        <el-input
          v-model="queryParams.customerPhone"
          placeholder="电话号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="车牌号码" prop="licensePlate">
        <el-input
          v-model="queryParams.licensePlate"
          placeholder="车牌号吗"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['appointment:appointment:add']"
        >新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="appointmentList">
      <el-table-column label="客户名称" align="center" prop="customerName" />
      <el-table-column label="联系方式" align="center" prop="customerPhone" />
      <el-table-column label="预约时间" align="center" prop="appointmentTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.appointmentTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="到店时间" align="center" prop="actualArrivalTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.actualArrivalTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="车牌号码" align="center" prop="licensePlate" />
      <el-table-column label="汽车类型" align="center" prop="carSeries" />
      <el-table-column label="服务类型" align="center" prop="serviceType">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.bus_service_type"
            :value="scope.row.serviceType"
          ></dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="info" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.bus_appointment_type"
            :value="scope.row.status"
          ></dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['appointment:appointment:edit']"
            v-if="scope.row.status === 0"
            >编辑</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleArrive(scope.row)"
            v-hasPermi="['appointment:appointment:arrive']"
            v-if="scope.row.status === 0"
            >到店</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            v-if="
              scope.row.status === 1 ||
              scope.row.status === 4 ||
              scope.row.status === 5
            "
            @click="handleStatement(scope.row)"
          >
            结算单
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            v-if="scope.row.status === 0"
            @click="handleCancel(scope.row)"
          >
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改养修预约信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="客户姓名: " prop="customerName">
          <el-input v-model="form.customerName" placeholder="客户姓名" />
        </el-form-item>
        <el-form-item label="联系方式: " prop="customerPhone">
          <el-input v-model="form.customerPhone" placeholder="联系方式" />
        </el-form-item>
        <el-form-item label="预约时间: " prop="appointmentTime">
          <el-date-picker clearable
            v-model="form.appointmentTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="预约时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="车牌号码: " prop="licensePlate">
          <el-input v-model="form.licensePlate" placeholder="车牌号码" />
        </el-form-item>
        <el-form-item label="汽车类型: " prop="carSeries">
          <el-input v-model="form.carSeries" placeholder="汽车类型" />
        </el-form-item>
        <el-form-item label="服务类型: " prop="serviceType">
          <el-select v-model="form.serviceType" clearable>
            <el-option
              v-for="dict in dict.type.bus_service_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息: " prop="info">
          <el-input v-model="form.info" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  listAppointment, 
  getAppointment, 
  delAppointment, 
  addAppointment,
  updateAppointment,
  arrival,cancelAppointment,
  generateStatement 
} from "@/api/appointment/appointment";
import { validatePhone, validateLicensePlate } from "@/utils/validate";
export default {
  name: "Appointment",
  dicts: ["bus_service_type","bus_appointment_type"],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 养修预约信息表格数据
      appointmentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        customerName: null,
        customerPhone: null,
        appointmentTime: null,
        actualArrivalTime: null,
        licensePlate: null,
        carSeries: null,
        serviceType: null,
        info: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        customerName:[
          {required: true, message: "客户名称不能为空",trigger:"blur"}
        ],
        customerPhone:[
          {required: true, message: "联系方式不能为空",trigger:"blur"},
          {validator: validatePhone, trigger: "blur"}
        ],
        appointmentTime:[
        {required: true, message: "预约时间不能为空",trigger:"blur"}
        ],
        licensePlate: [
          { required: true, message: "车牌号码不能为空", trigger: "blur" },
          { validator: validateLicensePlate, trigger: "blur" },
        ],
        carSeries: [
          { required: true, message: "汽车类型不能为空", trigger: "blur" },
        ],
        serviceType: [
          { required: true, message: "服务类型不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询养修预约信息列表 */
    getList() {
      this.loading = true;
      listAppointment(this.queryParams).then(response => {
        this.appointmentList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        customerName: null,
        customerPhone: null,
        appointmentTime: null,
        actualArrivalTime: null,
        licensePlate: null,
        carSeries: null,
        serviceType: null,
        createTime: null,
        info: null,
        status: 0
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加预约信息";
    },
    // 生成结算单
    handleStatement(row) {
      const id = row.id;
      let _this= this;
      this.$modal.confirm("是否需要生成结算单?")
      .then(function(){
        return generateStatement(id);
      })
      .then((response)=>{
        _this.$router.push({
          path: "/business/statement/item",
          query:{id: response.data}
        })
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAppointment(id).then(response => {
        this.form = response.data;
        //编辑时服务类型显示问题
        this.form.serviceType = this.form.serviceType.toString();
        this.open = true;
        this.title = "修改养修预约信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAppointment(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAppointment(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除养修预约信息编号为"' + ids + '"的数据项？').then(function() {
        return delAppointment(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 到店按钮操作*/
    handleArrive(row) {
      const id = row.id;
      this.$modal
        .confirm("客户是否到店?")
        .then(function () {
          return arrival(id);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("操作成功");
        })
        .catch(() => {});
    },
    //取消预约
    handleCancel(row) {
      const id = row.id;
      this.$modal
        .confirm("用户是否取消预约?")
        .then(function () {
          return cancelAppointment(id);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("操作成功");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('appointment/appointment/export', {
        ...this.queryParams
      }, `appointment_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
