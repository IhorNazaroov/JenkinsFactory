node("iEAT-slave") {
    parameters { 
        string(name: 'PROJECT_VERSION', defaultValue: '1.0')
        gitParameter(branch: '', branchFilter: '.*', defaultValue: 'origin/develop', description: '', name: 'GIT_BRANCH', quickFilterEnabled: false, selectedValue: 'DEFAULT', sortMode: 'NONE', tagFilter: '*', type: 'PT_BRANCH', useRepository: 'epm-rdk1/epm-rd1-atg-microservices')
    }

    properties([
        buildDiscarder(logRotator(numToKeepStr: '5'))
    ])
    
    stage ('Clean workspace') {
      cleanWs()
    }

    try {
    notifyBuild('STARTED')
    stage ('Git_pull') {
    dir("$WORKSPACE") {
        checkout(
            scm: [
                $class: 'GitSCM',
                branches: [[name: "${params.GIT_BRANCH}"]],
                doGenerateSubmoduleConfigurations: false,
                extensions: [],
                submoduleCfg: [],
            )
        }
    }
    stage ('Stop microservices'){
        dir("/home/jenkins/workspace/iEat/atg_microservices/"){
            sh 'sudo docker-compose down -v --remove-orphans'
        }
    }

    stage('Gradle build') {
        dir ("$WORKSPACE") {
        sh '''
            gradle build  -x test'''
        }
    }

    stage ('Check test results'){
          junit allowEmptyResults: true, testResults: 'build/**/test-results/TEST-*.xml'
      }
      
    stage ('Starting deploy') {
        build job: 'atg_deploy', propagate: false, wait: false
    }
    }
  }
  catch (e) {
    // If there was an exception thrown, the build failed
    currentBuild.result = "FAILED"
    throw e
  } finally {
    // Success or failure, always send notifications
    notifyBuild(currentBuild.result)
  }
}
def notifyBuild(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESSFUL'

  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"

  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'BLUE'
    colorCode = '#0000FF'
  } else if (buildStatus == 'SUCCESSFUL') {
    color = 'GREEN'
    colorCode = '#00FF00'
  }  else if (buildStatus == 'UNSTABLE') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else {
    color = 'RED'
    colorCode = '#FF0000'
  }

  // Send notifications
  slackSend (color: colorCode, message: summary)
}
