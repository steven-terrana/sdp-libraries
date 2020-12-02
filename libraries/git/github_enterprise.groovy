/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

// Import code required for GitHub functions
import org.kohsuke.github.GitHub

// Validate GitHub configuration is valid
void validate_configuration(){
    node{
        unstash "workspace"

        env.GIT_URL = scm.getUserRemoteConfigs()[0].getUrl()
        env.GIT_CREDENTIAL_ID = scm.getUserRemoteConfigs()[0].credentialsId.toString()
        def parts = env.GIT_URL.split("/")
        for (part in parts){
            parts = parts.drop(1)
            if (part.contains(".")) break
        }
        env.ORG_NAME = parts.getAt(0)
        env.REPO_NAME = parts[1..-1].join("/") - ".git"
        env.GIT_SHA = sh(script: "git rev-parse HEAD", returnStdout: true).trim()

        if (env.CHANGE_TARGET){
          env.GIT_BUILD_CAUSE = "pr"
        } else {
          env.GIT_BUILD_CAUSE = sh (
            script: 'git rev-list HEAD --parents -1 | wc -w', // will have 2 shas if commit, 3 or more if merge
            returnStdout: true
          ).trim().toInteger() > 2 ? "merge" : "commit"
        }

        println "Found Git Build Cause: ${env.GIT_BUILD_CAUSE}"
    }
    return
}

/*
    fetches the name of the source branch in a Pull Request.
*/
def get_source_branch(){
    String ghUrl = "${env.GIT_URL.split("/")[0..-3].join("/")}/api/v3"
    def repo, org
    withCredentials([
        usernamePassword(credentialsId: env.GIT_CREDENTIAL_ID, passwordVariable: 'PAT', usernameVariable: 'USER')
    ]) {
        return GitHub.connectToEnterprise(ghUrl, PAT).getRepository("${env.ORG_NAME}/${env.REPO_NAME}")
            .getPullRequest(env.CHANGE_ID.toInteger())
            .getHead()
            .getRef()
    }
}