package com.c4cydonia.file.metadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class},
        scanBasePackages = {"com.c4cydonia.file.metadata"}
)
@RestController
public class App 
{

    @GetMapping("/")
    @ResponseBody
    public String hello() {
        return "Hello from file metadata metadata API";
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
