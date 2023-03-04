package fr.groupbees.application;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface TeamLeagueOptions extends PipelineOptions {
    @Description("Path of the input Json file to read from")
    String getInputJsonFile();

    void setInputJsonFile(String value);

    @Description("Path of the slogans file to read from")
    String getInputFileSlogans();

    void setInputFileSlogans(String value);

    @Description("Path of the file to write to")
    String getTeamLeagueDataset();

    void setTeamLeagueDataset(String value);

    @Description("Team stats table")
    String getTeamStatsTable();

    void setTeamStatsTable(String value);

    @Description("Job type")
    String getJobType();

    void setJobType(String value);

    @Description("Failure output dataset")
    String getFailureOutputDataset();

    void setFailureOutputDataset(String value);

    @Description("Failure output table")
    String getFailureOutputTable();

    void setFailureOutputTable(String value);

    @Description("Feature name for failures")
    String getFailureFeatureName();

    void setFailureFeatureName(String value);
}
