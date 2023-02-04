package cn.wolfcode.car.appointment.domain.vo;

public class BusCarPackageAuditVo {
    // 套餐ID
    private Long id;
    // 审批意见
    private boolean auditStatus;
    // 批注
    private String info;

    @Override
    public String toString() {
        return "BusCarPackageAuditVo{" +
                "id=" + id +
                ", auditStatus=" + auditStatus +
                ", info='" + info + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(boolean auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
