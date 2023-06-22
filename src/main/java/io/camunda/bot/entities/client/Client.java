package io.camunda.bot.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;


@Setter
@Getter
@Entity
@RequiredArgsConstructor
public class Client {
    @Id
    @GeneratedValue
    private Long clientId;
    @Column(unique = true)
    private String chatId;

    public Client(String chatId){
        this.chatId = chatId;
    }
}
