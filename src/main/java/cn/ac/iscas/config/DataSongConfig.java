package cn.ac.iscas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/10/28 14:37
 */
@Configuration
@PropertySource("classpath:config/datasong.properties")
public class DataSongConfig {
    @Value("${datasong.ip}")
    private String datasongIp;

    @Value("${datasong.port}")
    private String datasongPort;

    @Value("${datasong.data.url}")
    private String dataUrl;

    @Value("${datasong.setting.url}")
    private String settingUrl;

    @Value("${datasong.manage.url}")
    private String manageUrl;

    @Value("${datasong.batchsize.max}")
    private int maxBatchSize;

    @Value("${datasong.batchsize.middle}")
    private int middleBatchSize;

    @Value("${datasong.batchsize.min}")
    private int minBatchSize;

    @Value("${datasong.batchsize.column.lower.thread}")
    private int lowerBatchSizeThread;

    @Value("${datasong.batchsize.column.upper.thread}")
    private int upperBatchSizeThread;

    public String getDatasongIp() {
        return datasongIp;
    }

    public void setDatasongIp(String datasongIp) {
        this.datasongIp = datasongIp;
    }

    public String getDatasongPort() {
        return datasongPort;
    }

    public void setDatasongPort(String datasongPort) {
        this.datasongPort = datasongPort;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getSettingUrl() {
        return settingUrl;
    }

    public void setSettingUrl(String settingUrl) {
        this.settingUrl = settingUrl;
    }

    public String getManageUrl() {
        return manageUrl;
    }

    public void setManageUrl(String manageUrl) {
        this.manageUrl = manageUrl;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public int getMiddleBatchSize() {
        return middleBatchSize;
    }

    public void setMiddleBatchSize(int middleBatchSize) {
        this.middleBatchSize = middleBatchSize;
    }

    public int getMinBatchSize() {
        return minBatchSize;
    }

    public void setMinBatchSize(int minBatchSize) {
        this.minBatchSize = minBatchSize;
    }

    public int getLowerBatchSizeThread() {
        return lowerBatchSizeThread;
    }

    public void setLowerBatchSizeThread(int lowerBatchSizeThread) {
        this.lowerBatchSizeThread = lowerBatchSizeThread;
    }

    public int getUpperBatchSizeThread() {
        return upperBatchSizeThread;
    }

    public void setUpperBatchSizeThread(int upperBatchSizeThread) {
        this.upperBatchSizeThread = upperBatchSizeThread;
    }
}
