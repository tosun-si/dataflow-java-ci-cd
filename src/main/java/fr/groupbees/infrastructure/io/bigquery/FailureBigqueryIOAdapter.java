package fr.groupbees.infrastructure.io.bigquery;

import com.google.api.services.bigquery.model.TableRow;
import fr.groupbees.asgarde.Failure;
import fr.groupbees.infrastructure.io.FailureConf;
import fr.groupbees.domain_ptransform.FailureIOConnector;
import lombok.val;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.POutput;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.Instant;

import javax.inject.Inject;

import static fr.groupbees.infrastructure.io.bigquery.FailureFields.*;

public class FailureBigqueryIOAdapter implements FailureIOConnector {

    private final FailureConf failureConf;

    @Inject
    public FailureBigqueryIOAdapter(FailureConf failureConf) {
        this.failureConf = failureConf;
    }

    private final static String COMPONENT_TYPE_VALUE = "DATAFLOW";

    public PTransform<PCollection<Failure>, ? extends POutput> write() {
        return BigQueryIO.<Failure>write()
                .withMethod(BigQueryIO.Write.Method.FILE_LOADS)
                .to(failureConf.getOutputDataset() + "." + failureConf.getOutputTable())
                .withFormatFunction(failure -> toFailureTableRow(failureConf, failure))
                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER)
                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND);
    }

    private static TableRow toFailureTableRow(FailureConf failureConf, Failure failure) {
        val creationDate = Instant.now().toString();

        return new TableRow()
                .set(FEATURE_NAME.getValue(), failureConf.getFeatureName())
                .set(PIPELINE_STEP.getValue(), failure.getPipelineStep())
                .set(JOB_NAME.getValue(), failureConf.getJobName())
                .set(INPUT_ELEMENT.getValue(), failure.getInputElement())
                .set(EXCEPTION_TYPE.getValue(), failure.getException().getClass().getSimpleName())
                .set(STACK_TRACE.getValue(), ExceptionUtils.getStackTrace(failure.getException()))
                .set(COMPONENT_TYPE.getValue(), COMPONENT_TYPE_VALUE)
                .set(DWH_CREATION_DATE.getValue(), creationDate);
    }
}
