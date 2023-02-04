package cn.wolfcode.car.appointment.service.impl;

import java.util.List;

import cn.wolfcode.car.appointment.domain.BusAppointment;
import cn.wolfcode.car.appointment.domain.vo.BusStatementVo;
import cn.wolfcode.car.appointment.enums.BusAppointmentStatus;
import cn.wolfcode.car.appointment.enums.BusStatementStatus;
import cn.wolfcode.car.appointment.mapper.BusAppointmentMapper;
import cn.wolfcode.car.common.utils.DateUtils;
import cn.wolfcode.car.common.utils.PhoneUtil;
import cn.wolfcode.car.common.utils.ValidateLicensePlateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wolfcode.car.appointment.mapper.BusStatementMapper;
import cn.wolfcode.car.appointment.domain.BusStatement;
import cn.wolfcode.car.appointment.service.IBusStatementService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 结算单Service业务层处理
 * 
 * @author wolfcode
 * @date 2023-01-02
 */
@Service
public class BusStatementServiceImpl implements IBusStatementService 
{
    @Autowired
    private BusStatementMapper busStatementMapper;
    @Autowired
    private BusAppointmentMapper busAppointmentMapper;

    /**
     * 查询结算单
     * 
     * @param id 结算单主键
     * @return 结算单
     */
    @Override
    public BusStatement selectBusStatementById(Long id)
    {
        return busStatementMapper.selectBusStatementById(id);
    }

    /**
     * 查询结算单列表
     * 
     * @param busStatement 结算单
     * @return 结算单
     */
    @Override
    public List<BusStatement> selectBusStatementList(BusStatement busStatement)
    {
        return busStatementMapper.selectBusStatementList(busStatement);
    }

    /**
     * 新增结算单
     * 
     * @param busStatementVo 结算单
     * @return 结果
     */
    @Override
    public int insertBusStatement(BusStatementVo busStatementVo)
    {
        Assert.notNull(busStatementVo,"非法参数");
        // 手机号码校验
        Assert.state(PhoneUtil.isMobileNumber(busStatementVo.getCustomerPhone())
                ,"手机号码格式不正确");
        // 车牌号码检验
        Assert.state(ValidateLicensePlateUtil.isValidateLicensePlate(busStatementVo.getLicensePlate()),
                "车牌号码格式不正确");
        BusStatement busStatement = new BusStatement();
        // 拷贝
        BeanUtils.copyProperties(busStatementVo,busStatement);
        busStatement.setCreateTime(DateUtils.getNowDate());
        return busStatementMapper.insertBusStatement(busStatement);
    }

    /**
     * 修改结算单
     * 
     * @param busStatementVo 结算单
     * @return 结果
     */
    @Override
    public int updateBusStatement(BusStatementVo busStatementVo)
    {
        Assert.notNull(busStatementVo,"非法参数");
        // 手机号码校验
        Assert.state(PhoneUtil.isMobileNumber(busStatementVo.getCustomerPhone())
                ,"手机号码格式不正确");
        // 车牌号码检验
        Assert.state(ValidateLicensePlateUtil.isValidateLicensePlate(busStatementVo.getLicensePlate()),
                "车牌号码格式不正确");
        // 查询消费状态是否为 消费中 否则不能修改消费单
        BusStatement statement = busStatementMapper.selectBusStatementById(busStatementVo.getId());
        Assert.state(statement.getStatus()== BusStatementStatus.IN_CONSUMPTION.ordinal(),
                "只有消费中,才可以编辑");
        BusStatement busStatement = new BusStatement();

        // 拷贝
        BeanUtils.copyProperties(busStatementVo,busStatement);

        return busStatementMapper.updateBusStatement(busStatement);
    }

    /**
     * 批量删除结算单
     * 
     * @param ids 需要删除的结算单主键
     * @return 结果
     */
    @Override
    public int deleteBusStatementByIds(Long[] ids)
    {
        return busStatementMapper.deleteBusStatementByIds(ids);
    }
    public static final int IS_DELETE = 1;
    /**
     * 逻辑删除 修改 is_delete 属性为 1
     * 删除结算单信息
     * 
     * @param id 结算单主键
     * @return 结果
     */
    @Override
    public int deleteBusStatementById(Long id)
    {
        //TODO:明细单不可移除,假如状态为已支付
        BusStatement busStatement = new BusStatement();
        busStatement.setId(id);
        busStatement.setIsDelete(IS_DELETE);
        return busStatementMapper.updateBusStatement(busStatement);
    }

    /**
     * 预约用户生成结算单
     * @param id 预约用户ID
     */
    @Override
    @Transactional
    public void generateBusStatement(Long id) {
        Assert.notNull(id,"非法参数");
        // 根据ID查到预约用户信息
        BusAppointment appointment = busAppointmentMapper.selectBusAppointmentById(id);
        Assert.notNull(appointment,"非法参数");
        // 期望状态是 已到店 || 已结算 || 已支付
        Assert.state(appointment.getStatus()== BusAppointmentStatus.ARRIVAL.ordinal()
                ,"已到店才可以生成结算单");
        //拷贝不能将id拷过去
        appointment.setId(null);
        BusStatement busStatement = new BusStatement();
        BeanUtils.copyProperties(appointment,busStatement);
        // 设置预约用户的id
        busStatement.setAppointmentId(id);
        // 设置结算状态为 消费中
        busStatement.setStatus(BusStatementStatus.IN_CONSUMPTION.ordinal());
        // 设置创建时间
        busStatement.setCreateTime(DateUtils.getNowDate());
        // 插入
        busStatementMapper.insertBusStatement(busStatement);
    }
}
