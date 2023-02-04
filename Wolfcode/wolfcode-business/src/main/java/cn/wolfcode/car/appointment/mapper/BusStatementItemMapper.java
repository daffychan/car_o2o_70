package cn.wolfcode.car.appointment.mapper;

import java.util.List;
import cn.wolfcode.car.appointment.domain.BusStatementItem;

/**
 * 明细单Mapper接口
 * 
 * @author wolfcode
 * @date 2023-01-03
 */
public interface BusStatementItemMapper 
{
    /**
     * 查询明细单
     * 
     * @param id 明细单主键
     * @return 明细单
     */
    public BusStatementItem selectBusStatementItemById(Long id);

    /**
     * 查询明细单列表
     * 
     * @param busStatementItem 明细单
     * @return 明细单集合
     */
    public List<BusStatementItem> selectBusStatementItemList(BusStatementItem busStatementItem);

    /**
     * 新增明细单
     * 
     * @param busStatementItem 明细单
     * @return 结果
     */
    public int insertBusStatementItem(BusStatementItem busStatementItem);

    /**
     * 修改明细单
     * 
     * @param busStatementItem 明细单
     * @return 结果
     */
    public int updateBusStatementItem(BusStatementItem busStatementItem);

    /**
     * 删除明细单
     * 
     * @param id 明细单主键
     * @return 结果
     */
    public int deleteBusStatementItemById(Long id);

    /**
     * 批量删除明细单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusStatementItemByIds(Long[] ids);

    void deleteBusStatementItemsByStatementId(Long statementId);
}
