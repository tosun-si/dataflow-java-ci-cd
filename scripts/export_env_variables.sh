#!/usr/bin/env bash

set -e
set -o pipefail
set -u

export PROJECT_ID=gb-poc-373711
export LOCATION=europe-west1

export REPO_NAME=internal-images
export IMAGE_NAME="dataflow/team-league-java"
export IMAGE_TAG=latest
export METADATA_FILE="config/metadata.json"
export METADATA_TEMPLATE_FILE_PATH="gs://mazlum_dev/dataflow/templates/team_league/java/team-league-java.json"
export SDK_LANGUAGE=JAVA
export FLEX_TEMPLATE_BASE_IMAGE=JAVA11
export JAR=target/teams-league-0.1.0.jar
export FLEX_TEMPLATE_JAVA_MAIN_CLASS="fr.groupbees.application.TeamLeagueApp"
export JOB_NAME="team-league-java"

export TEMP_LOCATION=gs://mazlum_dev/dataflow/temp
export STAGING_LOCATION="gs://mazlum_dev/dataflow/staging"
export SA_EMAIL=sa-dataflow-dev@gb-poc-373711.iam.gserviceaccount.com
export INPUT_FILE="gs://mazlum_dev/team_league/input/json/input_teams_stats_raw.json"
export SIDE_INPUT_FILE="gs://mazlum_dev/team_league/input/json/input_team_slogans.json"
export TEAM_LEAGUE_DATASET=mazlum_test
export TEAM_STATS_TABLE=team_stat
export JOB_TYPE=team_league_java_ingestion_job
export FAILURE_OUTPUT_DATASET=mazlum_test
export FAILURE_OUTPUT_TABLE=job_failure
export FAILURE_FEATURE_NAME=team_league
