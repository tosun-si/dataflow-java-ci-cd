package main

import (
	"context"
	"fmt"
	"os"

	"dagger.io/dagger"
)

func main() {
	ctx := context.Background()
	client, err := dagger.Connect(ctx, dagger.WithLogOutput(os.Stdout))

	if err != nil {
		panic(err)
	}
	defer client.Close()

	hostSourceDir := client.Host().Directory(".", dagger.HostDirectoryOpts{})

	activateServiceAccount := []string{
		"gcloud",
		"auth",
		"activate-service-account",
		"sa-dataflow-dev@gb-poc-373711.iam.gserviceaccount.com",
		"--key-file=./secrets/sa-dataflow.json",
		"--project=gb-poc-373711",
	}

	source := client.Container().
		From("google/cloud-sdk:420.0.0-slim").
		WithMountedDirectory("/src", hostSourceDir).
		WithWorkdir("/src").
		Directory(".")

	runFlexTemplate := client.Container().
		From("google/cloud-sdk:420.0.0-slim").
		WithDirectory(".", source).
		WithEnvVariable("PROJECT_ID", "gb-poc-373711").
		WithEnvVariable("LOCATION", "europe-west1").
		WithEnvVariable("REPO_NAME", "internal-images").
		WithEnvVariable("IMAGE_NAME", "dataflow/team-league-java-dagger").
		WithEnvVariable("IMAGE_TAG", "latest").
		WithEnvVariable("METADATA_TEMPLATE_FILE_PATH", "gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java-dagger.json").
		WithEnvVariable("SDK_LANGUAGE", "JAVA").
		WithEnvVariable("METADATA_FILE", "config/metadata.json").
		WithEnvVariable("GOOGLE_APPLICATION_CREDENTIALS", "./secrets/sa-dataflow.json").
		WithExec(activateServiceAccount).
		WithExec([]string{
			"gcloud",
			"dataflow",
			"flex-template",
			"run",
			"team-league-java-dagger",
			"--template-file-gcs-location",
			"gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java-dagger.json",
			"--project=gb-poc-373711",
			"--region=europe-west1",
			"--temp-location=gs://mazlum_dev/dataflow/temp",
			"--staging-location=gs://mazlum_dev/dataflow/staging",
			"--parameters",
			"serviceAccount=sa-dataflow-dev@gb-poc-373711.iam.gserviceaccount.com",
			"--parameters",
			"inputJsonFile=gs://mazlum_dev/team_league/input/json/input_teams_stats_raw.json",
			"--parameters",
			"inputFileSlogans=gs://mazlum_dev/team_league/input/json/input_team_slogans.json",
			"--parameters",
			"teamLeagueDataset=mazlum_test",
			"--parameters",
			"teamStatsTable=team_stat",
			"--parameters",
			"jobType=team_league_java_ingestion_job",
			"--parameters",
			"failureOutputDataset=mazlum_test",
			"--parameters",
			"failureOutputTable=job_failure",
			"--parameters",
			"failureFeatureName=team_league",
		})

	out, err := runFlexTemplate.Stdout(ctx)

	if err != nil {
		panic(err)
	}

	fmt.Printf("Published image to: %s\n", out)
}
