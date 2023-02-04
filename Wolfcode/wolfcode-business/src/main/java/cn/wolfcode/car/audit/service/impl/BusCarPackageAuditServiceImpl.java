package cn.wolfcode.car.audit.service.impl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import cn.wolfcode.car.appointment.domain.BusServiceItem;
import cn.wolfcode.car.appointment.domain.info.HistoryCommentInfo;
import cn.wolfcode.car.appointment.domain.vo.BusCarPackageAuditVo;
import cn.wolfcode.car.appointment.enums.BusBpmnInfoStatus;
import cn.wolfcode.car.appointment.enums.BusPackageAuditStatus;
import cn.wolfcode.car.appointment.enums.BusPackageInfoAuditStatus;
import cn.wolfcode.car.appointment.mapper.BusServiceItemMapper;
import cn.wolfcode.car.common.utils.DateUtils;
import cn.wolfcode.car.common.utils.SecurityUtils;
import cn.wolfcode.car.flowdefinition.domain.BusBpmnInfo;
import cn.wolfcode.car.flowdefinition.mapper.BusBpmnInfoMapper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wolfcode.car.audit.mapper.BusCarPackageAuditMapper;
import cn.wolfcode.car.audit.domain.BusCarPackageAudit;
import cn.wolfcode.car.audit.service.IBusCarPackageAuditService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * 套餐审核Service业务层处理
 * 
 * @author wolfcode
 * @date 2023-01-07
 */
@Service
public class BusCarPackageAuditServiceImpl implements IBusCarPackageAuditService 
{
    @Autowired
    private BusCarPackageAuditMapper busCarPackageAuditMapper;
    @Autowired
    private BusBpmnInfoMapper busBpmnInfoMapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private BusServiceItemMapper busServiceItemMapper;
    @Autowired
    private HistoryService historyService;
    @Autowired
    public RepositoryService repositoryService;


    /**
     * 查询套餐审核
     * 
     * @param id 套餐审核主键
     * @return 套餐审核
     */
    @Override
    public BusCarPackageAudit selectBusCarPackageAuditById(Long id)
    {
        return busCarPackageAuditMapper.selectBusCarPackageAuditById(id);
    }

    /**
     * 查询套餐审核列表
     * 
     * @param busCarPackageAudit 套餐审核
     * @return 套餐审核
     */
    @Override
    public List<BusCarPackageAudit> selectBusCarPackageAuditList(BusCarPackageAudit busCarPackageAudit)
    {
        return busCarPackageAuditMapper.selectBusCarPackageAuditList(busCarPackageAudit);
    }

    /**
     * 新增套餐审核
     * 
     * @param busCarPackageAudit 套餐审核
     * @return 结果
     */
    @Override
    public int insertBusCarPackageAudit(BusCarPackageAudit busCarPackageAudit)
    {
        busCarPackageAudit.setCreateTime(DateUtils.getNowDate());
        return busCarPackageAuditMapper.insertBusCarPackageAudit(busCarPackageAudit);
    }

    /**
     * 修改套餐审核
     * 
     * @param busCarPackageAudit 套餐审核
     * @return 结果
     */
    @Override
    public int updateBusCarPackageAudit(BusCarPackageAudit busCarPackageAudit)
    {
        return busCarPackageAuditMapper.updateBusCarPackageAudit(busCarPackageAudit);
    }

    /**
     * 批量删除套餐审核
     * 
     * @param ids 需要删除的套餐审核主键
     * @return 结果
     */
    @Override
    public int deleteBusCarPackageAuditByIds(Long[] ids)
    {
        return busCarPackageAuditMapper.deleteBusCarPackageAuditByIds(ids);
    }

    /**
     * 删除套餐审核信息
     * 
     * @param id 套餐审核主键
     * @return 结果
     */
    @Override
    public int deleteBusCarPackageAuditById(Long id)
    {
        return busCarPackageAuditMapper.deleteBusCarPackageAuditById(id);
    }

    /**
     * @return
     */
    @Override
    public List<BusCarPackageAudit> todoQuery() {
        // 1 获取当前用户ID
        Long userId = SecurityUtils.getUserId();
        // 3 通过id和key,查询业务标识(businessKey==>服务项的ID)
        //   3.1 通过id和流程定义的ID获取到流程实例的id(TaskService---act_ru_task)
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(String.valueOf(userId))
                .list();
        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.EMPTY_LIST;
        }
        Set<String> processInstanceIds = new HashSet<>();
        for (Task task : tasks) {
            String processInstanceId = task.getProcessInstanceId();
            processInstanceIds.add(processInstanceId);
        }
        //   3.2 通过流程实例的ID(runtimeService  act_ru_execution),查询业务标识businessKey
        List<ProcessInstance> processInstances = runtimeService
                .createProcessInstanceQuery()
                .processInstanceIds(processInstanceIds)
                .list();
        Set<String> businessKeys = new HashSet<>();
        for (ProcessInstance processInstance : processInstances) {
            String businessKey = processInstance.getBusinessKey();
            businessKeys.add(businessKey);
        }
        if (CollectionUtils.isEmpty(businessKeys)) {
            return Collections.EMPTY_LIST;
        }
        List<BusCarPackageAudit> busCarPackageAudits = busCarPackageAuditMapper
                .selectBusCarPackageAuditByBusinessKeys(businessKeys);
        // 4 获取代办的服务项信息
        return busCarPackageAudits;
    }


    /**
     * @param busCarPackageAuditVo  封装(审核套餐ID 审核意见(同意/拒绝) 批注)
     */
    @Override
    @Transactional
    public void audit(BusCarPackageAuditVo busCarPackageAuditVo) {
        // 防御编程
        Assert.notNull(busCarPackageAuditVo,"非法参数");
        BusCarPackageAudit busCarPackageAudit = busCarPackageAuditMapper
                .selectBusCarPackageAuditById(busCarPackageAuditVo.getId());
        Assert.notNull(busCarPackageAudit,"非法参数");
        // 套餐审核状态断言
        Assert.state(busCarPackageAudit.getStatus() == BusPackageInfoAuditStatus.AUDITING.ordinal(),
                "只有审核中才可以审批");
        // 查询流程实例任务
        Task task = taskService
                .createTaskQuery()
                .processInstanceId(busCarPackageAudit.getInstanceId())
                .singleResult();
        // 添加审核意见 (任务ID shopOwner 审批意见)
        taskService.setVariable(task.getId(),"shopOwner",busCarPackageAuditVo.isAuditStatus());
        // 添加批注 (String taskId, String processInstance, String message)
        String username = SecurityUtils.getUsername();
        String isAudit = busCarPackageAuditVo.isAuditStatus()?"同意":"拒绝";
        String comment = username + "[" + isAudit + "]" + busCarPackageAuditVo.getInfo();
        taskService.addComment(task.getId(),busCarPackageAudit.getInstanceId(),comment);
        // 完成任务 (任务ID)
        taskService.complete(task.getId());
        // 再查一遍任务是否处理结束
        Task nextTask = taskService
                .createTaskQuery()
                .processInstanceId(busCarPackageAudit.getInstanceId())
                .singleResult();
        // 根据审核意见 同意/拒绝 修改套餐审核状态/服务项审核状态
        if (busCarPackageAuditVo.isAuditStatus()) {
            // 同意审批
            if (nextTask==null) {
                // 任务结束 修改状态为审核通过
                // 套餐审核信息
                BusCarPackageAudit carPackageAudit = new BusCarPackageAudit();
                carPackageAudit.setId(busCarPackageAuditVo.getId());
                carPackageAudit.setStatus(BusPackageInfoAuditStatus.PASS.ordinal());
                busCarPackageAuditMapper.updateBusCarPackageAudit(carPackageAudit);
                // 服务项
                BusServiceItem serviceItem = new BusServiceItem();
                serviceItem.setId(busCarPackageAudit.getServiceItemId());
                serviceItem.setAuditStatus(BusPackageAuditStatus.PASS.ordinal());
                busServiceItemMapper.updateBusServiceItem(serviceItem);
            }
            // 任务还需继续审批则无需处理
        }else {
            // 不同意审批
            // 套餐审核信息
            BusCarPackageAudit carPackageAudit = new BusCarPackageAudit();
            carPackageAudit.setId(busCarPackageAuditVo.getId());
            carPackageAudit.setStatus(BusPackageInfoAuditStatus.CANCEL.ordinal());
            busCarPackageAuditMapper.updateBusCarPackageAudit(carPackageAudit);
            // 服务项
            BusServiceItem serviceItem = new BusServiceItem();
            serviceItem.setId(busCarPackageAudit.getServiceItemId());
            serviceItem.setAuditStatus(BusPackageAuditStatus.REJECT.ordinal());
            busServiceItemMapper.updateBusServiceItem(serviceItem);
        }
    }


    /**审批历史
     * @param id 套餐信息 ID
     * @return  返回任务审批历史信息
     * HistoryCommentInfo ( 任务名称 开始时间 结束时间 持续时间 批注)
     */
    @Override
    public List<HistoryCommentInfo> listHistory(Long id) {
        Assert.notNull(id,"非法参数");
        BusCarPackageAudit busCarPackageAudit = busCarPackageAuditMapper.selectBusCarPackageAuditById(id);
        Assert.notNull(busCarPackageAudit,"非法参数");
        String instanceId = busCarPackageAudit.getInstanceId();
        // 3 通过流程实例ID 查询历史任务信息
        // 4 通过流程实例ID 查询历史批注信息
        // act_hi_taskinst
        List<HistoricTaskInstance> taskInstances = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(instanceId)
                .orderByHistoricTaskInstanceEndTime()
                .asc()
                .list();
        // Stream 流 操作
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        List<HistoryCommentInfo> historyCommentInfos = taskInstances.stream().map(taskInstance -> {
            HistoryCommentInfo historyCommentInfo = new HistoryCommentInfo();
            historyCommentInfo.setTaskName(taskInstance.getName());
            historyCommentInfo.setStartTime(dateFormat.format(taskInstance.getStartTime()));
            if (taskInstance.getEndTime() != null) {
                historyCommentInfo.setEndTime(dateFormat.format(taskInstance.getEndTime()));
            }
            if (taskInstance.getDurationInMillis() != null) {
                historyCommentInfo.setDurationInMillis(DateUtils.getDatePoor(taskInstance.getEndTime(), taskInstance.getStartTime()));
            }
            List<Comment> comments = taskService.getTaskComments(taskInstance.getId(), "comment");
            if (!CollectionUtils.isEmpty(comments)) {
                historyCommentInfo.setComment(comments.get(0).getFullMessage());
            }
            return historyCommentInfo;
        }).collect(Collectors.toList());
        return historyCommentInfos;
    }


    /**
     * @param id 套餐的Id
     * @return    流程图带高亮节点bpmnModel
     */
    @Override
    public InputStream process(Long id) {
        Assert.notNull(id,"非法参数");
        BusCarPackageAudit busCarPackageAudit = busCarPackageAuditMapper.selectBusCarPackageAuditById(id);
        Assert.notNull(busCarPackageAudit,"非法参数");
        String instanceId = busCarPackageAudit.getInstanceId();
        // 获取流程定义的key
        String processDefinitionKey = null;
        List<String> activeActivityIds = Collections.emptyList();
        if (busCarPackageAudit.getStatus()==BusPackageInfoAuditStatus.AUDITING.ordinal()){
            processDefinitionKey = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(instanceId)
                    .list().get(0).getProcessDefinitionKey();
            // 获取高亮节点
            activeActivityIds = runtimeService.getActiveActivityIds(instanceId);
        }else {
            processDefinitionKey = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(instanceId)
                    .list().get(0).getProcessDefinitionKey();
        }
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .list().get(0);
        Assert.notNull(processDefinition,"获取不到流程定义");
        String processDefinitionId = processDefinition.getId();
        // 获取bpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        return generator.generateDiagram(
                bpmnModel
                ,activeActivityIds
                ,Collections.EMPTY_LIST
                ,"宋体"
                ,"宋体"
                ,"宋体");
    }

    /** 我的已办审核套餐列表
     * @return List<BusCarPackageAudit>
     */
    @Override
    public List<BusCarPackageAudit> doneQuery() {
        // 查询当前登录用户--userId
        Long userId = SecurityUtils.getUserId();
        // 根据userId--查询当前用户的历史任务
        List<HistoricTaskInstance> historicTaskInstances = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee(String.valueOf(userId))
                .list();
        // 判断该用户是否拥有已办的历史任务
        if (CollectionUtils.isEmpty(historicTaskInstances)) {
            // 避免报错 用户无已办历史任务则返回空集合
            return Collections.EMPTY_LIST;
        }
        Set<String> businessKeys = new HashSet<>();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
           if(historicTaskInstance.getEndTime()==null) continue;
            HistoricProcessInstance historicProcessInstance = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(historicTaskInstance.getProcessInstanceId())
                    .list().get(0);
            businessKeys.add(historicProcessInstance.getBusinessKey());
        }
        List<BusCarPackageAudit> busCarPackageAudits = busCarPackageAuditMapper
                .selectBusCarPackageAuditByBusinessKeysWithoutStatus(businessKeys);
        return busCarPackageAudits;
    }

    /**
     * 撤销套餐审核
     * @param id 套餐审核ID
     */
    @Override
    @Transactional
    public void cancel(Long id) {
        Assert.notNull(id,"非法参数");
        BusCarPackageAudit busCarPackageAudit = busCarPackageAuditMapper.selectBusCarPackageAuditById(id);
        Assert.notNull(busCarPackageAudit,"非法参数");
        String instanceId = busCarPackageAudit.getInstanceId();
        // 1 删除运行的流程实例
        runtimeService.deleteProcessInstance(instanceId,"");
        // 2 删除历史的流程实例(根据实际需求)
        historyService.deleteHistoricProcessInstance(instanceId);

        Long serviceItemId = busCarPackageAudit.getServiceItemId();
        BusServiceItem serviceItem = new BusServiceItem();
        serviceItem.setId(serviceItemId);
        serviceItem.setAuditStatus(BusPackageAuditStatus.INIT.ordinal());
        // 3 修改套餐审核信息状态为审核撤销
        // TODO:删除数据==>将审核数据状态改为审核撤销
        busCarPackageAuditMapper.deleteBusCarPackageAuditById(id);
        // 4 修改对应服务项的状态
        busServiceItemMapper.updateBusServiceItem(serviceItem);
    }
}
