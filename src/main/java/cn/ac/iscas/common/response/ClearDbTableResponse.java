package cn.ac.iscas.common.response;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/11/16 18:37
 */
public class ClearDbTableResponse extends BaseResponse{
    private String dbName;
    private String tableName;

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
}
