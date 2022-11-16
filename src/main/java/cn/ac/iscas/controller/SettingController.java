package cn.ac.iscas.controller;

import cn.ac.iscas.common.response.BatchCreateDbTableResponse;
import cn.ac.iscas.common.response.ClearMajorTablesResponse;
import cn.ac.iscas.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/10/28 20:42
 */

@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping({"/setting"})
public class SettingController {

    @Autowired
    SettingService settingService;


    @RequestMapping(value = "/create/skip", method = RequestMethod.GET)
    public BatchCreateDbTableResponse createAllDbTablesSkipExists() {
        return settingService.createAllDbTablesSkipExists();
    }

    @RequestMapping(value = "/create/delete", method = RequestMethod.GET)
    public BatchCreateDbTableResponse createAllDbTablesDeleteExists() {
        return settingService.createAllDbTablesDeleteExists();
    }

    @RequestMapping(value = "/clear/{dbName}/{tableName}", method = RequestMethod.DELETE)
    public Map<String, Object> clearDbTable(@PathVariable String dbName, @PathVariable String tableName) {
        return settingService.clearTable(dbName, tableName);
    }

    @RequestMapping(value = "/clear/major/{major}", method = RequestMethod.DELETE)
    public ClearMajorTablesResponse clearDbTable(@PathVariable String major) {
        return settingService.clearMajorTables(major);
    }
}
