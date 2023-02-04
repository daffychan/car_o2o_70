package cn.wolfcode.car.appointment.domain.info;

import cn.wolfcode.car.appointment.domain.BusServiceItem;
import cn.wolfcode.car.common.core.domain.entity.SysUser;

import java.math.BigDecimal;
import java.util.List;

public class ServiceItemAuditInfo {
    private BusServiceItem serviceItem;
    /**
     * 店长列表
     */
    private List<SysUser> shopOwners;

    /**
     * 财务列表
     */
    private List<SysUser> finances;

    /**
     * 审核折扣价
     */
    private BigDecimal discountPrice;

    @Override
    public String toString() {
        return "ServiceItemAuditInfo{" +
                "serviceItem=" + serviceItem +
                ", shopOwners=" + shopOwners +
                ", finance=" + finances +
                ", discountPrice=" + discountPrice +
                '}';
    }

    public BusServiceItem getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(BusServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public List<SysUser> getShopOwners() {
        return shopOwners;
    }

    public void setShopOwners(List<SysUser> shopOwners) {
        this.shopOwners = shopOwners;
    }

    public List<SysUser> getFinance() {
        return finances;
    }

    public void setFinance(List<SysUser> finance) {
        this.finances  = finance;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }
}
