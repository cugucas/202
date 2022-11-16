package cn.ac.iscas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/10/28 8:53
 */
@EnableScheduling
@SpringBootApplication
public class BigDataManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BigDataManagerApplication.class, args);
    }

}
