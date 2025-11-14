pipeline {
    agent none

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    parameters {
        // Choose platform to run
        choice(name: 'PLATFORM', choices: ['ALL', 'WINDOWS_10', 'WINDOWS_11'], description: 'Select the platform to run tests on')
        // If true, Jenkins will attempt to commit & push local changes back to origin
        booleanParam(name: 'AUTO_PUSH', defaultValue: false, description: 'Automatically commit and push workspace changes back to origin')
        string(name: 'COMMIT_MESSAGE', defaultValue: 'CI: update from Jenkins', description: 'Commit message when AUTO_PUSH is enabled')
        string(name: 'GIT_CREDENTIALS_ID', defaultValue: 'github-creds', description: 'Jenkins credentialsId for Git (username/password or token)')
    }

    stages {
        stage('Repo Sync') {
            agent any
            steps {
                script {
                    // Ensure workspace has repository; checkout scm if configured
                    try {
                        checkout scm
                    } catch (e) {
                        echo "checkout scm failed or not configured: ${e.message}"
                    }

                    // Use credentials to perform authenticated operations
                    withCredentials([usernamePassword(credentialsId: params.GIT_CREDENTIALS_ID, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN')]) {
                        // Derive remote URL (prefer env.GIT_URL if available)
                        def repoUrl = env.GIT_URL ?: 'https://github.com/Krishnasai630/PlaywrightAutomationtask.git'

                        // On Windows agents use bat, for master use sh if necessary. We'll run generic bat commands here.
                        bat "echo Repo URL: ${repoUrl}"

                        // If origin is missing, add it with credentials
                        bat "powershell -Command \"if (-not (git remote)) { git remote add origin ${repoUrl} } else { Write-Host 'remote exists' }\""

                        // Pull latest changes from origin/master
                        bat 'git fetch origin || echo fetch-failed'
                        bat 'git checkout master || git checkout -b master'
                        pipeline {
                            agent any

                            tools {
                                maven 'Maven3'
                                jdk 'JDK21'
                            }

                            stages {
                                stage('Checkout') {
                                    steps {
                                        // Use the SCM configured for the job (Pipeline script from SCM) or fallback to checkout scm
                                        script { 
                                            try {
                                                checkout scm
                                            } catch (e) {
                                                echo "checkout scm failed: ${e.message}"
                                            }
                                        }
                                    }
                                }

                                stage('Build & Test') {
                                    steps {
                                        script {
                                            // Ensure JAVA_HOME is set on the agent or set it here if needed
                                            bat 'set JAVA_HOME=C:\\Program Files\\Zulu\\zulu-21 || echo JAVA_HOME already set'
                                            bat 'set PATH=%JAVA_HOME%\\bin;%PATH%'

                                            // Build project (skip executing tests) then run the dynamic TestNG runner
                                            // Step 1: compile main and test classes and package, without running tests
                                            bat 'mvn -DskipTests clean package'

                                            // Step 2: execute the DynamicTestNGRunner main class. Include test classpath so test classes are available.
                                            bat 'mvn exec:java -Dexec.mainClass="com.playwright.DynamicTestNGRunner" -Dexec.classpathScope=test'
                                        }
                                    }
                                    post {
                                        always {
                                            echo 'Build & Test stage finished'
                                        }
                                    }
                                }

                                stage('Publish Results') {
                                    steps {
                                        script {
                                            // Archive artifacts and publish JUnit results
                                            archiveArtifacts artifacts: 'target/surefire-reports/**/*', fingerprint: true
                                            junit 'target/surefire-reports/TEST-*.xml'

                                            // Generate and publish surefire HTML report if available
                                            bat 'mvn surefire-report:report-only || echo report-only failed'
                                            publishHTML([
                                                allowMissing: true,
                                                alwaysLinkToLastBuild: true,
                                                keepAll: true,
                                                reportDir: 'target/site',
                                                reportFiles: 'surefire-report.html',
                                                reportName: 'Test Report'
                                            ])
                                        }
                                    }
                                }
                            }

                            post {
                                always {
                                    echo "Pipeline finished with status: ${currentBuild.currentResult}"
                                }
                            }
                        }