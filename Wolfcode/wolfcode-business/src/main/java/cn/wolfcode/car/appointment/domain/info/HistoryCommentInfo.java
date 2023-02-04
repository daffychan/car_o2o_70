package cn.wolfcode.car.appointment.domain.info;

/**
 * 历史的任务任务批注信息
 */
public class HistoryCommentInfo {
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 持续时间
     */
    private String durationInMillis;
    /**
     * 批注
     */
    private String comment;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDurationInMillis() {
        return durationInMillis;
    }

    public void setDurationInMillis(String durationInMillis) {
        this.durationInMillis = durationInMillis;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "HistoryCommentInfo{" +
                "taskName='" + taskName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", durationInMillis='" + durationInMillis + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
