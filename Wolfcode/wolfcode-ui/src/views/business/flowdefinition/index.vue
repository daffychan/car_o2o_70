<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="部署时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审核类型" prop="status">
        <el-select v-model="queryParams.bpmnType" clearable>
          <el-option
            v-for="dict in dict.type.bus_flow_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
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
          v-hasPermi="['business:bpmnInfo:add']"
          >流程定义部署</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
      <el-table-column label="流程名称" align="center" prop="bpmnLabel" 
      :show-overflow-tooltip="true"
      />
      <el-table-column
        label="流程定义key"
        align="center"
        prop="processDefinitionKey"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="描述信息" align="center" prop="info" />
      <el-table-column label="版本号" align="center" prop="version" />
      <el-table-column
        label="部署时间"
        align="center"
        prop="deployTime"
        width="180"
      >
      </el-table-column>
      <el-table-column label="流程类型" align="center" prop="bpmnType">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.bus_flow_type"
            :value="scope.row.bpmnType"
          >
          </dict-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="流程文件"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="medium"
            type="text"
            icon="el-icon-search"
            @click="handleReadResource(scope.row.id)"
            v-hasPermi="['business:bpmnInfo:readResource']"
            >查看</el-button
          >
        </template>
      </el-table-column>
      <el-table-column
        label="流程图"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="medium"
            type="text"
            icon="el-icon-search"
            @click="handleReadResourceImg(scope.row.id)"
            v-hasPermi="['business:bpmnInfo:readResource']"
            >查看</el-button
          >
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
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['flowdefinition:info:edit']"
            >撤销</el-button
          >
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

   <!-- 部署流程对话框 -->
   <el-dialog
      title="流程部署"
      :visible.sync="open"
      width="500px"
      append-to-body
    >
      <el-form ref="form" :model="form" label-width="100px" :rules="rules">
        <el-form-item label="审核类型" prop="bpmnType">
          <el-select
            v-model="form.bpmnType"
            placeholder="所有"
            style="width: 240px"
          >
            <el-option
              v-for="dict in dict.type.bus_flow_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="请选择文件: ">
          <el-upload
            ref="upfile"
            action="upload"
            style="display: inline"
            :auto-upload="false"
            :on-change="handleChange"
            :file-list="fileList"
          >
            <el-button type="success">选择文件</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item label="备注" prop="info">
          <el-input
            v-model="form.info"
            type="textarea"
            placeholder="请输入内容"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

        <!-- 流程文件显示框 -->
        <el-dialog title="流程文件" :visible.sync="openResource" width="1200px">
      <div>{{ resource}}</div>
    </el-dialog>
    <!-- 流程图片显示框 -->
    <el-dialog title="流程图片" :visible.sync="openResourceImg" width="1200px">
      <div v-html="resourceImg"></div>
    </el-dialog>
  </div>
</template>

<script>
import { listInfo, getInfo, delInfo, addInfo, updateInfo,deployBpmnInfo,getBpmnInfoFile } from "@/api/flowdefinition/info";

export default {
  name: "Info",
  dicts: ["bus_flow_type"],
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
      // 流程定义表格数据
      infoList: [],
      dateRange: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      fileList: [], //文件上传数组
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        bpmnLabel: null,
        bpmnType: null,
        processDefinitionKey: null,
        deployTime: null,
        version: null,
        info: null
      },
      // 表单参数
      form: {
        info: "", //备注
        bpmnType: "", //审核类型
        file: [],// 上传文件
        bpmnLabel: "", //审核名称
      },
      // 表单校验
      rules: {
        bpmnType: [
          {required: true, message: "审核类型不能为空", trigger: "change"},
        ],
        info: [{required: true, message: "备注不能为空", trigger: "blur"}],
      },
      // 流程文件对象
      resource: '',
      // 流程文件弹窗显示隐藏
      openResource: false,
      // 流程图片弹窗显示隐藏
      openResourceImg: false,
      // 流程图片对象
      resourceImg:''
    };
  },
  created() {
    this.getList();
  },
  methods: {
       /** 查询流程定义明细列表 */
       getList() {
      this.loading = true;
      listInfo(this.addDateRange(this.queryParams, this.dateRange)).then(
        (response) => {
          this.infoList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
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
        bpmnLabel: null,
        bpmnType: null,
        processDefinitionKey: null,
        deployTime: null,
        version: null,
        info: null
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加流程定义";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改流程定义";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          //拼接表单数据
          let formData=new FormData();
          formData.append("file",this.fileList[0].raw);
          formData.append("bpmnLabel",this.fileList[0].name);
          formData.append("bpmnType",this.form.bpmnType);
          formData.append("info",this.form.info);
          deployBpmnInfo(formData).then((res)=>{
            this.getList();
            this.open=false;
            this.$modal.msgSuccess(response.msg);
          }).catch((err)=>{
            this.open=false;
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除流程定义编号为"' + id + '"的数据项？').then(function() {
        return delInfo(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 文件变化操作 */
    handleChange(file, fileList){
      this.fileList=fileList;
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('flowdefinition/info/export', {
        ...this.queryParams
      }, `info_${new Date().getTime()}.xlsx`)
    },
    handleReadResource(id){
      getBpmnInfoFile({id,type:"xml"}).then((res)=>{
          this.openResource=true;
          this.resource=res;
      });
    },
    handleReadResourceImg(id){
      getBpmnInfoFile({id,type:"svg"}).then((res)=>{
        this.openResourceImg = true;
        this.resourceImg = res;
      })
    }
  }
};
</script>
