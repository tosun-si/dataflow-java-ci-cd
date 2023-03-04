package fr.groupbees.domain;

import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class TeamBestPasserStats implements Serializable {

    private String firstName;
    private String lastName;
    private int goalAssists;
    private int games;
}
