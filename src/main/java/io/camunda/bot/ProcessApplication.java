package io.camunda.bot;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@Deployment(resources = "classpath:telegrambotdiagram.bpmn")
public class ProcessApplication {



  public static void main(String[] args) {
    SpringApplication.run(ProcessApplication.class, args);
  }


}
