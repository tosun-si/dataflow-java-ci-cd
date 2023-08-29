resource "google_iam_workload_identity_pool" "github_actions_ci_cd_pool" {
  project                   = var.project_id
  workload_identity_pool_id = "gb-github-actions-ci-cd-pool"
  display_name              = "Pool CI CD Github actions"
  description               = "Pool for CI CD Github actions"
}

resource "google_iam_workload_identity_pool_provider" "github_actions_ci_cd_provider" {
  project                            = var.project_id
  workload_identity_pool_id          = google_iam_workload_identity_pool.github_actions_ci_cd_pool.workload_identity_pool_id
  workload_identity_pool_provider_id = "gb-github-actions-ci-cd-provider"
  attribute_mapping                  = {
    "google.subject"       = "assertion.sub"
    "attribute.repository" = "assertion.repository"
  }
  oidc {
    issuer_uri = "https://token.actions.githubusercontent.com"
  }
}

resource "google_service_account_iam_member" "dataflow_ci_cd_workload_identity_user" {
  service_account_id = "projects/${var.project_id}/serviceAccounts/sa-dataflow-dev@${var.project_id}.iam.gserviceaccount.com"
  role               = "roles/iam.workloadIdentityUser"
  member             = "principalSet://iam.googleapis.com/${google_iam_workload_identity_pool.github_actions_ci_cd_pool.name}/attribute.repository/${local.github_account_name}/${local.github_repo_name}"
}
