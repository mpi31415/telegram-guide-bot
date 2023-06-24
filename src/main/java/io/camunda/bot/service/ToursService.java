package io.camunda.bot.service;


import io.camunda.bot.entities.client.ClientVariables;
import io.camunda.bot.entities.tours.ClientTours;
import io.camunda.bot.entities.tours.Tours;
import io.camunda.bot.repository.ClientRepository;
import io.camunda.bot.repository.ClientToursRelationRepository;
import io.camunda.bot.repository.ClientVariablesRelationRepository;
import io.camunda.bot.repository.ToursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class ToursService {
    @Autowired
    private ToursRepository toursRepository;
    @Autowired
    private ClientToursRelationRepository clientToursRelationRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientVariablesRelationRepository clientVariablesRelationRepository;


    public List<Tours> getTop3UpcomingTours(){
        List<Tours> maps = toursRepository.findAllByOrderByTourStartAsc();
        return maps.subList(0,3);
    }

    public Tours getTourByName(String tourName){
        return toursRepository.findToursByTourName(tourName);
    }

    public List<ClientVariables> findClientsByTour(String tourName){
        Tours tour = toursRepository.findToursByTourName(tourName);
        System.out.println("tourname " + tourName + " " + tour.getTourName());
        List<ClientTours> clientTours = clientToursRelationRepository.findClientToursByTours(tour);
        System.out.println(clientTours.size());
        List<ClientVariables> clients = new ArrayList<>();
        for(ClientTours ct : clientTours){
            ClientVariables clientVariables = clientVariablesRelationRepository.findClientVariablesRelationByClient(ct.getClient()).getClientVariables();
            clients.add(clientVariables);
            System.out.println(clientVariables.getClientFirstname());
        }
        System.out.println("Client length: " +clients.size());
        return clients;
    }

}
