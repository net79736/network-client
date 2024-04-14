package org.httptest.networkclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class NetworkClientApplication implements CommandLineRunner {
    ApplicationContext applicationContext;

    @Autowired
    public NetworkClientApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static void main(String[] args) {
        SpringApplication.run(NetworkClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beanDefinitionNames);
        for (String beanDefinitionName : beanDefinitionNames) {
            log.warn(beanDefinitionName);
        }
    }
}
