node("iEAT-slave") {
  branchName = 'ATG_API_testing'

  properties([buildDiscarder(logRotator(numToKeepStr: '25'))])

  try {
  notifyBuild('STARTED')
  stage ('Git_pull') {
    dir("$WORKSPACE") {
      checkout(
          scm: [
              $class: 'GitSCM',
              branches: [[name: "${branchName}"]],
              doGenerateSubmoduleConfigurations: false,
              extensions: [],
              submoduleCfg: [],
              userRemoteConfigs: [[url: 'git@git.epam.com:epm-rdk1/epm-khrd-atg-rd.git', credentialsId: 'ieat-git-readonly-ssh']]]
      )
    }
  }

  stage ('Maven_test') {
       dir("$WORKSPACE"){
         sh '''mvn clean test -Dcucumber.options="--tags ~@wip --tags ~@bug"'''
       }
  }
  stage ('Starting ATG-ui') {
    build job: 'atg_ui', wait: true, propagate: false 
  }

  post {
    script {
      dir("$WORKSPACE"){
        cucumber fileIncludePattern: '**/cucumber.json', sortingMethod: 'ALPHABETICAL'
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
