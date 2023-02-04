package cn.wolfcode.car.appointment.service;

import java.util.List;

import cn.wolfcode.car.appointment.domain.BusStatement;
import cn.wolfcode.car.appointment.domain.BusStatementItem;
import cn.wolfcode.car.appointment.domain.vo.BusStatementItemVo;

/**
 * 明细单Service接口
 * 
 * @author wolfcode
 * @date 2023-01-03
 */
public interface IBusStatementItemService 
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
     * 批量删除明细单
     * 
     * @param ids 需要删除的明细单主键集合
     * @return 结果
     */
    public int deleteBusStatementItemByIds(Long[] ids);

    /**
     * 删除明细单信息
     * 
     * @param id 明细单主键
     * @return 结果
     */
    public int deleteBusStatementItemById(Long id);

    /**
     * 保存结算单明细
     * @param busStatementItemVo
     */
    BusStatement saveBusStatementItems(BusStatementItemVo busStatementItemVo);

    void pay(BusStatementItemVo busStatementItemVo);

}
