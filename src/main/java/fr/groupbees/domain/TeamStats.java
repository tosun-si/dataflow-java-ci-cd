package fr.groupbees.domain;

import lombok.*;

import java.io.Serializable;
import java.util.*;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class TeamStats implements Serializable {

    private String teamName;
    private int teamScore;
    private int teamTotalGoals;
    private String teamSlogan;
    private TeamTopScorerStats topScorerStats;
    private TeamBestPasserStats bestPasserStats;

    public static TeamStats computeTeamStats(final TeamStatsRaw teamStatsRaw) {
        final List<TeamScorerRaw> teamScorers = teamStatsRaw.getScorers();

        final TeamScorerRaw topScorer = teamScorers.stream()
                .max(Comparator.comparing(TeamScorerRaw::getGoals))
                .orElseThrow(NoSuchElementException::new);

        final TeamScorerRaw bestPasser = teamScorers.stream()
                .max(Comparator.comparing(TeamScorerRaw::getGoalAssists))
                .orElseThrow(NoSuchElementException::new);

        final int teamTotalGoals = teamScorers.stream()
                .mapToInt(TeamScorerRaw::getGoals)
                .sum();

        val topScorerStats = TeamTopScorerStats.builder()
                .firstName(topScorer.getScorerFirstName())
                .lastName(topScorer.getScorerLastName())
                .goals(topScorer.getGoals())
                .games(topScorer.getGames())
                .build();

        val bestPasserStats = TeamBestPasserStats.builder()
                .firstName(bestPasser.getScorerFirstName())
                .lastName(bestPasser.getScorerLastName())
                .goalAssists(bestPasser.getGoalAssists())
                .games(bestPasser.getGames())
                .build();

        return TeamStats.builder()
                .teamName(teamStatsRaw.getTeamName())
                .teamScore(teamStatsRaw.getTeamScore())
                .teamTotalGoals(teamTotalGoals)
                .topScorerStats(topScorerStats)
                .bestPasserStats(bestPasserStats)
                .build();
    }

    public TeamStats addSloganToStats(final Map<String, String> allSlogans) {
        final String slogan = Optional
                .ofNullable(allSlogans.get(teamName))
                .orElseThrow(() -> new IllegalArgumentException("No slogan for team : " + teamName));

        return this
                .toBuilder()
                .teamSlogan(slogan)
                .build();
    }
}
