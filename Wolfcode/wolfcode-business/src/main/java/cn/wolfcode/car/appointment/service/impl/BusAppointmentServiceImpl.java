package cn.wolfcode.car.appointment.service.impl;

import java.util.List;

import cn.wolfcode.car.appointment.domain.BusStatement;
import cn.wolfcode.car.appointment.domain.vo.BusAppointmentVo;
import cn.wolfcode.car.appointment.enums.BusAppointmentStatus;
import cn.wolfcode.car.appointment.enums.BusStatementStatus;
import cn.wolfcode.car.appointment.mapper.BusStatementMapper;
import cn.wolfcode.car.common.utils.DateUtils;
import cn.wolfcode.car.common.utils.PhoneUtil;
import cn.wolfcode.car.common.utils.ValidateLicensePlateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wolfcode.car.appointment.mapper.BusAppointmentMapper;
import cn.wolfcode.car.appointment.domain.BusAppointment;
import cn.wolfcode.car.appointment.service.IBusAppointmentService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 养修预约信息Service业务层处理
 * 
 * @author wolfcode
 * @date 2022-12-28
 */
@Service
public class BusAppointmentServiceImpl implements IBusAppointmentService 
{
    @Autowired
    private BusAppointmentMapper busAppointmentMapper;
    @Autowired
    private BusStatementMapper busStatementMapper;

    /**
     * 查询养修预约信息
     * 
     * @param id 养修预约信息主键
     * @return 养修预约信息
     */
    @Override
    public BusAppointment selectBusAppointmentById(Long id)
    {
        return busAppointmentMapper.selectBusAppointmentById(id);
    }

    /**
     * 查询养修预约信息列表
     * 
     * @param busAppointment 养修预约信息
     * @return 养修预约信息
     */
    @Override
    public List<BusAppointment> selectBusAppointmentList(BusAppointment busAppointment)
    {
        return busAppointmentMapper.selectBusAppointmentList(busAppointment);
    }

    /**
     * 新增养修预约信息
     * 
     * @param busAppointmentVo 养修预约信息
     * @return 结果
     */
    @Override
    public int insertBusAppointment(BusAppointmentVo busAppointmentVo)
    {
        // 1 检验数据的合法性,防御性编程(非空,预约时间不能在今天之前,手机号规则,车牌号规则)
        Assert.notNull(busAppointmentVo,"非法参数");
        // 预约时间不能在今天之前
        Assert.state(busAppointmentVo.getAppointmentTime().after(DateUtils.getNowDate())
                ,"预约时间不能在今天之前");
        // 手机号规则
        Assert.state(PhoneUtil.isMobileNumber(busAppointmentVo.getCustomerPhone())
                ,"手机号格式不正确");
        // 车牌号码规则
        Assert.state(ValidateLicensePlateUtil.isValidateLicensePlate(busAppointmentVo.getLicensePlate()),
                "车牌号码格式不正确");
        // 2 busAppointmentVo---busAppointment(设置)
        // 前端数据传递数据,默认数据(创建时间,预约状态初始值(预约中))
        BusAppointment busAppointment = new BusAppointment();
        // 将busAppointmentVo属性值拷贝到busAppointment 利用工具类
        BeanUtils.copyProperties(busAppointmentVo,busAppointment);
        // 设置创建时间
        busAppointment.setCreateTime(DateUtils.getNowDate());
        //设置状态[预约中0/已到店1/用户取消2/超时取消3/已结算4/已支付5]
        // 不采取直接数据赋值
        busAppointment.setStatus(BusAppointmentStatus.APPOINTING.ordinal());
        // 3 插入数据库
        return busAppointmentMapper.insertBusAppointment(busAppointment);
    }

    /**
     * 修改养修预约信息
     * 
     * @param busAppointment 养修预约信息
     * @return 结果
     */
    @Override
    public int updateBusAppointment(BusAppointmentVo busAppointmentVo)
    {
        // 1 检验数据的合法性,防御性编程(非空,预约时间不能在今天之前,手机号规则,车牌号规则)
        Assert.notNull(busAppointmentVo,"非法参数");
        // 预约时间不能在今天之前
        Assert.state(busAppointmentVo.getAppointmentTime().after(DateUtils.getNowDate())
                ,"预约时间不能在今天之前");
        // 手机号规则
        Assert.state(PhoneUtil.isMobileNumber(busAppointmentVo.getCustomerPhone())
                ,"手机号格式不正确");
        // 车牌号码规则
        Assert.state(ValidateLicensePlateUtil.isValidateLicensePlate(busAppointmentVo.getLicensePlate()),
                "车牌号码格式不正确");
        // 防御性编程
        // 只有预约中才可以对预约信息进行编辑
        // 预约ID --- 可以通过ID查询,预约状态(数据库直接查)
        BusAppointment appointment = busAppointmentMapper.selectBusAppointmentById(busAppointmentVo.getId());
        Assert.notNull(appointment,"非法参数");
        Assert.state(appointment.getStatus() == BusAppointmentStatus.APPOINTING.ordinal()
                ,"只有预约中,才可以修改预约信息");
        BusAppointment busAppointment = new BusAppointment();
        BeanUtils.copyProperties(busAppointmentVo,busAppointment);
        return busAppointmentMapper.updateBusAppointment(busAppointment);
    }

    /**
     * 批量删除养修预约信息
     * 
     * @param ids 需要删除的养修预约信息主键
     * @return 结果
     */
    @Override
    public int deleteBusAppointmentByIds(Long[] ids)
    {
        return busAppointmentMapper.deleteBusAppointmentByIds(ids);
    }

    /**
     * 删除养修预约信息信息
     * 
     * @param id 养修预约信息主键
     * @return 结果
     */
    @Override
    public int deleteBusAppointmentById(Long id)
    {
        return busAppointmentMapper.deleteBusAppointmentById(id);
    }

    @Override
    public void arrive(Long id) {
        Assert.notNull(id,"非法参数");
        // 预约中才可以点击到店
        BusAppointment appointment = busAppointmentMapper.selectBusAppointmentById(id);
        Assert.notNull(appointment,"非法参数");
        Assert.state(appointment.getStatus()==BusAppointmentStatus.APPOINTING.ordinal()
                ,"只有预约中才可以点击到店");
        // 更新预约表预约状态--已到店
        // 设置到店时间
        BusAppointment busAppointment = new BusAppointment();
        busAppointment.setId(id);
        busAppointment.setStatus(BusAppointmentStatus.ARRIVAL.ordinal());
        busAppointment.setActualArrivalTime(DateUtils.getNowDate());
        busAppointmentMapper.updateBusAppointment(busAppointment);
    }

    @Override
    public void cancel(Long id) {
        Assert.notNull(id,"非法参数");

        BusAppointment appointment = busAppointmentMapper.selectBusAppointmentById(id);
        Assert.notNull(appointment,"非法参数");
        Assert.state(appointment.getStatus()==BusAppointmentStatus.APPOINTING.ordinal()
                ,"只有预约中才可以取消");
        BusAppointment busAppointment = new BusAppointment();
        busAppointment.setId(id);
        //将状态更新为用户取消
        busAppointment.setStatus(BusAppointmentStatus.CANCEL.ordinal());
        busAppointmentMapper.updateBusAppointment(busAppointment);
    }

    /**
     *
     * @param id 预约ID
     * @return  结算单ID
     */
    @Override
    @Transactional
    public Long generateBusStatement(Long id) {
        Assert.notNull(id,"非法参数");
        // 根据ID查到预约用户信息
        BusAppointment appointment = busAppointmentMapper.selectBusAppointmentById(id);
        Assert.notNull(appointment,"非法参数");
        // 期望状态是 已到店 || 已结算 || 已支付
        Assert.state(appointment.getStatus()== BusAppointmentStatus.ARRIVAL.ordinal()
                        || appointment.getStatus()== BusAppointmentStatus.STATEMENT.ordinal()
                        || appointment.getStatus()== BusAppointmentStatus.PAID.ordinal()
                ,"已到店才可以生成结算单");
       // 数据库查找 结算单 存在则不重复插入
        BusStatement statement = busStatementMapper.selectBusStatementByAppointmentId(id);
        if (statement!=null) return  statement.getId();
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
        // 修改预约信息的状态
        BusAppointment busAppointment = new BusAppointment();
        busAppointment.setId(id);
        busAppointment.setStatus(BusAppointmentStatus.STATEMENT.ordinal());
        busAppointmentMapper.updateBusAppointment(busAppointment);
        return busStatement.getId();
    }
}
