package fr.groupbees.infrastructure.io;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class FailureConf implements Serializable {

    private String featureName;
    private String jobName;
    private String outputDataset;
    private String outputTable;
}
