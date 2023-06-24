package io.camunda.bot.repository;


import io.camunda.bot.entities.tours.ClientTours;
import io.camunda.bot.entities.tours.Tours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientToursRelationRepository extends JpaRepository<ClientTours, Long> {
    List<ClientTours> findClientToursByTours(Tours tour);
}
