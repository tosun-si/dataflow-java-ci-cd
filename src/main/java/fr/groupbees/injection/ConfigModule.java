package fr.groupbees.injection;

import dagger.Module;
import dagger.Provides;
import fr.groupbees.application.TeamLeagueOptions;
import fr.groupbees.infrastructure.io.FailureConf;
import fr.groupbees.application.PipelineConf;

@Module
class ConfigModule {

    @Provides
    static PipelineConf providePipelineConf(TeamLeagueOptions options) {
        return PipelineConf
                .builder()
                .inputJsonFile(options.getInputJsonFile())
                .inputFileSlogans(options.getInputFileSlogans())
                .teamLeagueDataset(options.getTeamLeagueDataset())
                .teamStatsTable(options.getTeamStatsTable())
                .build();
    }

    @Provides
    static FailureConf provideFailureConf(TeamLeagueOptions options) {
        return FailureConf
                .builder()
                .featureName(options.getFailureFeatureName())
                .jobName(options.getJobType())
                .outputDataset(options.getFailureOutputDataset())
                .outputTable(options.getFailureOutputTable())
                .build();
    }
}
