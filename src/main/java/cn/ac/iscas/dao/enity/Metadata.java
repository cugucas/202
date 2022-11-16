package cn.ac.iscas.dao.enity;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/10/31 10:29
 */
public class Metadata {
    private int id;
    private String database;
    private String table;
    private String major;
    private String json;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
