properties([gitLabConnection(''), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
parameters([string(defaultValue: '', description: 'Email of user (lower-case letters)', name: 'Name', trim: false), 
            choice(choices: ['Read access', 'global-admin'], description: '', name: 'Role')])])

node{
    stage('Grant permission for user') {
        withCredentials([usernamePassword(credentialsId: 'Admin-password', passwordVariable: 'USER_TOKEN', usernameVariable: 'USER_NAME')]) {
            sh label: 'Add user', script: 'curl --insecure -X POST https://jenkins.doaas.lab.com:8443/role-strategy/strategy/assignRole --data "type=globalRoles&roleName=$Role&sid=$Name" -u "$USER_NAME:$USER_TOKEN"'
        }
    }
}
