package io.camunda.bot.repository;

import io.camunda.bot.entities.tours.Tours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToursRepository extends JpaRepository<Tours, Long> {
    List<Tours> findAllByOrderByTourStartAsc();
    Tours findToursByTourName(String tourName);
}
