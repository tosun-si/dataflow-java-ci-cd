package fr.groupbees.domain_ptransform;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.groupbees.asgarde.CollectionComposer;
import fr.groupbees.asgarde.Failure;
import fr.groupbees.asgarde.transforms.MapElementFn;
import fr.groupbees.asgarde.transforms.MapProcessContextFn;
import fr.groupbees.domain.TeamStats;
import fr.groupbees.domain.TeamStatsRaw;
import lombok.val;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.WithFailures.Result;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

import static org.apache.beam.sdk.values.TypeDescriptor.of;

public class TeamStatsTransform extends PTransform<PCollection<TeamStatsRaw>, Result<PCollection<TeamStats>, Failure>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamStatsTransform.class);

    private final PCollectionView<String> slogansSideInput;

    public TeamStatsTransform(PCollectionView<String> slogansSideInput) {
        this.slogansSideInput = slogansSideInput;
    }

    @Override
    public Result<PCollection<TeamStats>, Failure> expand(PCollection<TeamStatsRaw> input) {
        return CollectionComposer.of(input)
                .apply("Validate fields", MapElements.into(of(TeamStatsRaw.class)).via(TeamStatsRaw::validateFields))
                .apply("Compute team stats", MapElementFn
                        .into(of(TeamStats.class))
                        .via(TeamStats::computeTeamStats)
                        .withStartBundleAction(() -> LOGGER.info("####################Start bundle compute stats")))
                .apply("Add team slogan", MapProcessContextFn
                                .from(TeamStats.class)
                                .into(of(TeamStats.class))
                                .via(c -> addSloganToStats(c, slogansSideInput))
                                .withSetupAction(() -> LOGGER.info("####################Start add slogan")),
                        Collections.singletonList(slogansSideInput))
                .getResult();
    }

    public TeamStats addSloganToStats(final DoFn<TeamStats, TeamStats>.ProcessContext context,
                                      final PCollectionView<String> slogansSideInput) {
        final String slogans = context.sideInput(slogansSideInput);

        val ref = new TypeReference<Map<String, String>>() {
        };

        final Map<String, String> slogansAsMap = JsonUtil.deserializeToMap(slogans, ref);

        final TeamStats teamStats = context.element();
        return teamStats.addSloganToStats(slogansAsMap);
    }
}
