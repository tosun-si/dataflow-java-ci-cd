#!/usr/bin/env bash

set -e
set -o pipefail
set -u

echo "#######Run the Dataflow Flex Template pipeline"

gcloud dataflow flex-template run "$JOB_NAME-$(date +%Y%m%d-%H%M%S)" \
  --template-file-gcs-location "$METADATA_TEMPLATE_FILE_PATH" \
  --project="$PROJECT_ID" \
  --region="$LOCATION" \
  --temp-location="$TEMP_LOCATION" \
  --staging-location="$STAGING_LOCATION" \
  --parameters serviceAccount="$SA_EMAIL" \
  --parameters inputJsonFile="$INPUT_FILE" \
  --parameters inputFileSlogans="$SIDE_INPUT_FILE" \
  --parameters teamLeagueDataset="$TEAM_LEAGUE_DATASET" \
  --parameters teamStatsTable="$TEAM_STATS_TABLE" \
  --parameters jobType="$JOB_TYPE" \
  --parameters failureOutputDataset="$FAILURE_OUTPUT_DATASET" \
  --parameters failureOutputTable="$FAILURE_OUTPUT_TABLE" \
  --parameters failureFeatureName="$FAILURE_FEATURE_NAME"
