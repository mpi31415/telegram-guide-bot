package io.camunda.bot.camundaUtil.worker;

import io.camunda.bot.entities.client.Client;
import io.camunda.bot.entities.client.ClientChatVariables;
import io.camunda.bot.entities.tours.Tours;
import io.camunda.bot.repository.ClientChatVariablesRepository;
import io.camunda.bot.repository.ClientProcessRepository;
import io.camunda.bot.repository.ClientRepository;
import io.camunda.bot.repository.ToursRepository;
import io.camunda.bot.telegramBot.ConnectorService;
import io.camunda.bot.telegramBot.TelegramBot;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Transactional
public class UserTaskWorker {

  private final static Logger LOG = LoggerFactory.getLogger(UserTaskWorker.class);
  @Autowired
  private ZeebeClientLifecycle zeebeClient;

  @Autowired
  private ClientChatVariablesRepository clientChatVariablesRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private ClientProcessRepository clientProcessRepository;


  @Autowired
  private TelegramBot telegramBot;
  @Autowired
  private ToursRepository toursRepository;


  @JobWorker(type = "io.camunda.zeebe:userTask", autoComplete = false, pollInterval = 1000)
  public void userTaskHandler(final ActivatedJob job) throws TelegramApiException {
    Client client = clientRepository.findClientByChatId((String) job.getVariablesAsMap().get("chat_id"));
    if(!clientChatVariablesRepository.existsClientChatVariablesByClient(client)){
      zeebeClient.newFailCommand(job.getKey()).retries(10).send().join();
      return;
    }

    ClientChatVariables clientChatVariables = clientChatVariablesRepository.findClientChatVariablesByClient(client);
    var variable = clientChatVariables.getVariables();

    clientChatVariablesRepository.delete(clientChatVariables);
    JSONObject jsonObject = new JSONObject(variable);
    String message = String.valueOf(jsonObject.get("message"));

    clientChatVariablesRepository.delete(clientChatVariables);

    if(job.getElementId().equals("decide-needs")){
      switch (message){
        case "/tours":
          zeebeClient.newCompleteCommand(job.getKey())
                  .variables(Map.of("needs", "tours"))
                  .send().join();
          System.out.println(job.getElementId() +  " " + job.getProcessDefinitionKey());

          break;
        case "/about":
          zeebeClient.newCompleteCommand(job.getKey())
                  .variables(Map.of("needs", "about"))
                  .send().join();
          System.out.println("I completed this job");

          break;
        default:
          SendMessage sm = new SendMessage();
          sm.setChatId(client.getChatId());
          sm.setText("Your input: " + jsonObject.get("message") + "is wrong, please try again");
          telegramBot.sendMessage(sm);

          System.out.println("Your input is wrong");
          zeebeClient.newFailCommand(job.getKey()).retries(10).send().join();

      }
    }
    else if(job.getElementId().equals("continue-or-end")){
      switch (message){
        case "/end":
          Map<String, String> map =  Map.of("dest_or_end", "end");
          zeebeClient.newCompleteCommand(job.getKey()).variables(map).send().join();
          System.out.println("end");
          SendMessage sm = new SendMessage();
          sm.setChatId(client.getChatId());
          sm.setText("Thank you for using our bot");
          telegramBot.sendMessage(sm);
          endProcess(client.getChatId());
          break;
        case "/tours":
          Map<String, String> destination =  Map.of("dest_or_end", "dest");
          zeebeClient.newCompleteCommand(job.getKey()).variables(destination).send().join();

          break;
        default:
          zeebeClient.newFailCommand(job.getKey()).retries(10).send().join();
      }
    }
    else if(job.getElementId().equals("tour-selection")){
      Map <String,String> result;
      List<Tours> toursList = toursRepository.findAllByOrderByTourStartAsc();
      for(Tours tour : toursList){
        if(("/"+tour.getTourName()).equals(message)){
          result = Map.of("destination", message);
          System.out.println("We have a result: " + message);
          zeebeClient.newCompleteCommand(job.getKey()).variables(result).send().join();
          return;
        }
      }
      zeebeClient.newFailCommand(job.getKey()).retries(10).send().join();
    }
    else if(job.getElementId().equals("user-display-options")){
          Map<String, String> result;
          //"options" is the required param
          if(message.equals("/information")){
              result = Map.of("options", "information");
          }else if(message.equals("/participants")){
            result = Map.of("options", "participants");
          }else if(message.equals("/signup")){
            result = Map.of("options", "signup");
          }else{
            zeebeClient.newFailCommand(job.getKey()).retries(10).send().join();
            return;
          }
          zeebeClient.newCompleteCommand(job.getKey()).variables(result).send().join();
       System.out.println("message: " + message);

    }

    System.out.println("var is: " + jsonObject.get("message") + " current stage is " + job.getElementId());


  }



  @Transactional
  public void endProcess(String chat_id){
    clientProcessRepository.deleteClientProcessesByClient(clientRepository.findClientByChatId(chat_id));
  }


}
