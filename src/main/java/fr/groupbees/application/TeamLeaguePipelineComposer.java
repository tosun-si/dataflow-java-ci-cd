package fr.groupbees.application;

import fr.groupbees.asgarde.Failure;
import fr.groupbees.domain.TeamStats;
import fr.groupbees.domain_ptransform.*;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.WithFailures.Result;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;

import javax.inject.Inject;
import javax.inject.Named;

import static fr.groupbees.domain_ptransform.FailureIOConnector.*;

public class TeamLeaguePipelineComposer {

    private final TeamStatsDatabaseIOConnector databaseIOConnector;
    private final TeamStatsFileIOConnector fileIOConnector;
    private final TeamStatsInMemoryIOConnector inMemoryIOConnector;
    private final FailureIOConnector failureLogIOConnector;
    private final FailureIOConnector failureDatabaseIOConnector;

    @Inject
    public TeamLeaguePipelineComposer(TeamStatsDatabaseIOConnector databaseIOConnector,
                                      TeamStatsFileIOConnector fileIOConnector,
                                      TeamStatsInMemoryIOConnector inMemoryIOConnector,
                                      @Named(FAILURE_LOG_SINK_NAME) FailureIOConnector failureLogIOConnector,
                                      @Named(FAILURE_DATABASE_SINK_NAME) FailureIOConnector failureDatabaseIOConnector) {
        this.databaseIOConnector = databaseIOConnector;
        this.fileIOConnector = fileIOConnector;
        this.inMemoryIOConnector = inMemoryIOConnector;
        this.failureLogIOConnector = failureLogIOConnector;
        this.failureDatabaseIOConnector = failureDatabaseIOConnector;
    }

    public Pipeline compose(final Pipeline pipeline) {
        PCollectionView<String> slogansSideInput = pipeline
                .apply("Read slogans", fileIOConnector.read_team_slogans());

        Result<PCollection<TeamStats>, Failure> resultTeamStats = pipeline
                .apply("Read team stats", inMemoryIOConnector.read())
                .apply("Team stats domain transform", new TeamStatsTransform(slogansSideInput));

        resultTeamStats
                .output()
                .apply("Write to database", databaseIOConnector.write());

        resultTeamStats
                .failures()
                .apply(LOG_FAILURES_STACKDRIVER, failureLogIOConnector.write());

        resultTeamStats
                .failures()
                .apply(WRITE_FAILURES_DATABASE, failureDatabaseIOConnector.write());

        return pipeline;
    }
}
