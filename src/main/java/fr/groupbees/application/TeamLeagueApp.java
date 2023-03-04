package fr.groupbees.application;

import fr.groupbees.injection.DaggerTeamLeagueComponent;
import fr.groupbees.injection.TeamLeagueComponent;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamLeagueApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamLeagueApp.class);

    public static void main(String[] args) {
        final TeamLeagueOptions options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(TeamLeagueOptions.class);

        Pipeline pipeline = Pipeline.create(options);

        final TeamLeagueComponent teamLeagueJob = DaggerTeamLeagueComponent.builder()
                .withPipelineOptions(options)
                .build();

        teamLeagueJob.composer().compose(pipeline);

        pipeline.run();

        LOGGER.info("End of CDP integration case JOB");
    }
}
