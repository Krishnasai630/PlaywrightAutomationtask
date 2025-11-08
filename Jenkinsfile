pipeline {
    agent none

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    parameters {
        // Add platform choice parameter
        choice(name: 'PLATFORM', choices: ['ALL', 'WINDOWS_10', 'WINDOWS_11'], description: 'Select the platform to run tests on')
    }

    stages {
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