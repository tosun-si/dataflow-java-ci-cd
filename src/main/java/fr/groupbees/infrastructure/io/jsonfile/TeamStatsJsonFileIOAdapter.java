package fr.groupbees.infrastructure.io.jsonfile;

import fr.groupbees.domain.TeamStatsRaw;
import fr.groupbees.domain_ptransform.TeamStatsFileIOConnector;
import fr.groupbees.application.PipelineConf;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;

import javax.inject.Inject;

public class TeamStatsJsonFileIOAdapter implements TeamStatsFileIOConnector {

    private final PipelineConf pipelineConf;

    @Inject
    public TeamStatsJsonFileIOAdapter(PipelineConf pipelineConf) {
        this.pipelineConf = pipelineConf;
    }

    @Override
    public PTransform<PBegin, PCollection<TeamStatsRaw>> read_team_stats() {
        return new TeamStatsJsonFileReadTransform(pipelineConf);
    }

    @Override
    public PTransform<PBegin, PCollectionView<String>> read_team_slogans() {
        return new TeamSloganJsonFileReadTransform(pipelineConf);
    }
}
