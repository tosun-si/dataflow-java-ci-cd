package fr.groupbees.infrastructure.io.mock;

import fr.groupbees.domain.*;
import fr.groupbees.domain_ptransform.TeamStatsInMemoryIOConnector;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;

import javax.inject.Inject;

public class TeamStatsMockIOAdapter implements TeamStatsInMemoryIOConnector {

    @Inject
    public TeamStatsMockIOAdapter() {
    }

    @Override
    public PTransform<PBegin, PCollection<TeamStatsRaw>> read() {
        return new TeamStatsMockReadTransform();
    }
}
