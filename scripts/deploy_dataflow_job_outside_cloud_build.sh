#!/usr/bin/env bash

set -e
set -o pipefail
set -u

echo "#######Creating Dataflow Flex Template"

gcloud builds submit \
  --project="$PROJECT_ID" \
  --region="$LOCATION" \
  --config dataflow-deploy-job.yaml \
  --substitutions _REPO_NAME="$REPO_NAME",_IMAGE_NAME="$IMAGE_NAME",_IMAGE_TAG="$IMAGE_TAG",_METADATA_TEMPLATE_FILE_PATH="$METADATA_TEMPLATE_FILE_PATH",_SDK_LANGUAGE="$SDK_LANGUAGE",_FLEX_TEMPLATE_BASE_IMAGE="$FLEX_TEMPLATE_BASE_IMAGE",_METADATA_FILE="$METADATA_FILE",_JAR="$JAR",_FLEX_TEMPLATE_JAVA_MAIN_CLASS="$FLEX_TEMPLATE_JAVA_MAIN_CLASS" \
  --verbosity="debug" .
