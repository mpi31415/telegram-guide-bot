package io.camunda.bot.camundaUtil.worker;

import io.camunda.bot.entities.client.ClientVariables;
import io.camunda.bot.entities.tours.Tours;
import io.camunda.bot.service.ToursService;
import io.camunda.bot.telegramBot.TelegramBot;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class ServiceWorker {
    private final static Logger LOG = LoggerFactory.getLogger(ServiceWorker.class);
    @Autowired
    private ZeebeClientLifecycle client;

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private ToursService toursService;

    @JobWorker(type = "display-aboutme")
    public void display_about_me(final ActivatedJob job) throws TelegramApiException {
        String chatId = job.getVariablesAsMap().get("chat_id").toString();

        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("My Name is Moroz and I am a tour guide");
        telegramBot.sendMessage(sm);
        client.newCompleteCommand(job.getKey());
        LOG.info("Displaying my info");
    }

    @JobWorker(type = "display-about-destination-choice")
    public void displayAboutMeDestinationChoice(final ActivatedJob activatedJob) throws TelegramApiException {
        String chatId = activatedJob.getVariablesAsMap().get("chat_id").toString();

        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("Do you want to know more /about me or more about our upcoming /tours?");
        telegramBot.sendMessage(sm);

    }

    @JobWorker(type = "display-destinations")
    public void destinationDisplay(final ActivatedJob job) throws TelegramApiException {

        List<Tours> destinations = toursService.getTop3UpcomingTours();
        client.newCompleteCommand(job.getKey());

        SendMessage sm = new SendMessage();
        String message = String.format("Popular destinations are \n 1. /%s \n 2. /%s \n 3. /%s \n", destinations.get(0).getTourName(), destinations.get(1).getTourName(), destinations.get(2).getTourName());
        sm.setText(message);
        sm.setChatId(job.getVariablesAsMap().get("chat_id").toString());
        telegramBot.sendMessage(sm);
        LOG.info("Displaying destinations");
    }

    @JobWorker(type="display-dest-options")
    public void displayDestinationOptions(final ActivatedJob job) throws TelegramApiException {
        client.newCompleteCommand(job.getKey());
        String selected_destination = job.getVariablesAsMap().get("destination").toString().split("/")[1];
        SendMessage sm = new SendMessage();
        String message = String.format("You have selected: %s . Do you want to see more /information, the current /participants, do you want to /signup or select a different /trip ?",selected_destination);
        sm.setText(message);
        sm.setChatId(job.getVariablesAsMap().get("chat_id").toString());
        telegramBot.sendMessage(sm);
        LOG.info("Displaying Options");
    }

    @JobWorker(type="trip-inf-disp")
    public void displayTripInformation(final ActivatedJob job) throws TelegramApiException {
        String selectedDestination = job.getVariablesAsMap().get("destination").toString().split("/")[1];
        Tours tour = toursService.getTourByName(selectedDestination);
        SendMessage sm = new SendMessage();
        sm.setText(tour.getTourDescription());
        sm.setChatId(job.getVariablesAsMap().get("chat_id").toString());
        telegramBot.sendMessage(sm);
        LOG.info("Displaying information for " + selectedDestination);
    }

    @JobWorker(type = "trip-participants-display")
    public void displayTripParticipants(final ActivatedJob job) throws TelegramApiException {
        String selectedDestination = job.getVariablesAsMap().get("destination").toString().split("/")[1];
        List <ClientVariables> clientVariables = toursService.findClientsByTour(selectedDestination);
        SendMessage sm = new SendMessage();
        StringBuilder message = new StringBuilder();
        message.append("Participating clients are: ");
        //TODO if zero provide different message
        for(ClientVariables clientVariables1 : clientVariables){
            message.append("\n").append(clientVariables1.getClientFirstname());
            System.out.println(clientVariables1.getClientFirstname());
        }
        sm.setText(message.toString());
        sm.setChatId(job.getVariablesAsMap().get("chat_id").toString());
        telegramBot.sendMessage(sm);
        client.newCompleteCommand(job.getKey()).send().join();
        LOG.info("Displaying participants for " + selectedDestination);


    }


    @JobWorker(type ="trip-signup")
    public void displayRegistrationPrompt(final  ActivatedJob job) throws TelegramApiException{
        System.out.println("I am here");
        SendMessage sm = new SendMessage();
        sm.setText("You have selected the Sign up option for the "+ job.getVariablesAsMap().get("destination").toString().split("/")[0]+" trip, in the following you will be prompted to provide your personal data - please respond in single messages and don't edit your messages, you will be later given a chance to correct your provided information.");
        sm.setChatId(job.getVariablesAsMap().get("chat_id").toString());
        telegramBot.sendMessage(sm);
        LOG.info("singup prompt");
    }

    @JobWorker(type = "ask-name")
    public void askName(final  ActivatedJob job) throws TelegramApiException{
        SendMessage sm = new SendMessage();
        sm.setText("Please provide your name in the romanized version (as it is written in your passport), e.g. Vasiliy Petrov");
        sm.setChatId(job.getVariablesAsMap().get("chat_id").toString());
        telegramBot.sendMessage(sm);
        LOG.info("Name prompt");
    }
}
