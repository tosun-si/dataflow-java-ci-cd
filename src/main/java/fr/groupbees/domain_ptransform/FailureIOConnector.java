package fr.groupbees.domain_ptransform;

import fr.groupbees.asgarde.Failure;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.POutput;

import java.io.Serializable;

public interface FailureIOConnector extends Serializable {

    static final String FAILURE_LOG_SINK_NAME = "failure_log_sink";
    static final String FAILURE_DATABASE_SINK_NAME = "failure_database_sink";
    static final String LOG_FAILURES_STACKDRIVER = "Log failures to Cloud Logging";
    static final String WRITE_FAILURES_DATABASE = "Write failures to database";

    PTransform<PCollection<Failure>, ? extends POutput> write();
}
