import io.jenkins.plugins.casc.ConfigurationAsCode;
node {
    stage('Reload JCasC') {
        ConfigurationAsCode.get().configure(env.HOME)
    }
}
