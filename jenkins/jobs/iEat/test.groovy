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
    stage ('Git_pool') {
    dir("$WORKSPACE") {
        checkout(
            scm: [
                $class: 'GitSCM',
                branches: [[name: "${params.GIT_BRANCH}"]],
                doGenerateSubmoduleConfigurations: false,
                extensions: [],
                submoduleCfg: [],
                userRemoteConfigs: [[url: 'git@git.epam.com:epm-rdk1/epm-rd1-atg-microservices.git', credentialsId: 'ieat-git-readonly-ssh']]]
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
