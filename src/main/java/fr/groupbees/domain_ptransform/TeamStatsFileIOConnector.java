package fr.groupbees.domain_ptransform;

import fr.groupbees.domain.TeamStatsRaw;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;

import java.io.Serializable;

public interface TeamStatsFileIOConnector extends Serializable {

    PTransform<PBegin, PCollection<TeamStatsRaw>> read_team_stats();

    PTransform<PBegin, PCollectionView<String>> read_team_slogans();
}
