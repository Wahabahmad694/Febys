/* groovylint-disable LineLength */
node ('android-node') {
    app = null

    properties([disableConcurrentBuilds()])

    stage('Set Prerequisites') {
        env.NODEJS_HOME = "${tool 'Node-16'}"
        env.PATH="${env.NODEJS_HOME}/bin:${env.PATH}"
        sh 'npm --version'

        env.GRADLE_HOME = "${tool 'Gradle-7.1'}"
        env.PATH="${env.GRADLE_HOME}/bin:${env.PATH}"
        sh 'gradle -v'
    }

    stage('Checkout Repository') {

        cleanWs()
        
        checkout scm

        sh 'git rev-parse --short HEAD > commit-id'

        env.COMMIT_ID = readFile('commit-id').trim()
        env.PROJECT_NAME = (env.JOB_NAME.tokenize('/') as String[])[0]
        env.SONAR_KEY = (env.WORKSPACE.tokenize('/') as String[]).last()
        env.IMAGE_TAG = "synavoshub/${env.PROJECT_NAME}:${commit_id}"
        env.PATH = "${env.PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin"
        env.BUILD_TAG = "${env.PROJECT_NAME}-${commit_id}"
        env.FIREBASE_BUILD_URL = "https://appdistribution.firebase.dev/i/a6fd955b346e3c78"
        env.GIT_AUTHOR = sh (script: 'git log -1 --pretty=%cn ${GIT_COMMIT}', returnStdout: true).trim()
        env.GIT_COMMIT_MSG = sh (script: 'git log -1 --pretty=%B ${GIT_COMMIT}', returnStdout: true).trim()
        postMattermostReport("started")

        sh 'printenv'

        setupBuildCredentials()
    }

    stage('Setup Build Tools') {
        // set up gradle for tasks.
//         sh 'gradle wrapper'
        sh 'touch local.properties'
        sh 'cp /var/jenkins_home/projects/android/febys/firebase_config/google-services.json app/src/qa/google-services.json'
    }

    try{
        String buildPath = null

        stage('Tests') {
            sh './gradlew test'
        }

        stage('Code Analysis') {
            if (env.BRANCH_NAME != 'staging' || env.BRANCH_NAME != 'release' || env.BRANCH_NAME != 'master') {
                def sonarqubeScannerHome = tool name: 'sonar', type: 'hudson.plugins.sonar.SonarRunnerInstallation'

                withCredentials([string(credentialsId: 'sonar', variable: 'sonarLogin')]) {
                    sh "${sonarqubeScannerHome}/bin/sonar-scanner -e -Dsonar.host.url=https://sonar.synavos.com -Dsonar.login=${sonarLogin} -Dsonar.projectName=${env.SONAR_KEY} -Dsonar.projectVersion=${env.BUILD_TAG} -Dsonar.projectKey=${env.SONAR_KEY} -Dsonar.sources=${env.WORKSPACE} -Dsonar.exclusions=**/*.test.js -Dsonar.tests=${env.WORKSPACE} -Dsonar.test.inclusions=**/*.test.js -Dsonar.coverage.exclusions=**/*.boundary.* -Dsonar.cpd.exclusions=**/*.svg.js -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info"
                    checkSonarStatus(currentBuild, env)
                }
            }
        }

        stage('Build') {
            if (env.BRANCH_NAME == 'release') {
                sh './gradlew assembleRelease'
                buildPath = "app/build/outputs/apk/release/app-release.apk"
            }
            else if (env.BRANCH_NAME == 'staging') {
                sh './gradlew assembleStaging'
                buildPath = "app/build/outputs/apk/staging/app-staging.apk"
            }
            else if (env.BRANCH_NAME == 'develop') {
                sh './gradlew assembleQa'
                buildPath = "app/build/outputs/apk/qa/app-qa.apk"
            }
            else {
                sh './gradlew assembleDebug'
                buildPath = "app/build/outputs/apk/debug/app-debug.apk"
            }
        }

        if (env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'staging' || env.BRANCH_NAME == 'release') {
            stage('Distribution') {
                distributeApp(buildPath)
            }
        }

    } catch (e) {
        echo "Failed: ${e}"
        currentBuild.result = "FAILURE"
    } finally {
        if (currentBuild.result == "FAILURE") {
            postMattermostReport("failed")
        }else{
           postMattermostReport("success")
        }
    }
}

boolean isProductionBranch() {
    return env.BRANCH_NAME == 'release'
}

void setupBuildCredentials() {

    setupDebugCredentials()

}


void setupDebugCredentials() {
    withCredentials([
        file(credentialsId: 'febys-keystore', variable: 'debugKeystore'),
        file(credentialsId: 'febys-keystore-properties', variable: 'debugKeystoreProperties'),
        file(credentialsId: 'febysCredentials', variable: 'febysCredentials')
    ]) {
        sh 'mkdir -p app/keystore/'
        sh 'cp \$debugKeystore app/keystore/debug.keystore'
        sh 'cp \$debugKeystoreProperties app/keystore/debug.properties'
        sh 'cp \$debugKeystore app/keystore/release.keystore'
        sh 'cp \$debugKeystoreProperties app/keystore/release.properties'
        sh 'cp \$febysCredentials credentials.properties'
    }
}

void distributeApp(String buildPath) {
    if (env.BRANCH_NAME == 'release') {
        /*withCredentials([
                string(credentialsId: 'generateOneFromTeamForRelease', variable: 'firebaseToken'),
                string(credentialsId: 'yahudaFirebaseProjectReleaseAppIdDevOps', variable: 'appId')
            ]) {
                sh "firebase appdistribution:distribute ${buildPath} --app \$appId --groups ticketlake-qa --token \$firebaseToken --debug"
            }*/
        error('Signing information not set up for ${env.BRANCH_NAME}. Set up release and try again.')
    }
    else if (env.BRANCH_NAME == 'develop') {
        withCredentials([
            string(credentialsId: 'devOpsFirebaseToken', variable: 'firebaseToken'),
            string(credentialsId: 'febys-qa-app-id', variable: 'appId')
        ]) {
            sh "firebase appdistribution:distribute ${buildPath} --app \$appId --token \$firebaseToken --groups febys-qa --release-notes-file release-notes.txt --debug"
        }
    }
    else {
        /*withCredentials([
            string(credentialsId: 'generateOneFromTeamForRelease', variable: 'firebaseToken'),
            string(credentialsId: 'yahudaFirebaseProjectReleaseAppIdDevOps', variable: 'appId')
        ]) {
            sh "firebase appdistribution:distribute ${buildPath} --app \$appId --groups ticketlake-qa --token \$firebaseToken --debug"
        }*/

        error('Signing information not set up for ${env.BRANCH_NAME}. Set up release and try again.')
    }
}

void postMattermostReport(String build_flag){
    if (build_flag == "started") {
        mattermostSend (
            color: "#2A42EE",
            message: """Build Started:
            Author: ${env.GIT_AUTHOR}
            Commit Message: ${env.GIT_COMMIT_MSG}
            Repository Name: ${env.JOB_NAME}
            Build : ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Link to build>)"""
        )
    }
    else if(build_flag == "failed") {
        mattermostSend (
            color: "#e00707",
            message: """Build Failed:
            Author: ${env.GIT_AUTHOR}
            Commit Message: ${env.GIT_COMMIT_MSG}
            Repository Name: ${env.JOB_NAME}
            Build : ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Link to build>)"""
        )
    }
    else if (env.BRANCH_NAME ==  "develop"){
        mattermostSend (
            color: "#00f514",
            message: """Build Success:
            Author: ${env.GIT_AUTHOR}
            Commit Message: ${env.GIT_COMMIT_MSG}
            Repository Name: ${env.JOB_NAME}
            Build : ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Link to build>)"""
        )
        mattermostSend (
            color: "#00f514",
            channel: "qa-deployments",
            message: """@ayeshajaved Febys android build successfully deployed.
            Firebase Build URL: ${env.FIREBASE_BUILD_URL}"""
        )
    }
    else{
        mattermostSend (
            color: "#00f514",
            message: """Build Success:
            Author: ${env.GIT_AUTHOR}
            Commit Message: ${env.GIT_COMMIT_MSG}
            Repository Name: ${env.JOB_NAME}
            Build : ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Link to build>)"""
        )
    }
}

String checkSonarStatus(currentBuild, env) {
    sh "sleep 20"
    sh "curl -X GET -H 'Accept: application/json' http://sonarqube:9000/api/qualitygates/project_status?projectKey=${env.SONAR_KEY} > status.json"

    def json = readJSON file:'status.json'

    echo "${json.projectStatus.status}"

    /* if ("${json.projectStatus.status}" == "ERROR") {
        currentBuild.result = 'FAILURE'
        error('SonarQube quality gate failed, please see sonar for details.')
    } */
}
