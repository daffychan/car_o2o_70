package cn.wolfcode.car.flowdefinition.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wolfcode.car.flowdefinition.mapper.BusBpmnInfoMapper;
import cn.wolfcode.car.flowdefinition.domain.BusBpmnInfo;
import cn.wolfcode.car.flowdefinition.service.IBusBpmnInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * 流程定义Service业务层处理
 * 
 * @author wolfcode
 * @date 2023-01-06
 */
@Service
public class BusBpmnInfoServiceImpl implements IBusBpmnInfoService 
{
    @Autowired
    private BusBpmnInfoMapper busBpmnInfoMapper;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 查询流程定义
     * 
     * @param id 流程定义主键
     * @return 流程定义
     */
    @Override
    public BusBpmnInfo selectBusBpmnInfoById(Long id)
    {
        return busBpmnInfoMapper.selectBusBpmnInfoById(id);
    }

    /**
     * 查询流程定义列表
     * 
     * @param busBpmnInfo 流程定义
     * @return 流程定义
     */
    @Override
    public List<BusBpmnInfo> selectBusBpmnInfoList(BusBpmnInfo busBpmnInfo)
    {
        return busBpmnInfoMapper.selectBusBpmnInfoList(busBpmnInfo);
    }

    /**
     * 新增流程定义
     * 
     * @param busBpmnInfo 流程定义
     * @return 结果
     */
    @Override
    public int insertBusBpmnInfo(BusBpmnInfo busBpmnInfo)
    {
        return busBpmnInfoMapper.insertBusBpmnInfo(busBpmnInfo);
    }

    /**
     * 修改流程定义
     * 
     * @param busBpmnInfo 流程定义
     * @return 结果
     */
    @Override
    public int updateBusBpmnInfo(BusBpmnInfo busBpmnInfo)
    {
        return busBpmnInfoMapper.updateBusBpmnInfo(busBpmnInfo);
    }

    /**
     * 批量删除流程定义
     * 
     * @param ids 需要删除的流程定义主键
     * @return 结果
     */
    @Override
    public int deleteBusBpmnInfoByIds(Long[] ids)
    {
        return busBpmnInfoMapper.deleteBusBpmnInfoByIds(ids);
    }

    /**
     * 删除流程定义信息
     * 
     * @param id 流程定义主键
     * @return 结果
     */
    @Override
    @Transactional//需要删除多张表
    public int deleteBusBpmnInfoById(Long id)
    {
        Assert.notNull(id,"非法参数");
        BusBpmnInfo busBpmnInfo = busBpmnInfoMapper.selectBusBpmnInfoById(id);
        Assert.notNull(busBpmnInfo,"非法参数");
        // 需要 部署的Id deploymentId
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(busBpmnInfo.getProcessDefinitionKey())
                .processDefinitionVersion(busBpmnInfo.getVersion())
                .singleResult();
        // TODO:删除关联表 需要管理员权限
        repositoryService.deleteDeployment(processDefinition.getDeploymentId(),true);
        return busBpmnInfoMapper.deleteBusBpmnInfoById(id);
    }

    /**
     *
     * @param file 部署文件
     * @param bpmnLabel 服务名称
     * @param bpmnType  服务类型
     * @param info    备注信息
     */
    @Override
    public void deploy(MultipartFile file, String bpmnLabel, Integer bpmnType, String info) throws IOException {
        // 数据校验
        Assert.notNull(file,"非法参数");
        // 只能上传bpmn后缀的文件
        String filename = file.getOriginalFilename();
        String substring = filename.substring(filename.lastIndexOf(".") + 1);
        Assert.state("bpmn".equals(substring),"只能上传流程定义文件,后缀名bpmn");
        // 部署流程
        Deployment deployment = repositoryService
                .createDeployment()
                .addInputStream(filename, file.getInputStream())
                .deploy();
        // 根据部署的ID获取流程定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .list().get(0);
        BusBpmnInfo busBpmnInfo = new BusBpmnInfo();
        busBpmnInfo.setBpmnLabel(bpmnLabel);
        busBpmnInfo.setBpmnType(bpmnType);
        busBpmnInfo.setInfo(info);
        busBpmnInfo.setDeployTime(deployment.getDeploymentTime());
        busBpmnInfo.setProcessDefinitionKey(processDefinition.getKey());
        busBpmnInfo.setVersion(processDefinition.getVersion());
        busBpmnInfoMapper.insertBusBpmnInfo(busBpmnInfo);
    }

    /**
     *
     * @param type 文件类型
     * @param id   bus_bpmn_info的自增id
     * @return    利用id 查询信息表中的 processDefinition_key流程定义的key以及版本号version
     */
    @Override
    public InputStream readResource(String type, Long id) {
        Assert.notNull(id,"非法参数");
        BusBpmnInfo busBpmnInfo = busBpmnInfoMapper.selectBusBpmnInfoById(id);
        Assert.notNull(busBpmnInfo,"非法参数");
        // 通过bus_bpmn_info 表的 流程定义Key以及 版本号 获取流程定义实例
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(busBpmnInfo.getProcessDefinitionKey())
                .processDefinitionVersion(busBpmnInfo.getVersion())
                .singleResult();
        InputStream inputStream = null;
        if ("xml".equalsIgnoreCase(type)) {
            inputStream = repositoryService
                    .getResourceAsStream(processDefinition.getDeploymentId(),processDefinition.getResourceName());
        }else {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            // 生成png图片
            DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
            inputStream = generator.generateDiagram(bpmnModel,
                    Collections.EMPTY_LIST,
                    Collections.EMPTY_LIST,
                    "宋体",
                    "宋体",
                    "宋体");
        }
        return inputStream;
    }
}
