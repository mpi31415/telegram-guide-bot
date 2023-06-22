package io.camunda.bot.entities.tours;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;


@Setter
@Getter
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
public class Tours {
    @Id
    @GeneratedValue
    private Long tourId;

    private String tourName;
    private String tourDescription;
    private Timestamp tourStart;
    private Timestamp tourEnd;

    public Tours(String tourName, String tourDescription, Timestamp tourStart, Timestamp tourEnd) {
        this.tourName = tourName;
        this.tourDescription = tourDescription;
        this.tourStart = tourStart;
        this.tourEnd = tourEnd;
    }
}
