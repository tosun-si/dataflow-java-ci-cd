package fr.groupbees.infrastructure.io.cloudlogging;

import fr.groupbees.asgarde.Failure;
import lombok.val;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.beam.sdk.values.TypeDescriptors.strings;

public class FailureCloudLoggingWriteTransform extends PTransform<PCollection<Failure>, PDone> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailureCloudLoggingWriteTransform.class);

    private static final String STEP_MAP_FAILURE_JSON_STRING = "Map Failure to Json String";
    private static final String STEP_LOG_FAILURE = "Logs Failure to stackDriver";

    public PDone expand(PCollection<Failure> input) {
        input.apply(STEP_MAP_FAILURE_JSON_STRING, new LogTransform());
        return PDone.in(input.getPipeline());
    }

    static class LogTransform extends PTransform<PCollection<Failure>, PCollection<String>> {
        public PCollection<String> expand(PCollection<Failure> input) {
            return input
                    .apply(STEP_MAP_FAILURE_JSON_STRING, MapElements
                            .into(strings())
                            .via(FailureCloudLoggingWriteTransform::toFailureLogInfo))
                    .apply(STEP_LOG_FAILURE, MapElements.into(strings()).via(this::logFailure));
        }

        /**
         * Logs the given failure string object.
         */
        private String logFailure(String failureAsString) {
            LOGGER.error(failureAsString);
            return failureAsString;
        }
    }

    private static String toFailureLogInfo(Failure failure) {
        val inputElementInfo = "InputElement : " + failure.getInputElement();
        val stackTraceInfo = "StackTrace : " + ExceptionUtils.getStackTrace(failure.getException());

        return inputElementInfo + "\n" + stackTraceInfo;
    }
}
