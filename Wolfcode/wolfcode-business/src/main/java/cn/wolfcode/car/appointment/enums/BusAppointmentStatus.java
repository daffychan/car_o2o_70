package cn.wolfcode.car.appointment.enums;

/**
 * 预约状态
 */
public enum BusAppointmentStatus {
    // 0 预约中
    APPOINTING,
    // 1 已到店
    ARRIVAL,
    // 2 用户取消
    CANCEL,
    // 3 超时取消
    TIMEOUT_CANCEL,
    // 4 已结算
    STATEMENT,
    // 5 已支付
    PAID
}
