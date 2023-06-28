package io.camunda.bot.camundaUtil.worker;

import io.camunda.bot.entities.client.Client;
import io.camunda.bot.entities.client.ClientChatVariables;
import io.camunda.bot.entities.client.ClientVariables;
import io.camunda.bot.entities.client.ClientVariablesRelation;
import io.camunda.bot.entities.tours.Tours;
import io.camunda.bot.repository.*;
import io.camunda.bot.telegramBot.TelegramBot;
import io.camunda.bot.utility.Util;
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


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
  private ClientVariablesRepository clientVariablesRepository;
  @Autowired
  private TelegramBot telegramBot;
  @Autowired
  private ToursRepository toursRepository;
  @Autowired
  private ClientVariablesRelationRepository clientVariablesRelationRepository;


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

    Map <String,String> result;
    switch (job.getElementId()){
      case "decide-needs":
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
        break;
      case "continue-or-end":
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
        break;
      case "tour-selection":
        List<Tours> toursList = toursRepository.findAllByOrderByTourStartAsc();
        for(Tours tour : toursList){
          if(("/"+tour.getTourName()).equals(message)){
            result = Map.of("destination", message);
            System.out.println("We have a result: " + message);
            zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_destination","true","destination",message)).send().join();
            return;
          }
        }
        zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_destination","false")).send().join();
        break;
      case "user-display-options":
        //"options" is the required param
        switch (message) {
          case "/information":
            result = Map.of("options", "information");
            break;
          case "/participants":
            result = Map.of("options", "participants");
            break;
          case "/signup":
            result = Map.of("options", "signup");
            break;
          case "/trip":
            result = Map.of("options", "trip");
            break;
          default:
            zeebeClient.newFailCommand(job.getKey()).retries(10).send().join();
            return;
        }
        zeebeClient.newCompleteCommand(job.getKey()).variables(result).send().join();
        System.out.println("message: " + message);
        break;
      case "name-input":
        System.out.println("Name input prompt");
        String regex = "^[A-Za-z ]+$";
        if(message.matches(regex) && message.split(" ").length >=2){
          System.out.println("valid regex");
          String[] nameArray = message.split(" ");
          String firstName = Arrays.stream(nameArray)
                  .limit(nameArray.length - 1)
                  .collect(Collectors.joining(" "));
          String lastName = nameArray[nameArray.length-1];
          ClientVariables clientVariables = new ClientVariables(firstName,lastName);
          ClientVariables savedClientVariables = clientVariablesRepository.save(clientVariables);
          clientVariablesRelationRepository.save(new ClientVariablesRelation(client, savedClientVariables));
          zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("name_valid","true")).send().join();
        }else{
          System.out.println("I am invalid: " + message.matches(regex));
          //invalid input
          zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("name_valid","false")).send().join();
        }
        break;
      case "birthday-input":{
        //message is supposed to be a date in the yyyy-MM-dd format
        if(!Util.validateDate(message, "yyyy-MM-dd")){
          System.out.println("message");
          zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_bdate", "false")).send().join();
          return;
        }
        ClientVariables clientVariables = clientVariablesRelationRepository.findClientVariablesRelationByClient(clientRepository.findClientByChatId(job.getVariablesAsMap().get("chat_id").toString())).getClientVariables();
        System.out.println("date:" + message );
        clientVariables.setClientBirthday(Util.stringToDate(message, "YYYY-MM-DD"));
        zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_bdate","true")).send().join();
        break;
      }


      case "nationality-input":{
        if(message.matches("^[A-Za-z ]+$")){
          ClientVariables clientVariables = clientVariablesRelationRepository.findClientVariablesRelationByClient(clientRepository.findClientByChatId(job.getVariablesAsMap().get("chat_id").toString())).getClientVariables();
          clientVariables.setClientNationality(message);
          zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_nat","true")).send().join();
        }else {
          zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_nat","false")).send().join();
        }
        break;
      } case "passport-input":{
        ClientVariables clientVariables = clientVariablesRelationRepository.findClientVariablesRelationByClient(clientRepository.findClientByChatId(job.getVariablesAsMap().get("chat_id").toString())).getClientVariables();
        clientVariables.setClientPassportid(message);
        zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_passid","true")).send().join();
        break;
      }
      case "address-input":{
          var split_message = message.split(",");
          if(split_message.length!=4){
              zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_address","true")).send().join();
              return;
          }
          var plz = split_message[1].replaceAll("\\s+","");
          if (!plz.matches("[0-9]+")){
            zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_address","true")).send().join();
            return;
          }

          var city = split_message[2].replaceAll("\\s+","");
          var country = split_message[3].replaceAll("\\s+","");
          if(!city.matches("[a-zA-Z]+")){
            zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_address","true")).send().join();
            return;
          }
          if(!country.matches("[a-zA-Z]+")){
            zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_address","true")).send().join();
            return;
          }

        ClientVariables clientVariables = clientVariablesRelationRepository.findClientVariablesRelationByClient(clientRepository.findClientByChatId(job.getVariablesAsMap().get("chat_id").toString())).getClientVariables();
        clientVariables.setClientAddress(message);
        zeebeClient.newCompleteCommand(job.getKey()).variables(Map.of("valid_address","true")).send().join();
        break;
      }


    }



  }



  @Transactional
  public void endProcess(String chat_id){
    clientProcessRepository.deleteClientProcessesByClient(clientRepository.findClientByChatId(chat_id));
  }


}
