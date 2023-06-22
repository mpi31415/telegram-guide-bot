package io.camunda.bot.repository;

import io.camunda.bot.entities.client.Client;
import io.camunda.bot.entities.client.ClientProcesses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientProcessRepository extends JpaRepository<ClientProcesses, Long> {
    ClientProcesses  findClientProcessesByClient(Client client);
    void deleteClientProcessesByClient(Client client);

    boolean existsClientProcessesByClient(Client client);
}
