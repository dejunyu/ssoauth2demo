package com.lagou;

import com.lagou.service.UserServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.lagou"})
public class MyApplication {

    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(MyApplication.class, args);

        UserServiceImpl.startServer("127.0.0.1",8990);
    }



}
