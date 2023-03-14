# dataflow-java-ci-cd

The Medium article for this use case :

https://medium.com/@mazlum.tosun/ci-cd-for-dataflow-java-with-flex-templates-and-cloud-build-e3c584b8e564

## Run job with Dataflow runner from local machine :

```bash
mvn compile exec:java \
  -Dexec.mainClass=fr.groupbees.application.TeamLeagueApp \
  -Dexec.args=" \
  --project=gb-poc-373711 \
  --runner=DataflowRunner \
  --jobName=team-league-java-job-$(date +'%Y-%m-%d-%H-%M-%S') \
  --region=europe-west1 \
  --streaming=false \
  --zone=europe-west1-d \
  --tempLocation=gs://mazlum_dev/dataflow/temp \
  --gcpTempLocation=gs://mazlum_dev/dataflow/temp \
  --stagingLocation=gs://mazlum_dev/dataflow/staging \
  --serviceAccount=sa-dataflow-dev@gb-poc-373711.iam.gserviceaccount.com \
  --inputJsonFile=gs://mazlum_dev/team_league/input/json/input_teams_stats_raw.json \
  --inputFileSlogans=gs://mazlum_dev/team_league/input/json/input_team_slogans.json \
  --teamLeagueDataset=mazlum_test \
  --teamStatsTable=team_stat \
  --jobType=team_league_java_ingestion_job \
  --failureOutputDataset=mazlum_test \
  --failureOutputTable=job_failure \
  --failureFeatureName=team_league \
  " \
  -Pdataflow-runner
```

## Build image with Cloud Build :

```bash
gcloud builds submit --tag europe-west1-docker.pkg.dev/gb-poc-373711/internal-images/dataflow/team-league-java:latest .
```

## Build the Java project into an Uber JAR file.

````shell
mvn clean package
````

## Create Flex Template spec file :

```bash
gcloud dataflow flex-template build gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java.json \
  --image-gcr-path "europe-west1-docker.pkg.dev/gb-poc-373711/internal-images/dataflow/team-league-java:latest" \
  --sdk-language "JAVA" \
  --flex-template-base-image JAVA11 \
  --metadata-file "config/metadata.json" \
  --jar "target/teams-league-0.1.0.jar" \
  --env FLEX_TEMPLATE_JAVA_MAIN_CLASS="fr.groupbees.application.TeamLeagueApp"
```

# Run a Flex Template pipeline :

```bash
gcloud dataflow flex-template run "team-league-java-`date +%Y%m%d-%H%M%S`" \
    --template-file-gcs-location "gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java.json" \
    --project=gb-poc-373711 \
    --region=europe-west1 \
    --temp-location=gs://mazlum_dev/dataflow/temp \
    --staging-location=gs://mazlum_dev/dataflow/staging \
    --parameters serviceAccount=sa-dataflow-dev@gb-poc-373711.iam.gserviceaccount.com \
    --parameters inputJsonFile=gs://mazlum_dev/team_league/input/json/input_teams_stats_raw.json \
    --parameters inputFileSlogans=gs://mazlum_dev/team_league/input/json/input_team_slogans.json \
    --parameters teamLeagueDataset=mazlum_test \
    --parameters teamStatsTable=team_stat \
    --parameters jobType=team_league_java_ingestion_job \
    --parameters failureOutputDataset=mazlum_test \
    --parameters failureOutputTable=job_failure \
    --parameters failureFeatureName=team_league
```

# Deploy and run the template with Cloud Build from local machine

### Set env vars in your Shell

```shell
export PROJECT_ID={{your_project_id}}
export LOCATION={{your_location}}
```

### Deploy the Dataflow job with Cloud Build

```shell
gcloud builds submit \
    --project=$PROJECT_ID \
    --region=$LOCATION \
    --config dataflow-deploy-job.yaml \
    --substitutions _REPO_NAME="internal-images",_IMAGE_NAME="dataflow/team-league-java",_IMAGE_TAG="latest",_METADATA_TEMPLATE_FILE_PATH="gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java.json",_SDK_LANGUAGE="JAVA",_FLEX_TEMPLATE_BASE_IMAGE="JAVA11",_METADATA_FILE="config/metadata.json",_JAR="target/teams-league-0.1.0.jar",_FLEX_TEMPLATE_JAVA_MAIN_CLASS="fr.groupbees.application.TeamLeagueApp" \
    --verbosity="debug" .
```

### Run the Dataflow job with Cloud Build

```shell
gcloud builds submit \
    --project=$PROJECT_ID \
    --region=$LOCATION \
    --config dataflow-run-job.yaml \
    --substitutions _JOB_NAME="team-league-java",_METADATA_TEMPLATE_FILE_PATH="gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java.json",_TEMP_LOCATION="gs://mazlum_dev/dataflow/temp",_STAGING_LOCATION="gs://mazlum_dev/dataflow/staging",_SA_EMAIL="sa-dataflow-dev@gb-poc-373711.iam.gserviceaccount.com",_INPUT_FILE="gs://mazlum_dev/team_league/input/json/input_teams_stats_raw.json",_SIDE_INPUT_FILE="gs://mazlum_dev/team_league/input/json/input_team_slogans.json",_TEAM_LEAGUE_DATASET="mazlum_test",_TEAM_STATS_TABLE="team_stat",_JOB_TYPE="team_league_java_ingestion_job",_FAILURE_OUTPUT_DATASET="mazlum_test",_FAILURE_OUTPUT_TABLE="job_failure",_FAILURE_FEATURE_NAME="team_league" \
    --verbosity="debug" .
```

# Deploy and run the template with Cloud Build with triggers

### Run unit tests with automatic trigger on Github repository

```bash
gcloud beta builds triggers create github \
    --project=$PROJECT_ID \
    --region=$LOCATION \
    --repo-name=dataflow-java-ci-cd \
    --repo-owner=tosun-si \
    --branch-pattern=".*" \
    --build-config=dataflow-run-tests.yaml \
    --include-logs-with-status \
    --verbosity="debug"
```

### Run the deploy template with a manual trigger on Github repository

```bash
gcloud beta builds triggers create github \
    --project=$PROJECT_ID \
    --region=$LOCATION \
    --name="launch-dataflow-unit-tests-team-league-java" \
    --repo-name=dataflow-java-ci-cd \
    --repo-owner=tosun-si \
    --branch-pattern=".*" \
    --build-config=dataflow-run-tests.yaml \
    --include-logs-with-status \
    --verbosity="debug"
```

### Deploy the Flex Template with a manual trigger on Github repository

```bash
gcloud beta builds triggers create manual \
    --project=$PROJECT_ID \
    --region=$LOCATION \
    --name="deploy-dataflow-template-team-league-java" \
    --repo="https://github.com/tosun-si/dataflow-java-ci-cd" \
    --repo-type="GITHUB" \
    --branch="main" \
    --build-config="dataflow-deploy-job.yaml" \
    --substitutions _REPO_NAME="internal-images",_IMAGE_NAME="dataflow/team-league-java",_IMAGE_TAG="latest",_METADATA_TEMPLATE_FILE_PATH="gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java.json",_SDK_LANGUAGE="JAVA",_FLEX_TEMPLATE_BASE_IMAGE="JAVA11",_METADATA_FILE="-",_JAR="target/teams-league-0.1.0.jar",_FLEX_TEMPLATE_JAVA_MAIN_CLASS="fr.groupbees.application.TeamLeagueApp" \
    --verbosity="debug"
```

### Run the Flex Template with a manual trigger on Github repository

```bash
gcloud beta builds triggers create manual \
    --project=$PROJECT_ID \
    --region=$LOCATION \
    --name="run-dataflow-template-team-league-java" \
    --repo="https://github.com/tosun-si/dataflow-java-ci-cd" \
    --repo-type="GITHUB" \
    --branch="main" \
    --build-config="dataflow-run-job.yaml" \
    --substitutions _JOB_NAME="team-league-java",_METADATA_TEMPLATE_FILE_PATH="gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java.json",_TEMP_LOCATION="gs://mazlum_dev/dataflow/temp",_STAGING_LOCATION="gs://mazlum_dev/dataflow/staging",_SA_EMAIL="sa-dataflow-dev@gb-poc-373711.iam.gserviceaccount.com",_INPUT_FILE="gs://mazlum_dev/team_league/input/json/input_teams_stats_raw.json",_SIDE_INPUT_FILE="gs://mazlum_dev/team_league/input/json/input_team_slogans.json",_TEAM_LEAGUE_DATASET="mazlum_test",_TEAM_STATS_TABLE="team_stat",_JOB_TYPE="team_league_java_ingestion_job",_FAILURE_OUTPUT_DATASET="mazlum_test",_FAILURE_OUTPUT_TABLE="job_failure",_FAILURE_FEATURE_NAME="team_league" \
    --verbosity="debug"
```

