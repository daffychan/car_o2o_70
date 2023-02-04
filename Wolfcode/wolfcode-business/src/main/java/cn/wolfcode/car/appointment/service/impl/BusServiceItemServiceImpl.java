package cn.wolfcode.car.appointment.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wolfcode.car.appointment.domain.info.ServiceItemAuditInfo;
import cn.wolfcode.car.appointment.domain.vo.BusAuditInfoVo;
import cn.wolfcode.car.appointment.domain.vo.BusServiceItemVo;
import cn.wolfcode.car.appointment.enums.*;
import cn.wolfcode.car.audit.domain.BusCarPackageAudit;
import cn.wolfcode.car.audit.mapper.BusCarPackageAuditMapper;
import cn.wolfcode.car.common.core.domain.entity.SysUser;
import cn.wolfcode.car.common.enums.BusinessStatus;
import cn.wolfcode.car.common.utils.ActivitiesUtils;
import cn.wolfcode.car.common.utils.DateUtils;
import cn.wolfcode.car.common.utils.SecurityUtils;
import cn.wolfcode.car.flowdefinition.domain.BusBpmnInfo;
import cn.wolfcode.car.flowdefinition.mapper.BusBpmnInfoMapper;
import cn.wolfcode.car.system.mapper.SysUserMapper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wolfcode.car.appointment.mapper.BusServiceItemMapper;
import cn.wolfcode.car.appointment.domain.BusServiceItem;
import cn.wolfcode.car.appointment.service.IBusServiceItemService;
import org.springframework.util.Assert;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author wolfcode
 * @date 2022-12-30
 */
@Service
public class BusServiceItemServiceImpl implements IBusServiceItemService 
{
    @Autowired
    private BusServiceItemMapper busServiceItemMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private BusBpmnInfoMapper busBpmnInfoMapper;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private BusCarPackageAuditMapper busCarPackageAuditMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public BusServiceItem selectBusServiceItemById(Long id)
    {
        return busServiceItemMapper.selectBusServiceItemById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busServiceItem 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusServiceItem> selectBusServiceItemList(BusServiceItem busServiceItem)
    {
        return busServiceItemMapper.selectBusServiceItemList(busServiceItem);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param busServiceItemVo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusServiceItem(BusServiceItemVo busServiceItemVo)
    {
        // 防御编程
        Assert.notNull(busServiceItemVo,"非法参数");
        // 原价不能小于折扣价
        boolean pass = busServiceItemVo.getOriginalPrice().compareTo(busServiceItemVo.getDiscountPrice()) >= 0;
        Assert.state(pass,"折扣价不能大于原价");
        BusServiceItem busServiceItem = new BusServiceItem();
        BeanUtils.copyProperties(busServiceItemVo,busServiceItem);
        // 根据是否是套餐设置 审核状态{0 初始化 / 4 无需审核}
        busServiceItem.setAuditStatus(busServiceItem.getCarPackage() == BusCarPackage.NO.ordinal()?
                BusPackageAuditStatus.NO_AUDIT.ordinal() :BusPackageAuditStatus.INIT.ordinal());
        // 上架状态为 未上架
        busServiceItem.setSaleStatus(BusSaleStatus.SALE_OFF.ordinal());

        return busServiceItemMapper.insertBusServiceItem(busServiceItem);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param busServiceItemVo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusServiceItem(BusServiceItemVo busServiceItemVo)
    {
        // 防御编程
        Assert.notNull(busServiceItemVo,"非法参数");
        // 原价不能小于折扣价
        boolean pass = busServiceItemVo.getOriginalPrice().compareTo(busServiceItemVo.getDiscountPrice()) >= 0;
        Assert.state(pass,"折扣价不能大于原价");
        // id 查数据库
        BusServiceItem serviceItem = busServiceItemMapper.selectBusServiceItemById(busServiceItemVo.getId());
        Assert.notNull(serviceItem,"非法参数");
        // 获取上下架状态 期望不是上架状态
        Assert.state(serviceItem.getSaleStatus()!= BusSaleStatus.SALE_ON.ordinal()
                ,"商品上架中,不可编辑");
        // 获取审核状态 期望不是审核中,或者审核通过
        Assert.state(serviceItem.getAuditStatus()!=BusPackageAuditStatus.AUDITING.ordinal()
                ,"审核中套餐,不可编辑");
        Assert.state(serviceItem.getAuditStatus()!=BusPackageAuditStatus.PASS.ordinal()
                ,"审核通过套餐,不可编辑");

        BusServiceItem busServiceItem = new BusServiceItem();
        // 判断是否为套餐
        if (busServiceItemVo.getCarPackage()== BusCarPackage.YES.ordinal()) {
            // 是套餐 设置审核为 初始化
            busServiceItem.setAuditStatus(BusPackageAuditStatus.INIT.ordinal());
        }else {
            //非套餐 设置为 无需审核
            busServiceItem.setAuditStatus(BusPackageAuditStatus.NO_AUDIT.ordinal());
        }
        BeanUtils.copyProperties(busServiceItemVo,busServiceItem);

        return busServiceItemMapper.updateBusServiceItem(busServiceItem);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteBusServiceItemByIds(Long[] ids)
    {
        return busServiceItemMapper.deleteBusServiceItemByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteBusServiceItemById(Long id)
    {
        return busServiceItemMapper.deleteBusServiceItemById(id);
    }

    /**
     * 服务上架
     * @param id
     */
    @Override
    public void saleOn(Long id) {
        Assert.notNull(id,"非法参数");
        BusServiceItem busServiceItem = busServiceItemMapper.selectBusServiceItemById(id);
        Assert.notNull(busServiceItem,"非法参数");
        // 下架状态 才能 上架
        Assert.state(busServiceItem.getSaleStatus()==BusSaleStatus.SALE_OFF.ordinal()
                ,"只有状态为下架,才可以上架");
        // 期望审核状态为 无需审核 或 审核通过 才可以上架
        Assert.state(busServiceItem.getAuditStatus()==BusPackageAuditStatus.PASS.ordinal() ||
                busServiceItem.getAuditStatus()==BusPackageAuditStatus.NO_AUDIT.ordinal()
                ,"服务项审核通过或无需审核,才可以上架");
        BusServiceItem serviceItem = new BusServiceItem();
        serviceItem.setId(id);
        serviceItem.setSaleStatus(BusSaleStatus.SALE_ON.ordinal());
        busServiceItemMapper.updateBusServiceItem(serviceItem);
    }

    /**
     * 服务下架
     * @param id
     */
    @Override
    public void saleOff(Long id) {
        Assert.notNull(id,"非法参数");
        BusServiceItem busServiceItem = busServiceItemMapper.selectBusServiceItemById(id);
        Assert.notNull(busServiceItem,"非法参数");
        // 上架状态 才能 下架
        Assert.state(busServiceItem.getSaleStatus()==BusSaleStatus.SALE_ON.ordinal()
                ,"只有状态为上架,才可以下架");
        BusServiceItem serviceItem = new BusServiceItem();
        serviceItem.setId(id);
        serviceItem.setSaleStatus(BusSaleStatus.SALE_OFF.ordinal());
        busServiceItemMapper.updateBusServiceItem(serviceItem);
    }

    /**
     * @param id 服务项主键ID
     * @return   (serviceItem,店长列表,财务列表,流程定义价格)
     */
    @Override
    public ServiceItemAuditInfo auditInfo(Long id) {
        Assert.notNull(id,"非法参数");
        // 根据 id 查询服务项
        BusServiceItem serviceItem = busServiceItemMapper.selectBusServiceItemById(id);
        Assert.notNull(serviceItem,"非法参数");
        Integer carPackage = serviceItem.getCarPackage();
        Integer auditStatus = serviceItem.getAuditStatus();
        Integer saleStatus = serviceItem.getSaleStatus();
        // 满足(是套餐 && (审核状态为 初始化 || 审核拒绝) && 未上架 )才可以发起审核
        Assert.state(carPackage==BusCarPackage.YES.ordinal()
                && (auditStatus == BusPackageAuditStatus.INIT.ordinal() || auditStatus == BusPackageAuditStatus.REJECT.ordinal())
                && saleStatus == BusSaleStatus.SALE_OFF.ordinal(),
                "不符合审核条件");
        ServiceItemAuditInfo auditInfo = new ServiceItemAuditInfo();
        List<SysUser> shopOwners = sysUserMapper.selectUserListByRoleKey("shopowner");
//        动态获取流程定义的价格
//        通过工具类AvtivitiUtil,需要两个参数(BpmnModel bpmnModel,String processDefinitionKey)
//        1.bus_bpmn_info 有processDefinitionKey字段
        BusBpmnInfo bpmnInfo = busBpmnInfoMapper.getBpmnInfoByType(BusBpmnInfoStatus.PACKAGE.ordinal());
        // 获取流程定义对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(bpmnInfo.getProcessDefinitionKey())
                .processDefinitionVersion(bpmnInfo.getVersion())
                .singleResult();
        // 获取BpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        // 利用工具类获取流程定义价格
        BigDecimal discountPrice = ActivitiesUtils.getGTEDiscountPrice(bpmnModel, bpmnInfo.getProcessDefinitionKey());
        //BigDecimal discountPrice = new BigDecimal(3000);
        if (serviceItem.getDiscountPrice().compareTo(discountPrice)>=0) {
            List<SysUser> finances = sysUserMapper.selectUserListByRoleKey("finance");
            auditInfo.setFinance(finances);
        }
        auditInfo.setServiceItem(serviceItem);
        auditInfo.setShopOwners(shopOwners);
        auditInfo.setDiscountPrice(discountPrice);
        return auditInfo;
    }

    /**
     * 发起服务项审核
     * @param busAuditInfoVo
     */
    @Override
    public void startAudit(BusAuditInfoVo busAuditInfoVo) {
        Assert.notNull(busAuditInfoVo,"非法参数");
        BusServiceItem serviceItem = busServiceItemMapper.selectBusServiceItemById(busAuditInfoVo.getServiceItemId());
        Assert.notNull(serviceItem,"非法参数");
        Integer carPackage = serviceItem.getCarPackage();
        Integer auditStatus = serviceItem.getAuditStatus();
        Integer saleStatus = serviceItem.getSaleStatus();
        Assert.state(carPackage==BusCarPackage.YES.ordinal()
                && (auditStatus == BusPackageAuditStatus.INIT.ordinal() || auditStatus == BusPackageAuditStatus.REJECT.ordinal())
                && saleStatus == BusSaleStatus.SALE_OFF.ordinal(),
                "不符合审核条件");
        BusBpmnInfo bpmnInfo = busBpmnInfoMapper.getBpmnInfoByType(BusBpmnInfoStatus.PACKAGE.ordinal());
        Map<String, Object> variables = new HashMap<>();
        variables.put("shopOwnerId",busAuditInfoVo.getShopOwnerId());
        if (busAuditInfoVo.getFinanceId()!=null) {
            variables.put("financeId",busAuditInfoVo.getFinanceId());
        }
        // ps:特别注意 流程变量的注入不能是BigDecimal类型 最好是long类型 activiti框架限定
        variables.put("disCountPrice",serviceItem.getDiscountPrice().longValue());
        // 开启流程实例(流程定义的Key businessKey->服务项ID 流程变量 map)
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(bpmnInfo.getProcessDefinitionKey(), String.valueOf(serviceItem.getId()), variables);

        BusCarPackageAudit busCarPackageAudit = new BusCarPackageAudit();
        busCarPackageAudit.setServiceItemId(busAuditInfoVo.getServiceItemId());
        busCarPackageAudit.setServiceItemName(serviceItem.getName());
        busCarPackageAudit.setServiceItemInfo(serviceItem.getInfo());
        busCarPackageAudit.setServiceItemPrice(serviceItem.getDiscountPrice());
        // 流程实例ID
        busCarPackageAudit.setInstanceId(processInstance.getProcessInstanceId());
        busCarPackageAudit.setCreatorId(String.valueOf(SecurityUtils.getUserId()));
        busCarPackageAudit.setInfo(busAuditInfoVo.getInfo());
        busCarPackageAudit.setStatus(BusPackageInfoAuditStatus.AUDITING.ordinal());
        busCarPackageAudit.setCreateTime(DateUtils.getNowDate());
        // 插入
        busCarPackageAuditMapper.insertBusCarPackageAudit(busCarPackageAudit);
        // 修改服务项状态为审核中
        BusServiceItem busServiceItem = new BusServiceItem();
        busServiceItem.setId(busAuditInfoVo.getServiceItemId());
        busServiceItem.setAuditStatus(BusPackageAuditStatus.AUDITING.ordinal());
        busServiceItemMapper.updateBusServiceItem(busServiceItem);
    }
}
