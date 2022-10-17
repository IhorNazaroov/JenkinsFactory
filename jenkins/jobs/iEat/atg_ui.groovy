node("iEAT-slave") {
    parameters { 
        string(name: 'MICROSERVICES_HOST', defaultValue: 'ieat.lab.epam.com')
        gitParameter(branch: '', branchFilter: '.*', defaultValue: 'origin/develop', description: '', name: 'GIT_BRANCH', quickFilterEnabled: false, selectedValue: 'DEFAULT', sortMode: 'NONE', tagFilter: '*', type: 'PT_BRANCH', useRepository: 'epm-rdk1/epm-rd1-atg-ui')
    }
    
    properties([
        buildDiscarder(logRotator(numToKeepStr: '25'))
    ])

    try {
    notifyBuild('STARTED')
    
    stage ('Stop UI and clean workspace') {
        sh '''
            ps auxfw | grep node | grep server.js | awk '{print $2}' | xargs kill -9 || echo 'no processes were found'
        '''
        cleanWs()
    }
    
    stage ('Git_pull') {
        checkout(
            scm: [
                $class: 'GitSCM',
                branches: [[name: "${params.GIT_BRANCH}"]],
                doGenerateSubmoduleConfigurations: false,
                extensions: [],
                submoduleCfg: [],
                userRemoteConfigs: [[url: 'git@git.epam.com:epm-rdk1/epm-rd1-atg-ui.git', credentialsId: 'ieat-git-readonly-ssh']]]
            )
    }

       stage ('Stop previous panel') {
            steps {
                sh '''
                   sudo docker ps | grep 'ui' | sudo xargs -r docker stop
                '''
            }
        }
        
        stage ('Run admin panel') {
            
                sh '''
                    
                    sudo docker build -f ./Dockerfile -t ui .
                    sudo docker run -p 3010:3010 -d ui 
                '''
            
        }
      }
    }