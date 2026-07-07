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

            }
        }

        stage('Git Info') {
            steps {
                echo "==============GIT INFO=================="
                echo "Commit : ${env.GIT_COMMIT}"
                echo "Branch : ${env.GIT_BRANCH}"
                echo "Job    : ${env.JOB_NAME}"
                echo "Build  : ${env.BUILD_NUMBER}"
                echo "Commit = ${env.GIT_COMMIT}"
                echo "Build URL = ${env.BUILD_URL}"
                echp "-----------------------------------------"
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
            echo "Build Completed successfully"
        }
        failure {
            echo "Build failed"
        }
    }
}