Jenkins CI/CD Setup for PlaywrightAutomationtask

This document explains how to configure Jenkins to run the repository pipeline, how the updated `Jenkinsfile` repo-sync works, how to provide credentials, and recommended secure options.

1) Overview

- The `Jenkinsfile` in the repo now contains a `Repo Sync` stage. It will:
  - Attempt `checkout scm` (if the Jenkins multibranch or pipeline job is configured with SCM).
  - Fetch and `git pull origin master` to bring the workspace up-to-date.
  - Optionally (when `AUTO_PUSH=true`) stage, commit, and push local workspace changes back to `origin/master` using credentials bound in the job.

2) Jenkins Job Parameters

- PLATFORM (choice): `ALL`, `WINDOWS_10`, `WINDOWS_11` — choose which platforms to run tests on.
- AUTO_PUSH (boolean): `false` by default. When `true`, the pipeline will `git add -A`, `git commit`, and `git push` back to the `origin/master`. Use with caution and prefer pushing to a branch.
- COMMIT_MESSAGE (string): Commit message to use when AUTO_PUSH is enabled. Default: `CI: update from Jenkins`.
- GIT_CREDENTIALS_ID (string): Jenkins credentialsId that contains the token/username+password used for pushing. Default: `github-creds`.

3) Recommended Jenkins Credentials Setup

Option A (recommended): Personal Access Token (PAT)
- Create a GitHub PAT with `repo` permission for repo writes.
- In Jenkins: Credentials > System > Global > Add Credentials
  - Kind: Username with password
  - Username: GitHub username (or any placeholder when using token only)
  - Password: Personal Access Token
  - ID: e.g. `github-creds`

Option B: Secret Text (token)
- Store the token as secret text and adapt the pipeline to use `string` credential binding. Current pipeline uses usernamePassword binding.

Option C (more secure): SSH deploy key (recommended for production pushes)
- Generate an SSH key and add it as a deploy key or machine user on the repo.
- Store the private key in Jenkins (SSH Username with private key) and modify pipeline to use `ssh-agent` and `git` over `git@github.com:owner/repo.git`.

4) Agent / Node Setup

Create two Jenkins agents (or reuse existing) with labels:
- `windows10`  (Windows 10 VM or node)
- `windows11`  (Windows 11 VM or node)

Install on both agents:
- Java 21 (Zulu or OpenJDK)
- Maven (3.x)
- Git
- Browsers (Edge installed on Windows 10 where you plan to use Edge channel; Chrome installed on Windows 11 if using Chrome)

Make sure the `PATH` includes the JDK and Git executables. The pipeline sets JAVA_HOME for the test stages, but tooling must be installed.

5) How to run the Pipeline

- Configure a Pipeline job with "Pipeline script from SCM" (recommended) and point it to this repo.
- Or create a Multibranch Pipeline for branch-based builds.
- For initial runs set `AUTO_PUSH=false`.
- Use "Build with Parameters" and set `PLATFORM=ALL` to run both Windows 10 and Windows 11.

6) What the Repo Sync stage does (details)

- `checkout scm` (if configured by the job): uses the configured SCM checkout method.
- If `checkout scm` is not configured (for manual pipeline script runs), the pipeline falls back to using the configured `repoUrl` in the script.
- Adds `origin` remote if missing.
- Runs `git fetch origin`, `git checkout master` (or create master if missing) and `git pull origin master`.
- If `AUTO_PUSH=true`:
  - Configures `git user` fields, stages all changes (`git add -A`) and then commits and pushes via an HTTPS URL that includes the Jenkins credentials (masked in logs).
  - Example push URL formed by the pipeline: `https://<GIT_USER>:<GIT_TOKEN>@github.com/owner/repo.git`

7) Security and best practices

- Prefer SSH deploy keys or an automated machine user with a PAT stored as a Jenkins credential over embedding tokens in pipeline logs.
- Avoid enabling `AUTO_PUSH` directly to `master`. Prefer a flow that pushes to a feature branch and then creates a PR for review.
- Ensure that the Jenkins credential used has the minimal required scope.
- Ensure sensitive output is masked in Jenkins (by default credentials binding masks the value in logs).

8) Troubleshooting

- If `git pull` fails because of diverged commits, the pipeline will print a warning. You should handle merges or rebase manually or add conflict resolution steps.
- If push fails with authentication error, confirm `GIT_CREDENTIALS_ID` points to valid credentials and the PAT has repo write permission.
- If `checkout scm` fails for multibranch pipeline, configure repository in the job's SCM settings and ensure webhooks or polling is configured.

9) Quick test commands for a Windows agent (PowerShell)

```powershell
# Set JAVA_HOME for the session
$env:JAVA_HOME = 'C:\Program Files\Zulu\zulu-21'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
# Run tests locally
mvn clean test -DplatformName=windows10 -DplatformVersion=10
```

10) Next steps I can do for you

- Convert AUTO_PUSH to push to a branch (e.g., `ci/autoupdate/${BUILD_NUMBER}`) instead of master and open a PR automatically.
- Replace HTTPS push with SSH-based push using an SSH key and `ssh-agent` credential binding (more secure).
- Add a small `docs/jenkins-setup.md` file to the repository (done) and commit any extra examples you want.

If you'd like, I can implement automatic branch pushes + PR creation next (safer than pushing to master). Let me know which option you prefer.
