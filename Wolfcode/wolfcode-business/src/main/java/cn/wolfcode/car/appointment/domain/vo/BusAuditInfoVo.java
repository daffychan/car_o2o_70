package cn.wolfcode.car.appointment.domain.vo;

public class BusAuditInfoVo {

    /**
     * 服务项ID
     */
    private Long serviceItemId;
    /**
     * 店长ID
     */
    private Long shopOwnerId;
    /**
     * 财务ID
     */
    private Long financeId;
    /**
     * 备注
     */
    private String info;

    @Override
    public String toString() {
        return "BusAuditInfoVo{" +
                "serviceItemId=" + serviceItemId +
                ", shopOwnerId=" + shopOwnerId +
                ", financeId=" + financeId +
                ", info='" + info + '\'' +
                '}';
    }

    public Long getServiceItemId() {
        return serviceItemId;
    }

    public void setServiceItemId(Long serviceItemId) {
        this.serviceItemId = serviceItemId;
    }

    public Long getShopOwnerId() {
        return shopOwnerId;
    }

    public void setShopOwnerId(Long shopOwnerId) {
        this.shopOwnerId = shopOwnerId;
    }

    public Long getFinanceId() {
        return financeId;
    }

    public void setFinanceId(Long financeId) {
        this.financeId = financeId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
