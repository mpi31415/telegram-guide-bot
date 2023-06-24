package io.camunda.bot.service;

import io.camunda.bot.entities.tours.Tours;
import io.camunda.bot.repository.ToursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service

public class ToursService {
    @Autowired
    private ToursRepository toursRepository;


    public List<Tours> getTop3UpcomingTours(){
        List<Tours> maps = toursRepository.findAllByOrderByTourStartAsc();
        return maps.subList(0,3);
    }

    public Tours getTourByName(String tourName){
        return toursRepository.findToursByTourName(tourName);
    }

}
