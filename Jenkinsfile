pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    parameters {
        choice(name: 'SUITE', choices: ['testng.xml', 'testng-cross-browser.xml', 'testng-lambdatest.xml'], description: 'TestNG suite to execute')
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser (used only for the single-suite run)')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browsers headless')
    }

    environment {
        LT_USERNAME   = credentials('LT_USERNAME')
        LT_ACCESS_KEY = credentials('LT_ACCESS_KEY')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean compile'
            }
        }

        stage('Test') {
            steps {
                script {
                    sh """
                        mvn -B test \
                            -Dsuite=${params.SUITE} \
                            -Dbrowser=${params.BROWSER} \
                            -Dheadless=${params.HEADLESS}
                    """
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Allure Report') {
            steps {
                allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
            }
        }

        stage('Publish Extent Report') {
            steps {
                publishHTML(target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'test-output/extent-report',
                    reportFiles: '*.html',
                    reportName: 'Extent Report'
                ])
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'test-output/**, target/allure-results/**', allowEmptyArchive: true
            echo "Allure report:  ${env.BUILD_URL}allure/"
            echo "Extent report:  ${env.BUILD_URL}Extent_Report/"
        }
    }
}
