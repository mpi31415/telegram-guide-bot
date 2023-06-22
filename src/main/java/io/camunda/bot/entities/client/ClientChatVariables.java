package io.camunda.bot.entities.client;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


import java.util.Map;

@Setter
@Getter
@Entity
@RequiredArgsConstructor
public class ClientChatVariables {

    @Id
    @GeneratedValue
    private Long id;


    @Column(columnDefinition = "text")
    private String variables;

    @OneToOne
    @JoinColumn(name= "clientId")
    private Client client;


    public ClientChatVariables(Client client, String variables){
        this.client = client;
        this.variables = variables;
    }


}
