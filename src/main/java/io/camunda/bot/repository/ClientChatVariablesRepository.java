package io.camunda.bot.repository;

import io.camunda.bot.entities.client.Client;
import io.camunda.bot.entities.client.ClientChatVariables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientChatVariablesRepository extends JpaRepository<ClientChatVariables, Long> {
    boolean existsClientChatVariablesByClient(Client client);
    ClientChatVariables findClientChatVariablesByClient(Client client);
}
