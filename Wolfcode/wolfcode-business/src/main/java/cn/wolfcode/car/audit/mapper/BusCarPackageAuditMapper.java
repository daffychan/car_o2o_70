package cn.wolfcode.car.audit.mapper;

import java.util.List;
import java.util.Set;

import cn.wolfcode.car.audit.domain.BusCarPackageAudit;
import org.apache.ibatis.annotations.Param;

/**
 * 套餐审核Mapper接口
 * 
 * @author wolfcode
 * @date 2023-01-07
 */
public interface BusCarPackageAuditMapper 
{
    /**
     * 查询套餐审核
     * 
     * @param id 套餐审核主键
     * @return 套餐审核
     */
    public BusCarPackageAudit selectBusCarPackageAuditById(Long id);

    /**
     * 查询套餐审核列表
     * 
     * @param busCarPackageAudit 套餐审核
     * @return 套餐审核集合
     */
    public List<BusCarPackageAudit> selectBusCarPackageAuditList(BusCarPackageAudit busCarPackageAudit);

    /**
     * 新增套餐审核
     * 
     * @param busCarPackageAudit 套餐审核
     * @return 结果
     */
    public int insertBusCarPackageAudit(BusCarPackageAudit busCarPackageAudit);

    /**
     * 修改套餐审核
     * 
     * @param busCarPackageAudit 套餐审核
     * @return 结果
     */
    public int updateBusCarPackageAudit(BusCarPackageAudit busCarPackageAudit);

    /**
     * 删除套餐审核
     * 
     * @param id 套餐审核主键
     * @return 结果
     */
    public int deleteBusCarPackageAuditById(Long id);

    /**
     * 批量删除套餐审核
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusCarPackageAuditByIds(Long[] ids);

    List<BusCarPackageAudit> selectBusCarPackageAuditByBusinessKeys(@Param("businessKeys") Set<String> businessKeys);

    List<BusCarPackageAudit> selectBusCarPackageAuditByBusinessKeysWithoutStatus(@Param("businessKeys") Set<String> businessKeys);
}
