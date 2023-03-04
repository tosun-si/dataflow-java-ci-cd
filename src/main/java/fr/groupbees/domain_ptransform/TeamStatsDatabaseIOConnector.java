package fr.groupbees.domain_ptransform;

import fr.groupbees.domain.TeamStats;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.POutput;

import java.io.Serializable;

public interface TeamStatsDatabaseIOConnector extends Serializable {

    PTransform<PCollection<TeamStats>, ? extends POutput> write();
}
