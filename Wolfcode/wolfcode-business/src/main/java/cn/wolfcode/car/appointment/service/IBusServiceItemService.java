package cn.wolfcode.car.appointment.service;

import java.util.List;
import cn.wolfcode.car.appointment.domain.BusServiceItem;
import cn.wolfcode.car.appointment.domain.info.ServiceItemAuditInfo;
import cn.wolfcode.car.appointment.domain.vo.BusAuditInfoVo;
import cn.wolfcode.car.appointment.domain.vo.BusServiceItemVo;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author wolfcode
 * @date 2022-12-30
 */
public interface IBusServiceItemService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public BusServiceItem selectBusServiceItemById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busServiceItem 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusServiceItem> selectBusServiceItemList(BusServiceItem busServiceItem);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busServiceItemVo 【请填写功能名称】
     * @return 结果
     */
    public int insertBusServiceItem(BusServiceItemVo busServiceItemVo);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busServiceItemVo 【请填写功能名称】
     * @return 结果
     */
    public int updateBusServiceItem(BusServiceItemVo busServiceItemVo);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteBusServiceItemByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteBusServiceItemById(Long id);

    void saleOn(Long id);

    void saleOff(Long id);

    ServiceItemAuditInfo auditInfo(Long id);

    void startAudit(BusAuditInfoVo busAuditInfoVo);
}
