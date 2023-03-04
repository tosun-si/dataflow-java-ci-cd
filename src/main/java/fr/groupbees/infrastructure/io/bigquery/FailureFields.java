package fr.groupbees.infrastructure.io.bigquery;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FailureFields {

    FEATURE_NAME("featureName"),
    PIPELINE_STEP("pipelineStep"),
    JOB_NAME("jobName"),
    INPUT_ELEMENT("inputElement"),
    EXCEPTION_TYPE("exceptionType"),
    STACK_TRACE("stackTrace"),
    COMPONENT_TYPE("componentType"),
    DWH_CREATION_DATE("dwhCreationDate");

    private final String value;
}
