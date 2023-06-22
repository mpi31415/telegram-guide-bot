package io.camunda.bot.entities.client;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class ClientVariablesRelation {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "clientId")
    private Client client;

    @OneToOne
    @JoinColumn(name = "entryId")
    private ClientVariables clientVariables;
}
