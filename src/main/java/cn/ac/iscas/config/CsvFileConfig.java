package cn.ac.iscas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/11/3 14:14
 */
@Configuration
@PropertySource("classpath:config/csvfile.properties")
public class CsvFileConfig {
    @Value("${radar.folder.path}")
    private String radarFolderPath;

    @Value("${radio.folder.path}")
    private String radioFolderPath;

    @Value("${sonar.folder.path}")
    private String sonarFolderPath;

    @Value("${merge.folder.path}")
    private String mergeFolderPath;

    @Value("${radar.name}")
    private String radarName;

    @Value("${radio.name}")
    private String radioName;

    @Value("${sonar.name}")
    private String sonarName;

    @Value("${merge.name}")
    private String mergeName;

    @Value("${nfs.root.path}")
    private String nfsRootPath;

    public String getRadarFolderPath() {
        return radarFolderPath;
    }

    public void setRadarFolderPath(String radarFolderPath) {
        this.radarFolderPath = radarFolderPath;
    }

    public String getRadioFolderPath() {
        return radioFolderPath;
    }

    public void setRadioFolderPath(String radioFolderPath) {
        this.radioFolderPath = radioFolderPath;
    }

    public String getSonarFolderPath() {
        return sonarFolderPath;
    }

    public void setSonarFolderPath(String sonarFolderPath) {
        this.sonarFolderPath = sonarFolderPath;
    }

    public String getMergeFolderPath() {
        return mergeFolderPath;
    }

    public void setMergeFolderPath(String mergeFolderPath) {
        this.mergeFolderPath = mergeFolderPath;
    }

    public String getRadarName() {
        return radarName;
    }

    public void setRadarName(String radarName) {
        this.radarName = radarName;
    }

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }

    public String getSonarName() {
        return sonarName;
    }

    public void setSonarName(String sonarName) {
        this.sonarName = sonarName;
    }

    public String getMergeName() {
        return mergeName;
    }

    public void setMergeName(String mergeName) {
        this.mergeName = mergeName;
    }

    public String getNfsRootPath() {
        return nfsRootPath;
    }

    public void setNfsRootPath(String nfsRootPath) {
        this.nfsRootPath = nfsRootPath;
    }
}
