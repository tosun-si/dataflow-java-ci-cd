package fr.groupbees.infrastructure.io.cloudlogging;

import fr.groupbees.asgarde.Failure;
import fr.groupbees.domain_ptransform.FailureIOConnector;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.POutput;

import javax.inject.Inject;

public class FailureCloudLoggingIOAdapter implements FailureIOConnector {

    @Inject
    public FailureCloudLoggingIOAdapter() {
    }

    @Override
    public PTransform<PCollection<Failure>, ? extends POutput> write() {
        return new FailureCloudLoggingWriteTransform();
    }
}
