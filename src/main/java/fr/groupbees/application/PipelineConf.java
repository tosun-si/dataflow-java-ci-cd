package fr.groupbees.application;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class PipelineConf implements Serializable {

    private final String inputJsonFile;
    private final String inputFileSlogans;
    private final String teamLeagueDataset;
    private final String teamStatsTable;
}
