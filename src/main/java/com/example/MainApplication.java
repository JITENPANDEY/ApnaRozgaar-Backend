package com.example;

import com.example.messaging.MsgService;
import com.example.messaging.SmsRequest;
import com.example.service.CompanyService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
//@EnableAsync
@AllArgsConstructor
@EnableAsync(proxyTargetClass = true)
//@CrossOrigin(origins ={"https://bluecollar-dot-hu18-groupa-java.et.r.appspot.com","https://localhost:4200"})
//@CrossOrigin(origins = "http://localhost:4200")
public class MainApplication {

    private MsgService msgService;
    private static Logger log = LoggerFactory.getLogger(MainApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);



        log.info("Dhruv Application has started");
    }
}
