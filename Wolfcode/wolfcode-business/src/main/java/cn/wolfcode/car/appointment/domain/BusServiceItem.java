package cn.wolfcode.car.appointment.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.wolfcode.car.common.annotation.Excel;
import cn.wolfcode.car.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 bus_service_item
 * 
 * @author wolfcode
 * @date 2022-12-30
 */
public class BusServiceItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 服务项名称 */
    @Excel(name = "服务项名称")
    private String name;

    /** 原价 */
    @Excel(name = "原价")
    private BigDecimal originalPrice;

    /** 折扣价 */
    @Excel(name = "折扣价")
    private BigDecimal discountPrice;

    /** 是否套餐【1是/0否】 */
    @Excel(name = "是否套餐【1是/0否】")
    private Integer carPackage;

    /** 备注信息 */
    @Excel(name = "备注信息")
    private String info;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date creatTime;

    /** 服务分类【维修/保养/其他】 */
    @Excel(name = "服务分类【维修/保养/其他】")
    private Integer serviceCatalog;

    /** 审核状态【0初始化/1审核中/2审核通过/3审核拒绝/4无需审核】 */
    @Excel(name = "审核状态【0初始化/1审核中/2审核通过/3审核拒绝/4无需审核】")
    private Integer auditStatus;

    /** 上架状态【1已上架/0未上架】 */
    @Excel(name = "上架状态【1已上架/0未上架】")
    private Integer saleStatus;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setOriginalPrice(BigDecimal originalPrice) 
    {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getOriginalPrice() 
    {
        return originalPrice;
    }
    public void setDiscountPrice(BigDecimal discountPrice) 
    {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getDiscountPrice() 
    {
        return discountPrice;
    }
    public void setCarPackage(Integer carPackage) 
    {
        this.carPackage = carPackage;
    }

    public Integer getCarPackage() 
    {
        return carPackage;
    }
    public void setInfo(String info) 
    {
        this.info = info;
    }

    public String getInfo() 
    {
        return info;
    }
    public void setCreatTime(Date creatTime) 
    {
        this.creatTime = creatTime;
    }

    public Date getCreatTime() 
    {
        return creatTime;
    }
    public void setServiceCatalog(Integer serviceCatalog) 
    {
        this.serviceCatalog = serviceCatalog;
    }

    public Integer getServiceCatalog() 
    {
        return serviceCatalog;
    }
    public void setAuditStatus(Integer auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public Integer getAuditStatus() 
    {
        return auditStatus;
    }
    public void setSaleStatus(Integer saleStatus) 
    {
        this.saleStatus = saleStatus;
    }

    public Integer getSaleStatus() 
    {
        return saleStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("originalPrice", getOriginalPrice())
            .append("discountPrice", getDiscountPrice())
            .append("carPackage", getCarPackage())
            .append("info", getInfo())
            .append("creatTime", getCreatTime())
            .append("serviceCatalog", getServiceCatalog())
            .append("auditStatus", getAuditStatus())
            .append("saleStatus", getSaleStatus())
            .toString();
    }
}
