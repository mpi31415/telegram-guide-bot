package io.camunda.bot.repository;

import io.camunda.bot.entities.client.Client;
import io.camunda.bot.entities.client.ClientVariablesRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientVariablesRelationRepository extends JpaRepository<ClientVariablesRelation, Long> {
    ClientVariablesRelation findClientVariablesRelationByClient(Client client);


}
