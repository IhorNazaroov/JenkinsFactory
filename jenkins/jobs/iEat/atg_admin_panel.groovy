pipeline {
    //Agent to run (nodes with Docker)
    agent {
        label {
            label 'iEAT-slave'
        }
    }

    // Disable concurrent builds, resume
    options {
        disableConcurrentBuilds()
        disableResume()
    }

    stages {

        stage ('Remove previous version') {
            steps {
                cleanWs()
            } 
        }

        // Get sources from Git
        stage ('Code checkout') {
            steps {
                script {
                    checkout(
                        scm: [
                            $class: 'GitSCM',
                            branches: [[name: "${branchName}"]],
                            doGenerateSubmoduleConfigurations: false,
                            extensions: [],
                            submoduleCfg: [],
                            userRemoteConfigs: [[url: 'git@git.com:epm-rdk1/epm-rd1-atg-ui.git', credentialsId: 'ieat-git-readonly-ssh']]]
                        )
                }
            }
        }
        
        stage ('Stop previous panel') {
            steps {
                sh '''
                   sudo docker ps | grep 'admin' | sudo xargs -r docker stop
                '''
            }
        }
        
        stage ('Run admin panel') {
            steps {
              dir("admin") {
                sh '''
                    
                    sudo docker build -f ./Dockerfile -t admin_panel .
                    sudo docker run -p 3030:3030 -d admin_panel 
                '''
              }
            }
        }
    }
}
