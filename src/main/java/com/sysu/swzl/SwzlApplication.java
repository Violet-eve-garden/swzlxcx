package com.sysu.swzl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SwzlApplication {
    public static void main(String[] args) {
        SpringApplication.run(SwzlApplication.class, args);
    }

}
