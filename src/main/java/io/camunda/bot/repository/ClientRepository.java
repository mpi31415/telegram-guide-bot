package io.camunda.bot.repository;

import io.camunda.bot.entities.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findClientByChatId(String chatId);
    boolean existsByChatId(String chatId);
}
