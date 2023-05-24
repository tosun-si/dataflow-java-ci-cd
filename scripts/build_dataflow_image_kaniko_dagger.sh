#!/usr/bin/env bash

set -e
set -o pipefail
set -u

echo "#######Building Dataflow Docker image with Kaniko"

/kaniko/executor \
  --use-new-run \
  --compressed-caching="false" \
  --single-snapshot \
  --context=dir://./ \
  --dockerfile Dockerfile \
  --destination "$LOCATION-docker.pkg.dev/$PROJECT_ID/$REPO_NAME/$IMAGE_NAME:$IMAGE_TAG"
