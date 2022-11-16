package cn.ac.iscas.controller;

import cn.ac.iscas.common.response.BatchInsertDataResponse;
import cn.ac.iscas.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping({"/data"})
public class DataController {

    @Autowired
    DataService dataService;


    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Map<String, BatchInsertDataResponse> batchSave(@RequestBody String path) {
        return dataService.saveAllData(path);
    }

    /**
     * @description: 导入指定文件夹下的csv文件
     * @param: path
     * @return: java.util.Map<java.lang.String,cn.ac.iscas.common.response.BatchInsertDataResponse>
     * @author LJian
     * @date: 2022/11/10 8:29
     */
    @RequestMapping(value = "/save/spec", method = RequestMethod.POST)
    public BatchInsertDataResponse batchSaveSpec(@RequestBody String path) {
        return dataService.saveSpecData(path);
    }

}
