package cn.wolfcode.car.appointment.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import cn.wolfcode.car.appointment.domain.BusAppointment;
import cn.wolfcode.car.appointment.domain.BusStatement;
import cn.wolfcode.car.appointment.domain.vo.BusStatementItemVo;
import cn.wolfcode.car.appointment.enums.BusAppointmentStatus;
import cn.wolfcode.car.appointment.enums.BusStatementStatus;
import cn.wolfcode.car.appointment.mapper.BusAppointmentMapper;
import cn.wolfcode.car.appointment.mapper.BusStatementMapper;
import cn.wolfcode.car.appointment.service.IBusStatementService;
import cn.wolfcode.car.common.utils.DateUtils;
import cn.wolfcode.car.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wolfcode.car.appointment.mapper.BusStatementItemMapper;
import cn.wolfcode.car.appointment.domain.BusStatementItem;
import cn.wolfcode.car.appointment.service.IBusStatementItemService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 明细单Service业务层处理
 * 
 * @author wolfcode
 * @date 2023-01-03
 */
@Service
public class BusStatementItemServiceImpl implements IBusStatementItemService 
{
    @Autowired
    private BusStatementItemMapper busStatementItemMapper;
    @Autowired
    private BusStatementMapper busStatementMapper;
    @Autowired
    private BusAppointmentMapper busAppointmentMapper;

    /**
     * 查询明细单
     * 
     * @param id 明细单主键
     * @return 明细单
     */
    @Override
    public BusStatementItem selectBusStatementItemById(Long id)
    {
        return busStatementItemMapper.selectBusStatementItemById(id);
    }

    /**
     * 查询明细单列表
     * 
     * @param busStatementItem 明细单
     * @return 明细单
     */
    @Override
    public List<BusStatementItem> selectBusStatementItemList(BusStatementItem busStatementItem)
    {
        return busStatementItemMapper.selectBusStatementItemList(busStatementItem);
    }

    /**
     * 新增明细单
     * 
     * @param busStatementItem 明细单
     * @return 结果
     */
    @Override
    public int insertBusStatementItem(BusStatementItem busStatementItem)
    {
        return busStatementItemMapper.insertBusStatementItem(busStatementItem);
    }

    /**
     * 修改明细单
     * 
     * @param busStatementItem 明细单
     * @return 结果
     */
    @Override
    public int updateBusStatementItem(BusStatementItem busStatementItem)
    {
        return busStatementItemMapper.updateBusStatementItem(busStatementItem);
    }

    /**
     * 批量删除明细单
     * 
     * @param ids 需要删除的明细单主键
     * @return 结果
     */
    @Override
    public int deleteBusStatementItemByIds(Long[] ids)
    {
        return busStatementItemMapper.deleteBusStatementItemByIds(ids);
    }

    /**
     * 删除明细单信息
     * 
     * @param id 明细单主键
     * @return 结果
     */
    @Override
    public int deleteBusStatementItemById(Long id)
    {
        return busStatementItemMapper.deleteBusStatementItemById(id);
    }

    /**
     * 保存结算明细
     * 需要操作两张表
     * bus_statement  结算单
     * bus_statement_item 明细表
     * 需要添加事务@Transactional
     * @param busStatementItemVo
     */
    @Override
    @Transactional
    public BusStatement saveBusStatementItems(BusStatementItemVo busStatementItemVo) {
        // 校验数据
        Assert.notNull(busStatementItemVo,"非法参数");
        Assert.notNull(busStatementItemVo.getDiscountPrice(),"非法参数");
        List<BusStatementItem> busStatementItems = busStatementItemVo.getBusStatementItems();
        Assert.notEmpty(busStatementItems,"结算单明细不能为空");
        boolean isUniqueStatementId  = checkUniqueStatementId(busStatementItems);
        Assert.state(isUniqueStatementId,"非法结算单明细");
        // 只要确保结算单明细集合里面结算单ID是唯一
        Long statementId = busStatementItems.get(0).getStatementId();
        // 期望结算单状态是 消费中
        BusStatement busStatement = busStatementMapper.selectBusStatementById(statementId);
        Assert.state(busStatement.getStatus()== BusStatementStatus.IN_CONSUMPTION.ordinal(),
                "只有消费中的状态才可以保存结算单明细或支付");
        // 避免二次保存导致数据库重复添加 先删除原有数据
        //根据statementId删除
        busStatementItemMapper.deleteBusStatementItemsByStatementId(statementId);
        BigDecimal totalAmount = new BigDecimal(0);
        Long totalQuantity = 0L;
        // 保存 结算单明细
        for (BusStatementItem busStatementItem : busStatementItems) {
            // 根据业务 ,结算单明细不多,可以直接for插入,但是数量比较大的情况下,可以使用批处理
            busStatementItemMapper.insertBusStatementItem(busStatementItem);
            // 计算总消费金额,总数量
            totalAmount = totalAmount.add(busStatementItem.getItemPrice()
                    .multiply(new BigDecimal(busStatementItem.getItemQuantity())));
            totalQuantity += busStatementItem.getItemQuantity();
        }
        // 折扣金额不能够 大于 消费总金额
        Assert.state(totalAmount.compareTo(busStatementItemVo.getDiscountPrice())>=0
                ,"折扣金额不能够大于消费总金额");
        // 更新结算单信息(总消费金额,折扣金额,总数量)
        BusStatement statement = new BusStatement();
        statement.setId(statementId);
        statement.setTotalAmount(totalAmount);
        statement.setDiscountAmount(busStatementItemVo.getDiscountPrice());
        statement.setTotalQuantity(new BigDecimal(totalQuantity));
        busStatementMapper.updateBusStatement(statement);
        return busStatement;
    }

    /**
     * 支付
     * 将新的结算单保存
     * @param busStatementItemVo
     */
    @Override
    @Transactional
    public void pay(BusStatementItemVo busStatementItemVo) {
        // 保存结算单
        BusStatement busStatement = saveBusStatementItems(busStatementItemVo);
        // 落库 bus_statement(收款人,支付时间,支付状态)
        BusStatement statement = new BusStatement();
        statement.setId(busStatement.getId());
        // 获取当前登录的用户ID(收款人)
        Long userId = SecurityUtils.getUserId();
        statement.setPayeeId(userId);
        statement.setPayTime(DateUtils.getNowDate());
        statement.setStatus(BusStatementStatus.PAID.ordinal());
        busStatementMapper.updateBusStatement(statement);
        // 落库 bus_appointment
        if (busStatement.getAppointmentId()!=null) {
            // 将预约用户的状态 设置为已支付
            BusAppointment busAppointment = new BusAppointment();
            busAppointment.setId(busStatement.getAppointmentId());
            busAppointment.setStatus(BusAppointmentStatus.PAID.ordinal());
            busAppointmentMapper.updateBusAppointment(busAppointment);
        }
    }

    /**
     * 判断是否为同一张结算单
     * @param busStatementItems
     * @return
     */
    private boolean checkUniqueStatementId(List<BusStatementItem> busStatementItems) {
        HashSet<Long> set = new HashSet<>();
        for (BusStatementItem statementItem : busStatementItems) {
            set.add(statementItem.getStatementId());
        }
        return set.size()==1;
    }
}
