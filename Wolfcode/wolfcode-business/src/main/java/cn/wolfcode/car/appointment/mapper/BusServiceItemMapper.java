package cn.wolfcode.car.appointment.mapper;

import java.util.List;
import cn.wolfcode.car.appointment.domain.BusServiceItem;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author wolfcode
 * @date 2022-12-30
 */
public interface BusServiceItemMapper 
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
     * @param busServiceItem 【请填写功能名称】
     * @return 结果
     */
    public int insertBusServiceItem(BusServiceItem busServiceItem);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busServiceItem 【请填写功能名称】
     * @return 结果
     */
    public int updateBusServiceItem(BusServiceItem busServiceItem);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteBusServiceItemById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusServiceItemByIds(Long[] ids);
}
