package io.camunda.bot.repository;

import io.camunda.bot.entities.tours.ClientTours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientToursRelationRepository extends JpaRepository<ClientTours, Long> {
}
