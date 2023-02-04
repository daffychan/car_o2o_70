package cn.wolfcode.car.appointment.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.wolfcode.car.common.annotation.Excel;
import cn.wolfcode.car.common.core.domain.BaseEntity;

/**
 * 结算单对象 bus_statement
 * 
 * @author wolfcode
 * @date 2023-01-02
 */
public class BusStatement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customerName;

    /** 客户电话 */
    @Excel(name = "客户电话")
    private String customerPhone;

    /** 到店时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "到店时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date actualArrivalTime;

    /** 车牌号 */
    @Excel(name = "车牌号")
    private String licensePlate;

    /** 汽车类型 */
    @Excel(name = "汽车类型")
    private String carSeries;

    /** 服务类型[0维修/1保养/2其他] */
    @Excel(name = "服务类型[0维修/1保养/2其他]")
    private Integer serviceType;

    /** 预约ID */
    @Excel(name = "预约ID")
    private Long appointmentId;

    /** 结算状态[消费中0/已支付1] */
    @Excel(name = "结算状态[消费中0/已支付1]")
    private Integer status;

    /** 付款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "付款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /** 收款人ID */
    @Excel(name = "收款人ID")
    private Long payeeId;

    /** 项目数量 */
    @Excel(name = "项目数量")
    private BigDecimal totalAmount;

    /** 总消费金额 */
    @Excel(name = "总消费金额")
    private BigDecimal totalQuantity;

    /** 折扣金额 */
    @Excel(name = "折扣金额")
    private BigDecimal discountAmount;

    /** 备注信息 */
    @Excel(name = "备注信息")
    private String info;

    /** 是否删除[0未删除/1删除] */
    @Excel(name = "是否删除[0未删除/1删除]")
    private Integer isDelete;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCustomerName(String customerName) 
    {
        this.customerName = customerName;
    }

    public String getCustomerName() 
    {
        return customerName;
    }
    public void setCustomerPhone(String customerPhone) 
    {
        this.customerPhone = customerPhone;
    }

    public String getCustomerPhone() 
    {
        return customerPhone;
    }
    public void setActualArrivalTime(Date actualArrivalTime) 
    {
        this.actualArrivalTime = actualArrivalTime;
    }

    public Date getActualArrivalTime() 
    {
        return actualArrivalTime;
    }
    public void setLicensePlate(String licensePlate) 
    {
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() 
    {
        return licensePlate;
    }
    public void setCarSeries(String carSeries) 
    {
        this.carSeries = carSeries;
    }

    public String getCarSeries() 
    {
        return carSeries;
    }
    public void setServiceType(Integer serviceType) 
    {
        this.serviceType = serviceType;
    }

    public Integer getServiceType() 
    {
        return serviceType;
    }
    public void setAppointmentId(Long appointmentId) 
    {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId() 
    {
        return appointmentId;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setPayTime(Date payTime) 
    {
        this.payTime = payTime;
    }

    public Date getPayTime() 
    {
        return payTime;
    }
    public void setPayeeId(Long payeeId) 
    {
        this.payeeId = payeeId;
    }

    public Long getPayeeId() 
    {
        return payeeId;
    }
    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }
    public void setTotalQuantity(BigDecimal totalQuantity) 
    {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalQuantity() 
    {
        return totalQuantity;
    }
    public void setDiscountAmount(BigDecimal discountAmount) 
    {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountAmount() 
    {
        return discountAmount;
    }
    public void setInfo(String info) 
    {
        this.info = info;
    }

    public String getInfo() 
    {
        return info;
    }
    public void setIsDelete(Integer isDelete) 
    {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() 
    {
        return isDelete;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("customerName", getCustomerName())
            .append("customerPhone", getCustomerPhone())
            .append("actualArrivalTime", getActualArrivalTime())
            .append("licensePlate", getLicensePlate())
            .append("carSeries", getCarSeries())
            .append("serviceType", getServiceType())
            .append("appointmentId", getAppointmentId())
            .append("status", getStatus())
            .append("payTime", getPayTime())
            .append("payeeId", getPayeeId())
            .append("totalAmount", getTotalAmount())
            .append("totalQuantity", getTotalQuantity())
            .append("discountAmount", getDiscountAmount())
            .append("createTime", getCreateTime())
            .append("info", getInfo())
            .append("isDelete", getIsDelete())
            .toString();
    }
}
