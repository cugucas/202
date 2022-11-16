package cn.ac.iscas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/11/2 13:41
 */
@Configuration
@PropertySource("classpath:config/cetc10db.properties")
public class Cetc10DbConfig {
    @Value("${cetc10db.table}")
    private String cetc10DbTable;

    public static final String CETC10_DB_NAME = "CETC10DB";

    private List<String> cetc10DbTables;

    @PostConstruct
    public void init(){
        cetc10DbTables = Arrays.asList(this.cetc10DbTable.split(","));
    }

    public String getCetc10DbTable() {
        return cetc10DbTable;
    }

    public void setCetc10DbTable(String cetc10DbTable) {
        this.cetc10DbTable = cetc10DbTable;
    }

    public List<String> getCetc10DbTables() {
        return cetc10DbTables;
    }

    public void setCetc10DbTables(List<String> cetc10DbTables) {
        this.cetc10DbTables = cetc10DbTables;
    }


}
