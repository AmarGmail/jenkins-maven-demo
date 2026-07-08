import groovy.json.JsonOutput

def updateGitHubStatus(String state, String description){

    def payload = JsonOutput.toJson([
        state       : state,
        context     : "Jenkins CI",
        description : description,
        target_url  : env.BUILD_URL

    ])

    withCredentials([
        string(credentialsId: 'github-pat-build', variable: 'GITHUB_TOKEN')
    ])  {
        sh """
            curl --fail --silent --show-error \
            --location \
            -X POST \
            -H "Accept: application/vnd.github+json" \
            -H "Authorization: Bearer \$GITHUB_TOKEN" \
            https://api.github.com/repos/${env.GITHUB_OWNER}/${env.GITHUB_REPO}/statuses/${env.GIT_COMMIT} \
            -d '${payload}'
        """
    }
}

@NonCPS
def parseOwnerRepo(String gitUrl) {
    def matcher = (gitUrl =~ /github\.com[:\/]([^\/]+)\/([^\/]+?)(\.git)?$/)
    if (!matcher.find()) {
        error("Unable to parse owner/repo from GitHub repository URL: ${gitUrl}")
    }
    // Return plain, serializable Strings — the Matcher itself never leaves this method
    return [matcher.group(1), matcher.group(2)]
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

        string(
            name: 'REPOSITORY_URL',
            defaultValue: 'https://github.com/AmarGmail/jenkins-maven-demo.git',
            description: 'GitHub repository URL to build')
    }

    stages {
        stage('Initialize.....'){
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
                    url: params.REPOSITORY_URL // Use the parameter for repository URL

                script {
                    def gitUrl = params.REPOSITORY_URL

                    // Regex matching happens entirely inside the @NonCPS method —
                    // only plain Strings come back here, so nothing unserializable
                    // stays in scope when updateGitHubStatus()'s sh step runs.
                    def (owner, repo) = parseOwnerRepo(gitUrl)
                    env.GITHUB_OWNER = owner
                    env.GITHUB_REPO  = repo

                    echo "GIT URL      : ${gitUrl}"
                    echo "GitHub Owner : ${env.GITHUB_OWNER}"
                    echo "Repository   : ${env.GITHUB_REPO}"

                    updateGitHubStatus(
                        "pending",
                        "Build Started..."
                    )
                }
            }
        }

        stage('Git and Build Info') {
            steps {
                echo "==============GIT INFO=================="
                echo "Branch        : ${env.GIT_BRANCH}"
                echo "Job           : ${env.JOB_NAME}"
                echo "Build         : ${env.BUILD_NUMBER}"
                echo "Commit        = ${env.GIT_COMMIT}"
                echo "Build URL     = ${env.BUILD_URL}"
                echo "Owner         : ${env.GITHUB_OWNER}"
                echo "Repository    : ${env.GITHUB_REPO}"
                echo "-----------------------------------------"
                echo "==============BUILD INFO================"
                echo "Build Number  : ${env.BUILD_NUMBER}"
                echo "Job Name      : ${env.JOB_NAME}"
                echo "Build ID      :${env.BUILD_ID}"
                echo "Workspace     : ${env.WORKSPACE}"
                echo "Build URL     : ${env.BUILD_URL}"
                echo "Branch        : ${params.BRANCH_NAME}"
                echo "Environment   : ${params.BUILD_ENV}"
                echo "Run Tests     : ${params.RUN_TESTS}"
                sh 'java -version'
                sh 'mvn -version'
                sh 'git --version'
                echo "----------------------------------------"
            }
        }
        
        stage('Test'){
            when {
                expression {
                    params.RUN_TESTS
                }
            }

            steps {
                sh 'mvn clean test'
            }
        }
        
        stage('Package'){
            steps {
                sh 'mvn package  -DskipTests'
            }
        }

        stage('Archive Jar'){
            steps{
                echo "Saving jar for future"
                archiveArtifacts artifacts: 'target/*.jar',
                fingerprint: true,
                allowEmptyArchive: true
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
                updateGitHubStatus(
                    "success",
                    "Build #${env.BUILD_NUMBER} Passed"
                )
            }
            echo "Build Completed successfully"
        }
        failure {
            script {
                updateGitHubStatus(
                    "failure",
                    "Build #${env.BUILD_NUMBER} failed"
                )
            }
            echo "Build failed"
        }
    }
}