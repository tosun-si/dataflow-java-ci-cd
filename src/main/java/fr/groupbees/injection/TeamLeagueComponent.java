package fr.groupbees.injection;

import dagger.BindsInstance;
import dagger.Component;
import fr.groupbees.application.TeamLeagueOptions;
import fr.groupbees.application.TeamLeaguePipelineComposer;

import javax.inject.Singleton;

@Singleton
@Component(modules = {TeamLeagueModule.class})
public interface TeamLeagueComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder withPipelineOptions(TeamLeagueOptions options);

        TeamLeagueComponent build();
    }

    TeamLeaguePipelineComposer composer();
}
