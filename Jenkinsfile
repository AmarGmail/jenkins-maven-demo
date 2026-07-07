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
        stage('Checkout'){
            steps {
                echo "Getting the code from github..."
                echo "Branch: ${params.BRANCH_NAME}"
                git branch: params.BRANCH_NAME, 
                    url: 'https://github.com/AmarGmail/jenkins-maven-demo.git'

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