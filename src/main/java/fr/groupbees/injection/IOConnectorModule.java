package fr.groupbees.injection;

import dagger.Binds;
import dagger.Module;
import fr.groupbees.domain_ptransform.FailureIOConnector;
import fr.groupbees.domain_ptransform.TeamStatsDatabaseIOConnector;
import fr.groupbees.domain_ptransform.TeamStatsFileIOConnector;
import fr.groupbees.domain_ptransform.TeamStatsInMemoryIOConnector;
import fr.groupbees.infrastructure.io.bigquery.FailureBigqueryIOAdapter;
import fr.groupbees.infrastructure.io.bigquery.TeamStatsBigQueryIOAdapter;
import fr.groupbees.infrastructure.io.cloudlogging.FailureCloudLoggingIOAdapter;
import fr.groupbees.infrastructure.io.mock.TeamStatsMockIOAdapter;
import fr.groupbees.infrastructure.io.jsonfile.TeamStatsJsonFileIOAdapter;

import javax.inject.Named;

@Module
public interface IOConnectorModule {

    @Binds
    TeamStatsDatabaseIOConnector provideTeamStatsDatabaseIOConnector(TeamStatsBigQueryIOAdapter bigQueryIOAdapter);

    @Binds
    TeamStatsFileIOConnector provideTeamStatsFileIOConnector(TeamStatsJsonFileIOAdapter fileIOAdapter);

    @Binds
    TeamStatsInMemoryIOConnector provideTeamStatsInMemoryIOConnector(TeamStatsMockIOAdapter inMemoryIOAdapter);

    @Binds
    @Named(FailureIOConnector.FAILURE_LOG_SINK_NAME)
    FailureIOConnector provideFailureLogIOConnector(FailureCloudLoggingIOAdapter failureLogAdapter);

    @Binds
    @Named(FailureIOConnector.FAILURE_DATABASE_SINK_NAME)
    FailureIOConnector provideFailureDatabaseIOConnector(FailureBigqueryIOAdapter failureBigqueryIOAdapter);
}
