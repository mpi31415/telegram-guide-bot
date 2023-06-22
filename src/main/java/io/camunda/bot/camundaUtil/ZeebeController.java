package io.camunda.bot.camundaUtil;

import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import org.checkerframework.common.value.qual.StringVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ZeebeController {

    @Autowired
    private ZeebeClientLifecycle client;


    public String createInstance(String id){
        var processInstance = client.newCreateInstanceCommand()
                .bpmnProcessId("GuideHelper")
                .latestVersion()
                .variables(Map.of("chat_id",id))
                .send()
                .join();

        return String.valueOf(processInstance.getProcessInstanceKey());
    }

    public void stopInstance(String instance_id){
        System.out.println(instance_id);
        client.newCancelInstanceCommand(Long.parseLong(instance_id)).send().join();
        System.out.println("stopped instance");
    }
}
