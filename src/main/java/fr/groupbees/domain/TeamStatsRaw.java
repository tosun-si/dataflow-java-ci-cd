package fr.groupbees.domain;

import fr.groupbees.domain.exception.TeamStatsRawValidatorException;
import lombok.*;

import java.io.Serializable;
import java.util.List;

import static java.util.Objects.isNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class TeamStatsRaw implements Serializable {

    public static final String TEAM_EMPTY_ERROR_MESSAGE = "Team name cannot be null or empty";

    private String teamName;
    private int teamScore;
    private List<TeamScorerRaw> scorers;

    public TeamStatsRaw validateFields() {
        if (isNull(teamName) || teamName.equals("")) {
            throw new TeamStatsRawValidatorException(TEAM_EMPTY_ERROR_MESSAGE);
        }

        return this;
    }
}
