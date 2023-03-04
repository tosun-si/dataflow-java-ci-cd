package fr.groupbees.infrastructure.io.jsonfile;

import fr.groupbees.application.PipelineConf;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollectionView;

public class TeamSloganJsonFileReadTransform extends PTransform<PBegin, PCollectionView<String>> {

    private final PipelineConf pipelineConf;

    public TeamSloganJsonFileReadTransform(PipelineConf pipelineConf) {
        this.pipelineConf = pipelineConf;
    }

    @Override
    public PCollectionView<String> expand(PBegin input) {
        return input
                .apply("Read slogans", TextIO.read().from(pipelineConf.getInputFileSlogans()))
                .apply("Create as collection view", View.asSingleton());
    }
}
