pipeline {
    agent any
    environment {
        APP_NAME = 'rest-app'
        NAMESPACE = 'lexis-nexis'
        IMAGE_TAG = 'latest'
        AWS_DEFAULT_REGION = 'eu-west-2'
        AWS_ACCOUNT_ID = '626254781145'
        ECR_REPOSITORY = 'narrative'
        CANARY_WEIGHT = '25'
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    script {
                        def testResult = junit testResults: '**/target/surefire-reports/TEST-*.xml', allowEmptyResults: true
                        def passedTests = testResult.passCount
                        def totalTests = testResult.totalCount
                        def passRate = (passedTests / totalTests) * 100
                        if (passRate < 80) {
                            error "Test pass rate is less than 80% (${passRate}%). Aborting pipeline."
                        }
                    }
                }
            }
        }
        stage('Build Image') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'aws-ecr-credentials', variable: 'DOCKER_CREDENTIALS')]) {
                        sh 'echo $DOCKER_CREDENTIALS | base64 -d | docker login -u AWS --password-stdin https://${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com'
                        docker.build("${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}", "--build-arg APP_NAME=${APP_NAME} .")
                        docker push "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}"
                    }
                }
            }
        }
        stage('Deploy Canary') {
            steps {
                script {
                    sh "kubectl apply -f kubernetes/canary.yaml -n ${NAMESPACE} --record"
                    sh "kubectl set image deployment/${APP_NAME} ${APP_NAME}=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG} --namespace ${NAMESPACE}"
                    sh "kubectl rollout status deployment/${APP_NAME}-canary --namespace ${NAMESPACE}"
                    sh "kubectl rollout pause deployment/${APP_NAME} --namespace ${NAMESPACE}"
                    sh "kubectl patch deployment ${APP_NAME} -p '{\"spec\":{\"template\":{\"metadata\":{\"labels\":{\"canary\":\"false\"}}}}}' --namespace ${NAMESPACE}"
                    sh "kubectl set image deployment/${APP_NAME} ${APP_NAME}=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG} --namespace ${NAMESPACE}"
                    sh "kubectl rollout resume deployment/${APP_NAME} --namespace ${NAMESPACE}"
                }
            }
        }
        stage('Integration Test') {
            steps {
                sh 'mvn verify -Pintegration-test'
            }
        }
    }
}
