package cn.wolfcode.car.flowdefinition.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import cn.wolfcode.car.flowdefinition.domain.BusBpmnInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 流程定义Service接口
 * 
 * @author wolfcode
 * @date 2023-01-06
 */
public interface IBusBpmnInfoService 
{
    /**
     * 查询流程定义
     * 
     * @param id 流程定义主键
     * @return 流程定义
     */
    public BusBpmnInfo selectBusBpmnInfoById(Long id);

    /**
     * 查询流程定义列表
     * 
     * @param busBpmnInfo 流程定义
     * @return 流程定义集合
     */
    public List<BusBpmnInfo> selectBusBpmnInfoList(BusBpmnInfo busBpmnInfo);

    /**
     * 新增流程定义
     * 
     * @param busBpmnInfo 流程定义
     * @return 结果
     */
    public int insertBusBpmnInfo(BusBpmnInfo busBpmnInfo);

    /**
     * 修改流程定义
     * 
     * @param busBpmnInfo 流程定义
     * @return 结果
     */
    public int updateBusBpmnInfo(BusBpmnInfo busBpmnInfo);

    /**
     * 批量删除流程定义
     * 
     * @param ids 需要删除的流程定义主键集合
     * @return 结果
     */
    public int deleteBusBpmnInfoByIds(Long[] ids);

    /**
     * 删除流程定义信息
     * 
     * @param id 流程定义主键
     * @return 结果
     */
    public int deleteBusBpmnInfoById(Long id);

    void deploy(MultipartFile file, String bpmnLabel, Integer bpmnType, String info) throws IOException;

    InputStream readResource(String type, Long id);
}
