package io.camunda.bot.telegramBot;

import io.camunda.bot.camundaUtil.ZeebeController;
import io.camunda.bot.entities.client.Client;
import io.camunda.bot.entities.client.ClientChatVariables;
import io.camunda.bot.entities.client.ClientProcesses;
import io.camunda.bot.repository.ClientChatVariablesRepository;
import io.camunda.bot.repository.ClientProcessRepository;
import io.camunda.bot.repository.ClientRepository;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

@Service
public class ConnectorService {
    @Autowired
    private ZeebeController controller;

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientProcessRepository clientProcessRepository;
    @Autowired
    private ClientChatVariablesRepository clientChatVariablesRepository;





    @Transactional
    public void createNewProcess(Long chat_id){

        String process_id = controller.createInstance(String.valueOf(chat_id));
        Client client = new Client(String.valueOf(chat_id));
        if(clientRepository.existsByChatId(String.valueOf(chat_id))){
            client = clientRepository.findClientByChatId(String.valueOf(chat_id));
            if(clientProcessRepository.existsClientProcessesByClient(clientRepository.findClientByChatId(String.valueOf(chat_id)))){
                ClientProcesses currProc = clientProcessRepository.findClientProcessesByClient(client);
                controller.stopInstance(currProc.getProcessId());
                clientProcessRepository.deleteClientProcessesByClient(client);
            }
        }else{
            clientRepository.save(client);
        }


        clientProcessRepository.save(new ClientProcesses(process_id, client));
        System.out.println("new client process saved with id: " + process_id);

    }

    public void saveNewChatData(Update update) throws SQLException {
        Client client = clientRepository.findClientByChatId(String.valueOf(update.getMessage().getChatId()));
        String message = update.getMessage().getText();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);

        if(!checkForExistingData(String.valueOf(update.getMessage().getChatId()))){
            ClientChatVariables chatVariables = new ClientChatVariables(client, jsonObject.toString());
            clientChatVariablesRepository.save(chatVariables);
        }else{
            System.out.println("Chat data already exists");
        }

    }

    public boolean checkForExistingData(String chat_id){
        return clientChatVariablesRepository.existsClientChatVariablesByClient(clientRepository.findClientByChatId(chat_id));
    }


}