import groovy.json.JsonOutput

def updateGitHubStatus(string state, string description){

    def payload = JsonOutput.toJson([
        state       : state,
        context     : "jenkins CI",
        decription  : description,
        target_url  : env.BUILD_URL

    ])

    withCredentials([
        string(credentialsId: 'github-pat-build', variable: 'GITHUB_TOKEN')
    ])  {
        sh """
            curl --fail --silent --show-error \
            -X POST \
            -H "Accept: application/vnd.github=json" \
            -H "Authorization: Bearer \$GITHUB_TOKEN" \
            https://api.github.com/repos/AmarGmail/jenkins-maven-demo/statuses/${env.GIT_COMMIT} \
            -d '${payload}'
        """
    }
}

pipeline {
    agent any

    parameters {
        string(
            name: 'BRANCH_NAME',
            defaultValue: 'main',
            description: 'Github branch to build'
        )

        booleanParam(
                name: 'RUN_TESTS',
                defaultValue: true,
                description: "Run Junit tests"
        )

        choice(
            name: 'BUILD_ENV',
            choices: ['dev', 'qa', 'prod'],
            description: 'Target Environment'
        )
    }

    stages {
        stage('Initilize.....'){
            steps {
                echo "============Build Parameters==========="
                echo "Build Number : ${env.BUILD_NUMBER}"
                echo "Job Name     : ${env.JOB_NAME}"
                echo "Build ID     :${env.BUILD_ID}"
                echo "Workspace    : ${env.WORKSPACE}"
                echo "Build URL    : ${env.BUILD_URL}"
                echo "Branch      : ${params.BRANCH_NAME}"
                echo "Environment : ${params.BUILD_ENV}"
                echo "Run Tests   : ${params.RUN_TESTS}"
                echo "----------------------------------------"
            }
        }

        stage('Checkout'){
            steps {
                echo "Getting the code from github..."
                echo "Branch: ${params.BRANCH_NAME}"
                git branch: params.BRANCH_NAME, 
                    url: 'https://github.com/AmarGmail/jenkins-maven-demo.git'

                script {
                    updateGitHubStatus(
                        "pending",
                        "Build Started..."
                    )
                }
            }
        }

        stage('Git Info') {
            steps {
                echo "==============GIT INFO=================="
                echo "Branch : ${env.GIT_BRANCH}"
                echo "Job    : ${env.JOB_NAME}"
                echo "Build  : ${env.BUILD_NUMBER}"
                echo "Commit = ${env.GIT_COMMIT}"
                echo "Build URL = ${env.BUILD_URL}"
                echo "-----------------------------------------"
            }
        }

        stage('Test'){
            when {
                expression {
                    params.RUN_TESTS
                }
            }

            steps {
                sh 'mvn test'
            }
        }
        
        stage('Package'){
            steps {
                sh 'mvn package'
            }
        }

        stage('Environment'){
            steps {
                echo "Deploy target is ${params.BUILD_ENV}"
            }
        }
    }

    post {
        success{
            script {
                updateGitHubStatus{
                    "success",
                    "Build Passed"
                }
            }
            echo "Build Completed successfully"
        }
        failure {
            script {
                updateGitHubStatus(
                    "failure",
                    "Build failed"
                )
            }
            echo "Build failed"
        }
    }
}