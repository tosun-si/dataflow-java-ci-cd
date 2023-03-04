package fr.groupbees.infrastructure.io.jsonfile;

import fr.groupbees.domain.TeamStatsRaw;
import fr.groupbees.domain_ptransform.JsonUtil;
import fr.groupbees.application.PipelineConf;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;

import static org.apache.beam.sdk.values.TypeDescriptor.of;

public class TeamStatsJsonFileReadTransform extends PTransform<PBegin, PCollection<TeamStatsRaw>> {

    private final PipelineConf pipelineConf;

    public TeamStatsJsonFileReadTransform(PipelineConf pipelineConf) {
        this.pipelineConf = pipelineConf;
    }

    @Override
    public PCollection<TeamStatsRaw> expand(PBegin input) {
        return input
                .apply("Read Json file", TextIO.read().from(pipelineConf.getInputJsonFile()))
                .apply("Deserialize", MapElements.into(of(TeamStatsRaw.class)).via(this::deserializeToTeamStats));
    }

    private TeamStatsRaw deserializeToTeamStats(final String teamStatsAsString) {
        return JsonUtil.deserialize(teamStatsAsString, TeamStatsRaw.class);
    }
}
