package cn.ac.iscas.common.response;

import java.util.Date;
import java.util.Map;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/10/28 14:52
 */
public class SingleInsertDataResponse extends BaseResponse{
    private Date beginTime;
    private Date endTime;
    private String dbName;
    private String tableName;
    private Long successRowNum;
    private int batchSize;

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

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getSuccessRowNum() {
        return successRowNum;
    }

    public void setSuccessRowNum(Long successRowNum) {
        this.successRowNum = successRowNum;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
