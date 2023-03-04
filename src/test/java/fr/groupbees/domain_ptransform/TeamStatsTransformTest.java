package fr.groupbees.domain_ptransform;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.groupbees.asgarde.Failure;
import fr.groupbees.domain.TeamStats;
import fr.groupbees.domain.TeamStatsRaw;
import fr.groupbees.domain.exception.TeamStatsRawValidatorException;
import lombok.val;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.WithFailures.Result;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.Serializable;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.beam.sdk.values.TypeDescriptors.strings;

@RunWith(JUnit4.class)
public class TeamStatsTransformTest implements Serializable {

    private static final int PSG_SCORE = 30;
    private static final String SLOGANS = "{\"PSG\": \"Paris est magique\",\"Real\": \"Hala Madrid\"}";

    @Rule
    public transient TestPipeline p = TestPipeline.create();

    @Test
    @Category(ValidatesRunner.class)
    public void givenInputTeamsStatsRawWithoutErrorWhenTransformToStatsDomainThenExpectedOutputInResult() {
        // Given.
        val referenceTeamStatRaw = new TypeReference<List<TeamStatsRaw>>() {
        };
        final List<TeamStatsRaw> inputTeamsStatsRaw = JsonUtil.deserializeFromResourcePath(
                "files/input/domain/ptransform/input_teams_stats_raw_without_error.json",
                referenceTeamStatRaw
        );

        PCollection<TeamStatsRaw> input = p.apply("Read team stats Raw", Create.of(inputTeamsStatsRaw));

        // When.
        Result<PCollection<TeamStats>, Failure> resultTransform = input
                .apply("Transform to team stats", new TeamStatsTransform(getSlogansSideInput(p)));

        PCollection<String> output = resultTransform
                .output()
                .apply("Map to Json String", MapElements.into(strings()).via(JsonUtil::serialize))
                .apply("Log Output team stats", MapElements.into(strings()).via(this::logStringElement));

        val referenceTeamStat = new TypeReference<List<TeamStats>>() {
        };
        final List<String> expectedTeamsStats = JsonUtil.deserializeFromResourcePath(
                        "files/expected/domain/ptransform/expected_teams_stats_without_error.json",
                        referenceTeamStat)
                .stream()
                .map(JsonUtil::serialize)
                .collect(toList());

        // Then.
        PAssert.that(output).containsInAnyOrder(expectedTeamsStats);
        PAssert.that(resultTransform.failures()).empty();

        p.run().waitUntilFinish();
    }

    @Test
    @Category(ValidatesRunner.class)
    public void givenInputTeamsStatsRawWithOneErrorAndOneGoodInputWhenTransformToStatsDomainThenOneExpectedFailureAndOneGoodOutput() {
        // Given.
        val referenceTeamStatRaw = new TypeReference<List<TeamStatsRaw>>() {
        };
        final List<TeamStatsRaw> inputTeamsStatsRaw = JsonUtil.deserializeFromResourcePath(
                "files/input/domain/ptransform/input_teams_stats_raw_with_one_error_one_good_output.json",
                referenceTeamStatRaw
        );

        PCollection<TeamStatsRaw> input = p.apply("Read team stats Raw", Create.of(inputTeamsStatsRaw));

        // When.
        Result<PCollection<TeamStats>, Failure> resultTransform = input
                .apply("Transform to team stats", new TeamStatsTransform(getSlogansSideInput(p)));

        PCollection<String> output = resultTransform
                .output()
                .apply("Map to Json String", MapElements.into(strings()).via(JsonUtil::serialize))
                .apply("Log Output team stats", MapElements.into(strings()).via(this::logStringElement));

        val referenceTeamStat = new TypeReference<List<TeamStats>>() {
        };
        final List<String> expectedTeamsStats = JsonUtil.deserializeFromResourcePath(
                        "files/expected/domain/ptransform/expected_teams_stats_with_one_error_one_good_output.json",
                        referenceTeamStat)
                .stream()
                .map(JsonUtil::serialize)
                .collect(toList());

        final TeamStatsRaw teamStatsRawWithErrorField = inputTeamsStatsRaw
                .stream()
                .filter(t -> t.getTeamScore() == PSG_SCORE)
                .findFirst()
                .get();

        final String expectedFailure = Failure.from(
                "Validate fields",
                teamStatsRawWithErrorField,
                new TeamStatsRawValidatorException(TeamStatsRaw.TEAM_EMPTY_ERROR_MESSAGE)
        ).toString();

        PCollection<String> resultFailuresAsString = resultTransform.failures()
                .apply("Failure as string", MapElements.into(strings()).via(Failure::toString))
                .apply("Log Failure", MapElements.into(strings()).via(this::logStringElement));

        // Then.
        PAssert.that(output).containsInAnyOrder(expectedTeamsStats);
        PAssert.that(resultFailuresAsString).containsInAnyOrder(singletonList(expectedFailure));

        p.run().waitUntilFinish();
    }

    private PCollectionView<String> getSlogansSideInput(final Pipeline pipeline) {
        return pipeline
                .apply("String side input", Create.of(SLOGANS))
                .apply("Create as collection view", View.asSingleton());
    }

    private String logStringElement(final String element) {
        System.out.println(element);
        return element;
    }
}
