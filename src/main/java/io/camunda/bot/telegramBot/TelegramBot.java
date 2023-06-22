package io.camunda.bot.telegramBot;

import io.camunda.bot.entities.tours.Tours;
import io.camunda.bot.repository.ToursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class TelegramBot  extends TelegramLongPollingBot {

    private final ConnectorService connectorService;

    //REMOVE IN PROD
    @Autowired
    private ToursRepository toursRepository;

    @Autowired
    private Environment env;




    @Autowired
    public TelegramBot(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void onStart() throws TelegramApiException, ParseException {
        TelegramBotsApi tb = new TelegramBotsApi(DefaultBotSession.class);
        tb.registerBot(this); // Register the current instance of TelegramBot
        System.out.println("The Bot successfully started");
        String date = "2023/07/02 18:30:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date startDate = simpleDateFormat.parse(date);
        Date endDate = new Date(startDate.getTime()+ 1000L *60*60*24);

        toursRepository.save(new Tours("Rimini","fun trip!", new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())));

        startDate.setTime(startDate.getTime() +1000L+60*60*24);
        endDate.setTime(endDate.getTime()+1000L+60*60*24);
        toursRepository.save(new Tours("Paris","another fun trip!", new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())));


        startDate.setTime(startDate.getTime() +1000L+60*60*24);
        endDate.setTime(endDate.getTime()+ 1000L+60*60*24);
        toursRepository.save(new Tours("Rome","a really fun trip!", new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())));



    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sm = new SendMessage();
        sm.setChatId(update.getMessage().getChatId());
        if(update.getMessage().getText().equals("/start")){
            sm.setText("Hello!");
            try {
                sendMessage(sm);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            connectorService.createNewProcess(update.getMessage().getChatId());

        }else{
            sm.setText(update.getMessage().getText());
            try {
                connectorService.saveNewChatData(update);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(SendMessage sm) throws TelegramApiException {

        execute(sm);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return "Helper Bot";
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public String getBotToken() {
        return env.getProperty("telegram.bot.token");
    }
}