package cn.wolfcode.car.appointment.domain.vo;

import cn.wolfcode.car.appointment.domain.BusStatementItem;
import cn.wolfcode.car.common.annotation.Excel;
import cn.wolfcode.car.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.List;


public class BusStatementItemVo
{
    /**
     * 折扣价
     */
    private BigDecimal discountPrice;
    /**
     * 结算单明细列表
     */
    private List<BusStatementItem> busStatementItems;

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public List<BusStatementItem> getBusStatementItems() {
        return busStatementItems;
    }

    public void setBusStatementItems(List<BusStatementItem> busStatementItems) {
        this.busStatementItems = busStatementItems;
    }

    @Override
    public String toString() {
        return "BusStatementItemVo{" +
                "discountPrice=" + discountPrice +
                ", busStatementItems=" + busStatementItems +
                '}';
    }
}
