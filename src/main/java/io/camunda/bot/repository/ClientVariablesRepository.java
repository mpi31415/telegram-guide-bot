package io.camunda.bot.repository;

import io.camunda.bot.entities.client.ClientVariables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientVariablesRepository extends JpaRepository<ClientVariables, Long> {
}
