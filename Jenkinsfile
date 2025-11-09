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
                        bat 'git pull origin master || echo no-remote-or-no-updates'

                        // If AUTO_PUSH enabled, commit and push any local changes
                        if (params.AUTO_PUSH.toBoolean()) {
                            // Configure git user
                            bat 'git config user.email "jenkins@example.com"'
                            bat 'git config user.name "jenkins-ci"'

                            // Stage all changes
                            bat 'git add -A'

                            // If there are staged changes, commit and push using credentials
                            // The following uses exitCode check via powershell
                            bat 'powershell -Command "if ((git diff --cached --name-only) -ne '') { git commit -m \"'${params.COMMIT_MESSAGE}'\"; git push https://${env:GIT_USER}:${env:GIT_TOKEN}@${repoUrl.replaceFirst('https://','')} master } else { Write-Host \"No changes to commit\" }"'
                        } else {
                            echo 'AUTO_PUSH disabled; skipping push'
                        }
                    }
                }
            }
        }

        stage('Build') {
            agent any
            steps {
                script {
                    // Clean and compile
                    bat 'mvn clean compile'
                }
            }
        }
        
        stage('Test Execution') {
            parallel {
                stage('Windows 10') {
                    when {
                        expression { params.PLATFORM == 'ALL' || params.PLATFORM == 'WINDOWS_10' }
                    }
                    agent {
                        node {
                            label 'windows10'
                        }
                    }
                    steps {
                        script {
                            try {
                                // Set Java environment
                                bat 'set JAVA_HOME=C:\\Program Files\\Zulu\\zulu-21'
                                bat 'set PATH=%JAVA_HOME%\\bin;%PATH%'
                                
                                // Run tests with Windows 10 profile
                                bat 'mvn test -DplatformName=windows10 -DplatformVersion=10'
                            } catch (Exception e) {
                                currentBuild.result = 'FAILURE'
                                error("Windows 10 Test Execution Failed: ${e.message}")
                            }
                        }
                    }
                    post {
                        always {
                            // Archive test reports
                            archiveArtifacts artifacts: 'target/surefire-reports/**/*', fingerprint: true
                            junit '**/target/surefire-reports/TEST-*.xml'
                        }
                    }
                }

                stage('Windows 11') {
                    when {
                        expression { params.PLATFORM == 'ALL' || params.PLATFORM == 'WINDOWS_11' }
                    }
                    agent {
                        node {
                            label 'windows11'
                        }
                    }
                    steps {
                        script {
                            try {
                                // Set Java environment
                                bat 'set JAVA_HOME=C:\\Program Files\\Zulu\\zulu-21'
                                bat 'set PATH=%JAVA_HOME%\\bin;%PATH%'
                                
                                // Run tests with Windows 11 profile
                                bat 'mvn test -DplatformName=windows11 -DplatformVersion=11'
                            } catch (Exception e) {
                                currentBuild.result = 'FAILURE'
                                error("Windows 11 Test Execution Failed: ${e.message}")
                            }
                        }
                    }
                    post {
                        always {
                            // Archive test reports
                            archiveArtifacts artifacts: 'target/surefire-reports/**/*', fingerprint: true
                            junit '**/target/surefire-reports/TEST-*.xml'
                        }
                    }
                }
            }
        }

        stage('Generate Report') {
            agent any
            steps {
                script {
                    // Merge reports from different platforms
                    bat 'mvn surefire-report:report-only'
                }
            }
            post {
                always {
                    // Publish the merged report
                    publishHTML([
                        allowMissing: false,
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
            node('master') {
                // Send email notification
                emailext (
                    subject: "Test Execution Status: ${currentBuild.currentResult}",
                    body: """
                        Build Status: ${currentBuild.currentResult}
                        Build Number: ${currentBuild.number}
                        Build URL: ${env.BUILD_URL}
                        
                        Please check the build for more details.
                    """,
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
                )
            }
        }
    }
}