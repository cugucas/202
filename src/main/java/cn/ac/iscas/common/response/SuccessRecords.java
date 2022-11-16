package cn.ac.iscas.common.response;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/10/28 15:23
 */
public class SuccessRecords {
    private String dbName;
    private String tableName;
    private Long recordsNum;

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

    public Long getRecordsNum() {
        return recordsNum;
    }

    public void setRecordsNum(Long recordsNum) {
        this.recordsNum = recordsNum;
    }
}
