package cn.ac.iscas.common.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/5/4 12:42
 */
public class BatchInsertDataResponse extends BaseResponse{
    private Date beginTime;
    private Date endTime;
    private List<SingleInsertDataResponse> successTableRecords;
    private List<SingleInsertDataResponse> failedTableRecords;


    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<SingleInsertDataResponse> getSuccessTableRecords() {
        return successTableRecords;
    }

    public void setSuccessTableRecords(List<SingleInsertDataResponse> successTableRecords) {
        this.successTableRecords = successTableRecords;
    }

    public List<SingleInsertDataResponse> getFailedTableRecords() {
        return failedTableRecords;
    }

    public void setFailedTableRecords(List<SingleInsertDataResponse> failedTableRecords) {
        this.failedTableRecords = failedTableRecords;
    }
}
