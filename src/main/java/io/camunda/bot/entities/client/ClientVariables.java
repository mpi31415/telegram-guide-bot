package io.camunda.bot.entities.client;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Date;

@Setter
@Getter
@Entity
@RequiredArgsConstructor
public class ClientVariables {
    @Id
    @GeneratedValue
    private Long entryId;


    private String clientFirstname;
    private String clientLastname;
    private Date clientBirthday;
    private String clientPassportid;
    private String clientAddress;
    private String clientPhoneNumber;
    private String clientNationality;


    public ClientVariables(String clientFirstname, String clientLastname){
        this.clientFirstname = clientFirstname;
        this.clientLastname = clientLastname;
    }


    @Override
    public String toString() {
        return "ClientVariables{" +
                ", clientFirstname='" + clientFirstname + '\'' +
                ", clientLastname='" + clientLastname + '\'' +
                ", clientBirthday=" + clientBirthday +
                ", clientPassportid='" + clientPassportid + '\'' +
                ", clientAddress='" + clientAddress + '\'' +
                ", clientPhoneNumber='" + clientPhoneNumber + '\'' +
                ", clientNationality='" + clientNationality + '\'';
    }
}
