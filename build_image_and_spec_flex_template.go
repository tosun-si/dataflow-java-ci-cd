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

	buildFlexTemplateImage := client.Container().
		From("gcr.io/kaniko-project/executor:v1.9.0-debug").
		WithEntrypoint([]string{}).
		WithDirectory(".", source).
		//WithWorkdir("/src").
		WithEnvVariable("PROJECT_ID", "gb-poc-373711").
		WithEnvVariable("LOCATION", "europe-west1").
		WithEnvVariable("REPO_NAME", "internal-images").
		WithEnvVariable("IMAGE_NAME", "dataflow/team-league-java-dagger").
		WithEnvVariable("IMAGE_TAG", "latest").
		WithEnvVariable("METADATA_TEMPLATE_FILE_PATH", "gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java.json").
		WithEnvVariable("SDK_LANGUAGE", "JAVA").
		WithEnvVariable("METADATA_FILE", "config/metadata.json").
		WithEnvVariable("GOOGLE_APPLICATION_CREDENTIALS", "./secrets/sa-dataflow.json").
		WithExec([]string{
			"/kaniko/executor",
			//"--use-new-run",
			"--compressed-caching=false",
			"--single-snapshot",
			"--context=dir://./",
			"--dockerfile",
			"Dockerfile",
			"--destination",
			"europe-west1-docker.pkg.dev/gb-poc-373711/internal-images/dataflow/team-league-java-dagger:latest",
		}).
		//WithExec([]string{
		//	"./scripts/build_image_kaniko_dagger.sh",
		//}).
		Directory(".")

	createFlexTemplateSpecFileGcs := client.Container().
		From("google/cloud-sdk:420.0.0-slim").
		WithDirectory(".", buildFlexTemplateImage).
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
			"build",
			"gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java-dagger.json",
			"--image",
			"europe-west1-docker.pkg.dev/gb-poc-373711/internal-images/dataflow/team-league-java-dagger:latest",
			"--sdk-language",
			"JAVA",
			"--metadata-file",
			"config/metadata.json",
		})

	out, err := createFlexTemplateSpecFileGcs.Stdout(ctx)

	if err != nil {
		panic(err)
	}

	fmt.Printf("Published image to: %s\n", out)
}
