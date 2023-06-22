package io.camunda.bot.entities.tours;

import io.camunda.bot.entities.client.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@RequiredArgsConstructor
public class ClientTours {
    @Id
    @GeneratedValue
    private Long entryId;


    @OneToOne
    @JoinColumn(name = "tourId")
    private Tours tours;

    @OneToOne
    @JoinColumn(name = "clientId")
    private Client client;




}
