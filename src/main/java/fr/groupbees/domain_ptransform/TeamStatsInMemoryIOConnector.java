package fr.groupbees.domain_ptransform;

import fr.groupbees.domain.TeamStatsRaw;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;

import java.io.Serializable;

public interface TeamStatsInMemoryIOConnector extends Serializable {

    PTransform<PBegin, PCollection<TeamStatsRaw>> read();
}
