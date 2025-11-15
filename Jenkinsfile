pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    // Explicitly checkout master branch
                    git branch: 'master', url: 'https://github.com/Krishnasai630/PlaywrightAutomationtask.git'
                }
            }
        }

        stage('Build & Run Tests') {
            steps {
                script {
                    bat 'set JAVA_HOME=C:\\Program Files\\Zulu\\zulu-21'
                    bat 'set PATH=%JAVA_HOME%\\bin;%PATH%'

                    bat 'mvn -DskipTests clean package'
                    bat 'mvn exec:java -Dexec.mainClass="com.playwright.DynamicTestNGRunner" -Dexec.classpathScope=test'
                }
            }
            post {
                always {
                    echo 'Build & Run Tests stage finished'
                }
            }
        }

        stage('Publish Results') {
            steps {
                script {
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', fingerprint: true
                    junit 'target/surefire-reports/TEST-*.xml'

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
