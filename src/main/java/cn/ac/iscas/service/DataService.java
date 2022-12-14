package cn.ac.iscas.service;

import cn.ac.iscas.common.response.BatchInsertDataResponse;
import cn.ac.iscas.common.response.SingleInsertDataResponse;
import cn.ac.iscas.config.Cetc10DbConfig;
import cn.ac.iscas.config.CsvFileConfig;
import cn.ac.iscas.config.DataSongConfig;
import cn.ac.iscas.utils.DataSongJsonUtils;
import cn.ac.iscas.utils.OkHttpClientUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.iscas.datasong.lib.common.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/10/28 13:17
 */
@Service
public class DataService {
    static final Logger logger = LoggerFactory.getLogger(DataService.class);
    @Autowired
    private DataSongConfig dataSongConfig;

    @Autowired
    private Cetc10DbConfig cetc10DbConfig;

    @Autowired
    private CsvFileConfig csvFileConfig;

    private static final String CETC10_DB_NAME = "CETC10DB";

    //private static List<String> cetc10DbTables;

//    @PostConstruct
//    public void init(){
//        cetc10DbTables = Arrays.asList(cetc10DbConfig.getCetc10DbTable().split(","));
//    }

    private static OkHttpClientUtils httpClientUtils = new OkHttpClientUtils();

    public Map<String, BatchInsertDataResponse> saveAllData(String rootPath) {
        String nfsRootPath = csvFileConfig.getNfsRootPath();
        if (!nfsRootPath.endsWith(File.separator)) {
            nfsRootPath += File.separator;
        }
        Map<String, BatchInsertDataResponse> response = new HashMap<>(8);
        if (!rootPath.endsWith(File.separator)) {
            rootPath += File.separator;
        }
        String radarFolderPath = nfsRootPath + rootPath + csvFileConfig.getRadarFolderPath();
        String radioFolderPath = nfsRootPath + rootPath + csvFileConfig.getRadioFolderPath();
        String sonarFolderPath = nfsRootPath + rootPath + csvFileConfig.getSonarFolderPath();
        String mergeFolderPath = nfsRootPath + rootPath + csvFileConfig.getMergeFolderPath();

        logger.info(String.format("Radar csv folder: %s", radarFolderPath));
        logger.info(String.format("Signal csv folder: %s", radioFolderPath));
        logger.info(String.format("Sonar csv folder: %s", sonarFolderPath));
        logger.info(String.format("Fusion csv folder: %s", mergeFolderPath));

        response.put(csvFileConfig.getRadarName(), insert(radarFolderPath));
        response.put(csvFileConfig.getRadioName(), insert(radioFolderPath));
        response.put(csvFileConfig.getSonarName(), insert(sonarFolderPath));
        response.put(csvFileConfig.getMergeName(), insert(mergeFolderPath));
        logger.info(DataSongJsonUtils.toJson(response));
        return response;
    }

    public BatchInsertDataResponse saveSpecData(String rootPath) {
        return insert(rootPath);
    }

    public BatchInsertDataResponse insert(String csvFolderPath) {
        BatchInsertDataResponse response = new BatchInsertDataResponse();
        response.setBeginTime(new Date());
        String info = "";
        int status = Status.OK.getValue();
        List<SingleInsertDataResponse> singleSuccessInsertDataResponses = new ArrayList<>();
        List<SingleInsertDataResponse> singleFailedInsertDataResponses = new ArrayList<>();
        File csvDir = new File(csvFolderPath);
        int successFileCount = 0;
        int failedFileCount = 0;
        if (!csvDir.exists()) {
            response.setStatus(Status.PARAM_ERROR.getValue());
            response.setInfo(String.format("Path: [%s] not exists!", csvFolderPath));
            response.setEndTime(new Date());
            return response;
        }
        if (csvDir.isDirectory()) {
            for (File csvFile : csvDir.listFiles()) {
                if (!csvFile.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
                    continue;
                }
                SingleInsertDataResponse singleInsertDataResponse = singleFileInsert(csvFile);
                if (singleInsertDataResponse.getStatus() == Status.OK.getValue()) {
                    singleSuccessInsertDataResponses.add(singleInsertDataResponse);
                    successFileCount++;
                    logger.info(String.format("File [%s] finished success.", csvFile.getName()));
                } else {
                    singleFailedInsertDataResponses.add(singleInsertDataResponse);
                    status = Status.SERVER_ERROR.getValue();
                    failedFileCount++;
                    logger.info(String.format("File [%s] finished failed, reason: %s", csvFile.getName(), singleInsertDataResponse.getInfo()));
                }
            }
        } else {
            SingleInsertDataResponse singleInsertDataResponse = singleFileInsert(csvDir);
            if (singleInsertDataResponse.getStatus() == Status.OK.getValue()) {
                singleSuccessInsertDataResponses.add(singleInsertDataResponse);
                successFileCount++;
                logger.info(String.format("File [%s] finished success.", csvDir.getName()));
            } else {
                singleFailedInsertDataResponses.add(singleInsertDataResponse);
                status = Status.SERVER_ERROR.getValue();
                failedFileCount++;
                logger.info(String.format("File [%s] finished failed, reason: %s", csvDir.getName(), singleInsertDataResponse.getInfo()));
            }
        }
        if (status == Status.OK.getValue()) {
            info = String.format("All %d csv files insert successful.", successFileCount);
        } else {
            info = String.format("All %d csv files insert finished, %d successed, %d failed", (successFileCount + failedFileCount), successFileCount, failedFileCount);
        }
        response.setStatus(status);
        response.setInfo(info);
        response.setEndTime(new Date());
        response.setSuccessTableRecords(singleSuccessInsertDataResponses);
        response.setFailedTableRecords(singleFailedInsertDataResponses);
        logger.info(DataSongJsonUtils.toJson(response));

        return response;
    }

    public SingleInsertDataResponse singleFileInsert(File csvFile) {
        //??????CSV????????????
        Long rowCount = 0L;
        int batchSize = 2000;
        int status = 200;
        String dbName = csvFile.getName();
        String tableName = csvFile.getName();
        dbName = dbName.substring(0, dbName.lastIndexOf(".")).replace("_", "").trim();
        tableName = tableName.substring(0, tableName.lastIndexOf(".")).trim();
        if (cetc10DbConfig.getCetc10DbTables().contains(tableName)) {
            dbName = CETC10_DB_NAME;
        }
        String info = String.format("Insert db:[%s], table:[%s] success!", dbName, tableName);

        String datasongDataUrl = dataSongConfig.getDataUrl() + dbName + "/" + tableName;
        SingleInsertDataResponse response = new SingleInsertDataResponse();
        response.setBeginTime(new Date());
        List<Map<String, Object>> rows = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"));
            String line;
            String[] headers = null;
            //List<Integer> headerNums = new ArrayList<>();
            List<Integer> srcDataIndex = new ArrayList<>();
            int unmatch = 0;
            while ((line = reader.readLine()) != null) {

                //CSV?????????????????????????????????","
                String[] elements = line.split(",", -1);

                for (int i = 0; i < elements.length; i++) {
                    elements[i] = elements[i].replace("\"", "").trim();
                }

                if (headers == null) {
                    headers = new String[elements.length];
                    for (int i = 0; i < elements.length; i++) {
                        headers[i] = elements[i];
                    }
                    //??????????????????????????????
                    batchSize = getBatchSize(headers.length);
                    continue;
                }

                Map<String, Object> row = new LinkedHashMap<>();
                //?????????????????????????????????????????????
                if (headers.length == elements.length) {
                    //??????????????????
                    for (int i = 0; i < headers.length; i++) {
                        //???????????????????????????map??????
                        row.put(headers[i], elements[i]);
                    }
                    //??????????????????
                    rows.add(row);
                } else {
                    //?????????????????????????????????????????????????????????????????????
//                    System.out.println(row.keySet().stream()
//                            .map(key -> key + "=" + row.get(key))
//                            .collect(Collectors.joining(", ", "{", "}")));
                    unmatch++;
                }

                //????????????????????????batchSize???????????????DataSong
                if (rows.size() >= batchSize) {
                    Map<String, Object> datasongResponse = sendToDataSong(datasongDataUrl, rows);
                    int datasongStatus = (int)(datasongResponse.getOrDefault("status", 0));
                    if (datasongStatus == Status.OK.getValue()) {
                        rowCount += rows.size();
                    } else {
                        String datasongInfo = (String)(datasongResponse.getOrDefault("info", 0));
                        status = datasongStatus;
                        info = datasongInfo;
                        rows.clear();
                        break;
                    }
                    //????????????
                    rows.clear();
                }

            }

            //??????????????????????????????DataSong
            if (!rows.isEmpty()) {
                Map<String, Object> datasongResponse = sendToDataSong(datasongDataUrl, rows);
                int datasongStatus = (int)(datasongResponse.getOrDefault("status", 0));
                if (datasongStatus == Status.OK.getValue()) {
                    rowCount += rows.size();
                } else {
                    String datasongInfo = (String)(datasongResponse.getOrDefault("info", 0));
                    status = datasongStatus;
                    info = datasongInfo;
                }
                //????????????
                rows.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //???????????????
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        response.setInfo(info);
        response.setStatus(status);
        response.setDbName(dbName);
        response.setTableName(tableName);
        response.setEndTime(new Date());
        response.setSuccessRowNum(rowCount);
        response.setBatchSize(batchSize);
        return response;
    }

    //?????????DataSong??????
    private static Map<String, Object> sendToDataSong(String datasongDataUrl, List<Map<String, Object>> datas) {
        Map<String, Object> response = new HashMap<>();
        String json = DataSongJsonUtils.toJson(datas);
        try {
            String result = httpClientUtils.doPut(datasongDataUrl, json);
            response = DataSongJsonUtils.fromJson(result, new TypeReference<HashMap<String, Object>>(){});
            int status = (int)(response.getOrDefault("status", 0));
            if (status != Status.OK.getValue()) {
                int sleepTime = 1000;
                //???????????????=711?????????ES?????????????????????????????????
                while (status == 711 && sleepTime <= 60 * 1000) {
                    //????????????????????????????????????
                    System.err.println(response.getOrDefault("info", ""));
                    //??????1s
                    sleep(sleepTime);
                    //???????????????????????????
                    result = httpClientUtils.doPut(datasongDataUrl, json);
                    response = DataSongJsonUtils.fromJson(result, new TypeReference<HashMap<String, Object>>(){});
                    status = (int)(response.getOrDefault("status", 0));
                    //??????????????????0.5???
                    sleepTime += 500;
                }

                //???????????????????????????200
                if (status != Status.OK.getValue()) {
                    System.err.println(result);
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return response;
    }

    private int getBatchSize(int columnNums) {
        if (columnNums <= dataSongConfig.getLowerBatchSizeThread()) {
            return dataSongConfig.getMaxBatchSize();
        } else if (columnNums > dataSongConfig.getLowerBatchSizeThread() && columnNums <= dataSongConfig.getUpperBatchSizeThread()) {
            return dataSongConfig.getMiddleBatchSize();
        }
        return dataSongConfig.getMinBatchSize();
    }
}
