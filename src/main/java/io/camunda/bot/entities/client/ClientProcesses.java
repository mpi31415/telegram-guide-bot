package io.camunda.bot.entities.client;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@RequiredArgsConstructor
public class ClientProcesses {
    @Id
    @GeneratedValue
    private Long id;

    private String processId;

    @OneToOne
    @JoinColumn(name = "clientId")
    private Client client;

    public ClientProcesses(String processId, Client client){
        this.processId = processId;
        this.client = client;
    }
}
