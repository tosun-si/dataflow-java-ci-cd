package fr.groupbees.injection;

import dagger.Module;

@Module(includes = {IOConnectorModule.class, ConfigModule.class})
public class TeamLeagueModule {
}
