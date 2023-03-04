package fr.groupbees.infrastructure.io.bigquery;

import com.google.api.services.bigquery.model.TableRow;
import fr.groupbees.domain.TeamBestPasserStats;
import fr.groupbees.domain.TeamStats;
import fr.groupbees.domain.TeamTopScorerStats;
import fr.groupbees.domain_ptransform.TeamStatsDatabaseIOConnector;
import fr.groupbees.application.PipelineConf;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.joda.time.Instant;

import javax.inject.Inject;

public class TeamStatsBigQueryIOAdapter implements TeamStatsDatabaseIOConnector {

    private final PipelineConf pipelineConf;

    @Inject
    public TeamStatsBigQueryIOAdapter(PipelineConf pipelineConf) {
        this.pipelineConf = pipelineConf;
    }

    @Override
    public BigQueryIO.Write<TeamStats> write() {
        return BigQueryIO.<TeamStats>write()
                .withMethod(BigQueryIO.Write.Method.FILE_LOADS)
                .to(pipelineConf.getTeamLeagueDataset() + "." + pipelineConf.getTeamStatsTable())
                .withFormatFunction(TeamStatsBigQueryIOAdapter::toTeamStatsTableRow)
                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER)
                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND);
    }

    private static TableRow toTeamStatsTableRow(final TeamStats teamStats) {
        final TeamTopScorerStats topScorerStats = teamStats.getTopScorerStats();
        final TeamBestPasserStats bestPasserStats = teamStats.getBestPasserStats();

        final TableRow topScorerStatsRow = new TableRow()
                .set("firstName", topScorerStats.getFirstName())
                .set("lastName", topScorerStats.getLastName())
                .set("goals", topScorerStats.getGoals())
                .set("games", topScorerStats.getGames());

        final TableRow bestPasserStatsRow = new TableRow()
                .set("firstName", bestPasserStats.getFirstName())
                .set("lastName", bestPasserStats.getLastName())
                .set("goalAssists", bestPasserStats.getGoalAssists())
                .set("games", bestPasserStats.getGames());

        return new TableRow()
                .set("teamName", teamStats.getTeamName())
                .set("teamScore", teamStats.getTeamScore())
                .set("teamTotalGoals", teamStats.getTeamTotalGoals())
                .set("teamSlogan", teamStats.getTeamSlogan())
                .set("topScorerStats", topScorerStatsRow)
                .set("bestPasserStats", bestPasserStatsRow)
                .set("ingestionDate", new Instant().toString());
    }
}
