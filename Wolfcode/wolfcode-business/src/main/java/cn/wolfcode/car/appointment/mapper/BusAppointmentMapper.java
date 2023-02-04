package cn.wolfcode.car.appointment.mapper;

import java.util.List;
import cn.wolfcode.car.appointment.domain.BusAppointment;

/**
 * 养修预约信息Mapper接口
 * 
 * @author wolfcode
 * @date 2022-12-28
 */
public interface BusAppointmentMapper 
{
    /**
     * 查询养修预约信息
     * 
     * @param id 养修预约信息主键
     * @return 养修预约信息
     */
    public BusAppointment selectBusAppointmentById(Long id);

    /**
     * 查询养修预约信息列表
     * 
     * @param busAppointment 养修预约信息
     * @return 养修预约信息集合
     */
    public List<BusAppointment> selectBusAppointmentList(BusAppointment busAppointment);

    /**
     * 新增养修预约信息
     * 
     * @param busAppointment 养修预约信息
     * @return 结果
     */
    public int insertBusAppointment(BusAppointment busAppointment);

    /**
     * 修改养修预约信息
     * 
     * @param busAppointment 养修预约信息
     * @return 结果
     */
    public int updateBusAppointment(BusAppointment busAppointment);

    /**
     * 删除养修预约信息
     * 
     * @param id 养修预约信息主键
     * @return 结果
     */
    public int deleteBusAppointmentById(Long id);

    /**
     * 批量删除养修预约信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusAppointmentByIds(Long[] ids);
}
