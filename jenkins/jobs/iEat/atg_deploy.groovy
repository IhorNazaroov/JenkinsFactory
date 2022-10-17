node("iEAT-slave") {
    parameters { 
        string(name: 'MICROSERVICES_HOST', defaultValue: 'ieat.lab.epam.com')
    }
    
    properties([
        buildDiscarder(logRotator(numToKeepStr: '25'))
    ])
    
    try {
    notifyBuild('STARTED')
    
    stage ('Start_microservices'){
        dir("/home/jenkins/workspace/iEat/atg_microservices/"){
          sh '''
              sudo systemctl start docker
              sudo docker-compose down -v --remove-orphans
              sudo SEARCH_HOST_URL="${params.MICROSERVICES_HOST}" MONGO_HOST="${params.MICROSERVICES_HOST}" docker-compose up -d
              sudo docker image prune --force
              sleep 5m'''
        }
    }
    
    stage ('Start UI') {
        build job: 'atg_ui', propagate: false, wait: false
    }
  
    stage ('Starting API-Test') {
    build job: 'atg_test_apitest', wait: true, propagate: false 
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