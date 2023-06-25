package io.camunda.bot.entities.client;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    public ClientVariablesRelation(Client client, ClientVariables clientVariables) {
        this.client = client;
        this.clientVariables = clientVariables;
    }
}
