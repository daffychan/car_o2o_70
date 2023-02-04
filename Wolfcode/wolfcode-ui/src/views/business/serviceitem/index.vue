<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入服务项名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否套餐" prop="carPackage">
        <el-select v-model="queryParams.carPackage" clearable>
          <el-option
            v-for="dict in dict.type.bus_package_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="服务分类" prop="serviceCatalog">
        <el-select v-model="queryParams.serviceCatalog" clearable>
          <el-option
            v-for="dict in dict.type.bus_service_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态:">
        <el-select v-model="queryParams.auditStatus" clearable>
          <el-option
            v-for="dict in dict.type.bus_audit_package_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="上架状态:">
        <el-select v-model="queryParams.saleStatus" clearable>
          <el-option
            v-for="dict in dict.type.bus_sale_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['appointment:item:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleAudit"
          :disabled="isNotAudit"
          v-hasPermi="[':appointment:item:auditInfo']"
          >发起审核</el-button
        >
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="itemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="服务名称" align="center" prop="name" />
      <el-table-column label="服务原价" align="center" prop="originalPrice" />
      <el-table-column label="折扣价" align="center" prop="discountPrice" />
      <el-table-column label="是否套餐" align="center" prop="carPackage">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.bus_package_type"
            :value="scope.row.carPackage"
          ></dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="服务分类" align="center" prop="serviceCatalog">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.bus_service_type"
            :value="scope.row.serviceCatalog"
          ></dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.bus_audit_package_type"
            :value="scope.row.auditStatus"
          ></dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="上架状态" align="center" prop="saleStatus">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.bus_sale_type"
            :value="scope.row.saleStatus"
          ></dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注信息" align="center" prop="info" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            :disabled="editStatus(scope.row)"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['business:serviceitem:edit']"
            >编辑</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-thumb"
            v-if="!scope.row.saleStatus"
            @click="handleOnsale(scope.row)"
            v-hasPermi="['business:serviceitem:onsale']"
            >上架</el-button
          >

          <el-button
            size="mini"
            type="text"
            icon="el-icon-thumb"
            v-if="scope.row.saleStatus"
            @click="handleUnsale(scope.row)"
            v-hasPermi="['business:serviceitem:unsale']"
            >下架</el-button
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

     <!-- 添加或修改服务项对话框 -->
     <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="服务名称: " prop="name">
          <el-input v-model="form.name" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="服务原价: " prop="originalPrice">
          <el-input
            v-model="form.originalPrice"
            placeholder="请输入服务项原价"
          />
        </el-form-item>
        <el-form-item label="折扣价: " prop="discountPrice">
          <el-input
            v-model="form.discountPrice"
            placeholder="请输入服务项折扣价"
          />
        </el-form-item>
        <el-form-item label="是否套餐: " prop="carPackage">
          <el-radio-group v-model="form.carPackage">
            <el-radio
              v-for="dict in dict.type.bus_package_type"
              :key="dict.value"
              :label="parseInt(dict.value)"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
        <el-form-item label="服务分类: " prop="serviceCatalog">
          <el-select size="medium" v-model="form.serviceCatalog">
            <el-option
              v-for="dict in dict.type.bus_service_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息: " prop="info">
          <el-input v-model="form.info" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 审核页面对话框 -->
    <el-dialog 
      title="审核页面"
      :visible.sync="auditOpen"
      width="600px"
      append-to-body
      >
      <el-form
        ref="auditInfo"
        :model="auditInfo"
        :rules="auditRules"
        label-width="150px"
      >
        <el-form-item label="服务单项名称：" prop="name">
          <el-input disabled v-model="auditInfo.serviceItem.name" />
        </el-form-item>
        <el-form-item label="原价：" prop="originalPrice">
          <el-input disabled v-model="auditInfo.serviceItem.originalPrice" />
        </el-form-item>
        <el-form-item label="折扣价：" prop="discountPrice">
          <el-input disabled v-model="auditInfo.serviceItem.discountPrice" />
        </el-form-item>
        <el-form-item label="审核人(店长)：" prop="shopOwners">
          <el-select
            size="medium"
            v-model="shopOwnerId"
          >
            <el-option
              v-for="(item) in auditInfo.shopOwners"
              :key="item.userId"
              :label="item.nickName"
              :value="item.userId"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          label="审核人(财务)："
          v-if="auditInfo.serviceItem.discountPrice >= auditInfo.discountPrice"
          prop="finances"
        >
          <el-select 
            size="medium"
            v-model="financeId"
            >
            <el-option
              v-for="(item) in auditInfo.finance"
              :key="item.userId"
              :label="item.nickName"
              :value="item.userId"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息：" prop="info">
          <el-input v-model="form.info" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="auditSubmit">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listItem
  , getItem
  , delItem
  , addItem
  , updateItem
  , serviceItemSaleOff
  , serviceItemSaleOn
  , serviceItemAuditInfo
  , serviceItemStartAudit } from "@/api/appointment/item";

export default {
  name: "Item",
  dicts:['bus_service_type','bus_package_type','bus_audit_package_type','bus_sale_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 是否可以发起审核
      isNotAudit: true,
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
      // 是否显示审核页面
      auditOpen:false,
      // 【请填写功能名称】表格数据
      itemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        originalPrice: null,
        discountPrice: null,
        carPackage: null,
        info: null,
        creatTime: null,
        serviceCatalog: null,
        auditStatus: null,
        saleStatus: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "服务名称不能为空", trigger: "blur" },
        ],
        originalPrice: [
          { required: true, message: "服务原价不能为空", trigger: "blur" },
        ],
        discountPrice: [
          { required: true, message: "服务折扣价不能为空", trigger: "blur" },
        ],
        carPackage: [
          { required: true, message: "是否套餐不能为空", trigger: "blur" },
        ],
        serviceCatalog: [
          { required: true, message: "服务分类不能为空", trigger: "blur" },
        ],
        info: [
          { required: true, message: "备注信息不能为空", trigger: "blur" },
        ],
      },
      auditRules: {
        name: [
          { required: true, message: "服务名称不能为空", trigger: "blur" },
        ],
        originalPrice: [
          { required: true, message: "服务原价不能为空", trigger: "blur" },
        ],
        discountPrice: [
          { required: true, message: "服务折扣价不能为空", trigger: "blur" },
        ],
      },
      auditInfo:{
        serviceItem: {},
        shopOwners: {},
        finances:{},
        discountPrice:0
      },
      shopOwnerId:null,
      financeId:null
    };
  },
  created() {
    this.getList();
  },
  computed: {
    // 未上架服务项的编辑状态：
    editStatus() {
      return function (row) {
        // 已上架或审核中的服务项不可编辑
        if (row.saleStatus == 1 || row.auditStatus == 1|| row.auditStatus == 2) {
          return true;
        }
        return false;
      };
    }
  },
  methods: {
    /** 查询【请填写功能名称】列表 */
    getList() {
      this.loading = true;
      listItem(this.queryParams).then(response => {
        this.itemList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
       /** 审核提交 */
       auditSubmit() {
      this.auditOpen=false;
      let auditParams={
        serviceItemId: this.auditInfo.serviceItem.id,
        shopOwnerId:this.shopOwnerId,
        financeId:this.financeId,
        info:this.form.info
      };
      serviceItemStartAudit(auditParams).then((response)=>{
        this.$modal.msgSuccess("提交成功");
        this.getList();
      });
    },
    // 取消按钮
    cancel() {
      this.auditOpen=false;
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        originalPrice: null,
        discountPrice: null,
        carPackage: null,
        info: null,
        creatTime: null,
        serviceCatalog: null,
        auditStatus: 0,
        saleStatus: 0
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      // 可以发起编辑的状态
      // 处理单个发起审核的
      // 是套餐,未上架,初始化状态,审批被拒绝的
      // 是否套餐【是1/否0】
      // 【1已上架/0未上架】
      // 审核状态【初始化/审核中/审核通过/审核拒绝/无需审核】
      let isSingleSeletion=selection.length == 1;
      if(!isSingleSeletion){//非当个
        this.isNotAudit = true;
        return;
      }
      let serviceItem=selection[0];
      let isPacakge=serviceItem.carPackage == 1; // 是套餐
      let isNoOnSale=serviceItem.saleStatus == 0; // 未上架
      let isPassAudit=serviceItem.auditStatus == 0 || serviceItem.auditStatus == 3;
      if(!isPacakge){ //非套餐
        this.isNotAudit = true;
        return;
      }
      if(!isNoOnSale){//已上架
        this.isNotAudit = true;
        return;
      }
      if(!isPassAudit){//审核校验不通过
        this.isNotAudit = true;
        return;
      }
      this.isNotAudit = false;
      this.id= serviceItem.id;
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },

     /** 发起审核处理 */
     handleAudit() {
      if(this.isNotAudit){
        return;
      }
      this.auditOpen=true;
      this.reset();
      //console.log(this.id);
      serviceItemAuditInfo({id:this.id}).then((response)=>{
        this.auditInfo=response.data;
        console.log(this.auditInfo);
        this.shopOwnerId=this.auditInfo.shopOwners[0].userId;
        if(this.auditInfo.finances !==undefined){
          this.financeId=this.auditInfo.finances[0].userId;
        }
      })
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加养修服务单项";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getItem(id).then(response => {
        this.form = response.data;
        this.form.serviceCatalog = this.form.serviceCatalog.toString();
        this.open = true;
        this.title = "修改养修服务单项";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateItem(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addItem(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
     // 上架操作
     handleOnsale(row) {
      const id = row.id;
      this.$modal
        .confirm("是否需要上架该商品?")
        .then(function () {
          return serviceItemSaleOn({ id });
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("操作成功");
        })
        .catch(() => {});
    },
    
    // 下架操作
    handleUnsale(row) {
        const id = row.id;
      this.$modal
        .confirm("是否需要下架该商品?")
        .then(function () {
          return serviceItemSaleOff({ id });
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("操作成功");
        })
        .catch(() => {});
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除【请填写功能名称】编号为"' + ids + '"的数据项？').then(function() {
        return delItem(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('appointment/item/export', {
        ...this.queryParams
      }, `item_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
