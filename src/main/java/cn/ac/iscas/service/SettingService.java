package cn.ac.iscas.service;

import cn.ac.iscas.common.response.*;
import cn.ac.iscas.config.Cetc10DbConfig;
import cn.ac.iscas.config.DataSongConfig;
import cn.ac.iscas.dao.enity.Metadata;
import cn.ac.iscas.dao.mapper.MetadataMapper;
import cn.ac.iscas.utils.DataSongJsonUtils;
import cn.ac.iscas.utils.OkHttpClientUtils;
import com.iscas.datasong.lib.common.DataSongException;
import com.iscas.datasong.lib.common.DatabaseInfo;
import com.iscas.datasong.lib.common.Status;
import com.iscas.datasong.lib.response.manage.DBListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/10/31 10:13
 */

@Service
public class SettingService {
    static final Logger logger = LoggerFactory.getLogger(SettingService.class);

    @Autowired
    MetadataMapper metadataMapper;

    @Autowired
    DataSongConfig dataSongConfig;

    @Autowired
    private Cetc10DbConfig cetc10DbConfig;

    private static OkHttpClientUtils httpClientUtils = new OkHttpClientUtils();

    public BatchCreateDbTableResponse createAllDbTablesSkipExists() {
        Map<String, List<String>> existDbTables = getExistDbTables();
        BatchCreateDbTableResponse response = new BatchCreateDbTableResponse();
        List<SingleDbTableResponse> success = new ArrayList<>();
        List<SingleDbTableResponse> failed = new ArrayList<>();
        List<Metadata> metadatas = metadataMapper.selectAll();
        for (Metadata metadata : metadatas) {
            String db = metadata.getDatabase().replace("_", "").trim();
            String table = metadata.getTable();
            if (cetc10DbConfig.getCetc10DbTables().contains(table)) {
                db = Cetc10DbConfig.CETC10_DB_NAME;
            }
            String dbLowercase = db.toLowerCase(Locale.ROOT);
            if (existDbTables.containsKey(dbLowercase)) {
                if (existDbTables.get(dbLowercase).contains(table.toLowerCase(Locale.ROOT))) {
                    continue;
                } else {
                    Map<String, Object> createTableResult = createTable(db, table, metadata.getJson());
                    SingleDbTableResponse singleDbTableResponse = new SingleDbTableResponse();
                    singleDbTableResponse.setDbName(db);
                    singleDbTableResponse.setTableName(table);
                    int singleCreateDbTableStatus = (int) createTableResult.getOrDefault("status", -1);
                    singleDbTableResponse.setStatus(singleCreateDbTableStatus);
                    singleDbTableResponse.setInfo((String) createTableResult.getOrDefault("info", ""));
                    if (singleCreateDbTableStatus == Status.OK.getValue()) {
                        success.add(singleDbTableResponse);
                    } else {
                        failed.add(singleDbTableResponse);
                    }
                }
            } else {
                SingleDbTableResponse singleDbTableResponse = new SingleDbTableResponse();
                singleDbTableResponse.setDbName(db);
                singleDbTableResponse.setTableName(table);
                Map<String, Object> createDbResult = createDb(db);
                int singleCreateDbStatus = (int) createDbResult.getOrDefault("status", -1);
                if (singleCreateDbStatus == Status.OK.getValue()) {
                    Map<String, Object> createTableResult = createTable(db, table, metadata.getJson());
                    int singleCreateDbTableStatus = (int) createTableResult.getOrDefault("status", -1);
                    singleDbTableResponse.setStatus(singleCreateDbTableStatus);
                    singleDbTableResponse.setInfo((String) createTableResult.getOrDefault("info", ""));
                    if (singleCreateDbTableStatus == Status.OK.getValue()) {
                        success.add(singleDbTableResponse);
                    } else {
                        failed.add(singleDbTableResponse);
                    }
                } else {
                    singleDbTableResponse.setStatus(singleCreateDbStatus);
                    singleDbTableResponse.setInfo((String) createDbResult.getOrDefault("info", ""));
                    failed.add(singleDbTableResponse);
                }
            }
        }

        response.setFailed(failed);
        response.setSuccess(success);
        if (failed.size() > 0) {
            response.setStatus(Status.SERVER_ERROR.getValue());
            response.setInfo("Create all databases and tables failed!");
        } else {
            response.setStatus(Status.OK.getValue());
            response.setInfo("Create all databases and tables success!");
        }
        logger.info(DataSongJsonUtils.toJson(response));
        return response;
    }


    public BatchCreateDbTableResponse createAllDbTablesDeleteExists() {
        Map<String, List<String>> existDbTables = getExistDbTables();
        BatchCreateDbTableResponse response = new BatchCreateDbTableResponse();
        List<SingleDbTableResponse> success = new ArrayList<>();
        List<SingleDbTableResponse> failed = new ArrayList<>();
        List<Metadata> metadatas = metadataMapper.selectAll();
        for (Metadata metadata : metadatas) {
            String db = metadata.getDatabase().replace("_", "").trim();
            String table = metadata.getTable();
            if (cetc10DbConfig.getCetc10DbTables().contains(table)) {
                db = Cetc10DbConfig.CETC10_DB_NAME;
            }
            if (existDbTables.containsKey(db.toLowerCase(Locale.ROOT))) {
                if (!db.equalsIgnoreCase(Cetc10DbConfig.CETC10_DB_NAME)) {
                    Map<String, Object> deleteDbResponse = deleteDb(db);
                    int deleteDbStatus = (int) deleteDbResponse.getOrDefault("status", -1);
                    if (deleteDbStatus != Status.OK.getValue()) {
                        response.setStatus(deleteDbStatus);
                        response.setInfo((String) deleteDbResponse.getOrDefault("info", ""));
                        return response;
                    }
                } else {
                    if (existDbTables.get(db.toLowerCase(Locale.ROOT)).contains(table.toLowerCase(Locale.ROOT))) {
                        Map<String, Object> deleteTableResponse = deleteTable(db, table);
                        int deleteTableStatus = (int) deleteTableResponse.getOrDefault("status", -1);
                        if (deleteTableStatus != Status.OK.getValue()) {
                            response.setStatus(deleteTableStatus);
                            response.setInfo((String) deleteTableResponse.getOrDefault("info", ""));
                            return response;
                        }
                    }
                }
            }
            SingleDbTableResponse singleDbTableResponse = new SingleDbTableResponse();
            singleDbTableResponse.setDbName(db);
            singleDbTableResponse.setTableName(table);

            Map<String, Object> createDbResult = new HashMap<>(32);
            boolean alreadyCreatedCetc10Db = false;

            if (db.equalsIgnoreCase(Cetc10DbConfig.CETC10_DB_NAME)) {
                if (!existDbTables.containsKey(db.toLowerCase(Locale.ROOT)) && !alreadyCreatedCetc10Db) {
                    createDbResult = createDb(db);
                    alreadyCreatedCetc10Db = true;
                    int singleCreateDbStatus = (int) createDbResult.getOrDefault("status", -1);
                    if (singleCreateDbStatus != Status.OK.getValue()) {
                        singleDbTableResponse.setStatus(singleCreateDbStatus);
                        singleDbTableResponse.setInfo((String) createDbResult.getOrDefault("info", ""));
                        failed.add(singleDbTableResponse);
                    }
                }

                Map<String, Object> createTableResult = createTable(db, table, metadata.getJson());
                int singleCreateDbTableStatus = (int) createTableResult.getOrDefault("status", -1);
                singleDbTableResponse.setStatus(singleCreateDbTableStatus);
                singleDbTableResponse.setInfo((String) createTableResult.getOrDefault("info", ""));
                if (singleCreateDbTableStatus == Status.OK.getValue()) {
                    success.add(singleDbTableResponse);
                } else {
                    failed.add(singleDbTableResponse);
                }

            } else {
                createDbResult = createDb(db);
                int singleCreateDbStatus = (int) createDbResult.getOrDefault("status", -1);
                if (singleCreateDbStatus == Status.OK.getValue()) {
                    Map<String, Object> createTableResult = createTable(db, table, metadata.getJson());
                    int singleCreateDbTableStatus = (int) createTableResult.getOrDefault("status", -1);
                    singleDbTableResponse.setStatus(singleCreateDbTableStatus);
                    singleDbTableResponse.setInfo((String) createTableResult.getOrDefault("info", ""));
                    if (singleCreateDbTableStatus == Status.OK.getValue()) {
                        success.add(singleDbTableResponse);
                    } else {
                        failed.add(singleDbTableResponse);
                    }
                } else {
                    singleDbTableResponse.setStatus(singleCreateDbStatus);
                    singleDbTableResponse.setInfo((String) createDbResult.getOrDefault("info", ""));
                    failed.add(singleDbTableResponse);
                }
            }


        }

        response.setFailed(failed);
        response.setSuccess(success);
        if (failed.size() > 0) {
            response.setStatus(Status.SERVER_ERROR.getValue());
            response.setInfo("Create all databases and tables failed!");
        } else {
            response.setStatus(Status.OK.getValue());
            response.setInfo("Create all databases and tables success!");
        }
        logger.info(DataSongJsonUtils.toJson(response));
        return response;
    }


    public Map<String, List<String>> getExistDbTables() {
        String url = dataSongConfig.getManageUrl() + "_list";
        DBListResponse dbListResponse = null;
        try {
            String result = httpClientUtils.doGet(url);
            dbListResponse = DataSongJsonUtils.fromJson(result, DBListResponse.class);
        } catch (IOException | DataSongException e) {
            e.printStackTrace();
        }

        Map<String, List<String>> dbTables = new HashMap<>(64);
        for (DatabaseInfo dbTable : dbListResponse.getDatabases()) {
            dbTables.put(dbTable.getDBName(), dbTable.getTables());
        }
        return dbTables;
    }

    public Map<String, Object> createDb(String dbName) {
        Map<String, Object> resultMap = null;
        try {
            String result = httpClientUtils.doPut(dataSongConfig.getSettingUrl() + dbName, (String) "");
            resultMap = DataSongJsonUtils.fromJson(result, Map.class);
        } catch (IOException | DataSongException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    public Map<String, Object> createTable(String dbName, String tableName, String schema) {
        Map<String, Object> resultMap = null;
        try {
            String result = httpClientUtils.doPut(dataSongConfig.getSettingUrl() + dbName + "/" + tableName, schema);
            resultMap = DataSongJsonUtils.fromJson(result, Map.class);
        } catch (IOException | DataSongException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    public Map<String, Object> deleteDb(String dbName) {
        String result = null;
        Map<String, Object> resultMap = new HashMap<>(16);
        try {
            result = httpClientUtils.doDelete(dataSongConfig.getSettingUrl() + dbName);
            resultMap = DataSongJsonUtils.fromJson(result, Map.class);
        } catch (IOException | DataSongException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    public Map<String, Object> deleteTable(String dbName, String tableName) {
        String result = null;
        Map<String, Object> resultMap = new HashMap<>(16);
        try {
            result = httpClientUtils.doDelete(dataSongConfig.getSettingUrl() + dbName + "/" + tableName);
            resultMap = DataSongJsonUtils.fromJson(result, Map.class);
        } catch (IOException | DataSongException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    public ClearMajorTablesResponse clearMajorTables(String major) {
        ClearMajorTablesResponse response = new ClearMajorTablesResponse();
        int status = Status.OK.getValue();
        String info = String.format("All tables in major [%s] cleared success", major);
        List<SingleDbTableResponse> success = new ArrayList<>();
        List<SingleDbTableResponse> failed = new ArrayList<>();
        List<Metadata> tablesInMajor = metadataMapper.selectByMajor(major);
        for (Metadata tableInMajor : tablesInMajor) {
            SingleDbTableResponse singleDbTableResponse = new SingleDbTableResponse();
            singleDbTableResponse.setDbName(tableInMajor.getDatabase());
            singleDbTableResponse.setTableName(tableInMajor.getTable());
            Map<String, Object> clearTableResponse = clearTable(tableInMajor.getDatabase(), tableInMajor.getTable());
            int clearTableStatus = (int) clearTableResponse.getOrDefault("status", -1);
            singleDbTableResponse.setInfo(clearTableResponse.getOrDefault("info", "").toString());
            singleDbTableResponse.setStatus(clearTableStatus);
            if (clearTableStatus != Status.OK.getValue()) {
                failed.add(singleDbTableResponse);
                status = Status.SERVER_ERROR.getValue();
                info = String.format("Some tables in major [%s] cleared failed", major);
            } else {
                success.add(singleDbTableResponse);
            }
        }
        response.setSuccess(success);
        response.setFailed(failed);
        response.setInfo(info);
        response.setStatus(status);
        return response;
    }

//    public ClearDbTableResponse clearDbTable(String dbName, String tableName) {
//        BaseResponse response = new BaseResponse();
//        String result = null;
//        Map<String, Object> resultMap = new HashMap<>(16);
//        try {
//            result = httpClientUtils.doDelete(dataSongConfig.getSettingUrl() + dbName + "/" + tableName + "/clear");
//            resultMap = DataSongJsonUtils.fromJson(result, Map.class);
//        } catch (IOException | DataSongException e) {
//            e.printStackTrace();
//        }
//
//        return resultMap;
//    }

    public Map<String, Object> clearTable(String dbName, String tableName) {
        String result = null;
        Map<String, Object> resultMap = new HashMap<>(16);
        try {
            result = httpClientUtils.doDelete(dataSongConfig.getSettingUrl() + dbName + "/" + tableName + "/clear");
            resultMap = DataSongJsonUtils.fromJson(result, Map.class);
        } catch (IOException | DataSongException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

}
