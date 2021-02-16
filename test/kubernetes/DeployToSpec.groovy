package libraries.kubernetes

public class DeployToSpec extends JTEPipelineSpecification {

  def deploy_to

  void setup(){
    deploy_to = loadPipelineScriptForStep("kubernetes", "deploy_to") 
    explicitlyMockPipelineStep("withGit")
    explicitlyMockPipelineStep("inside_sdp_image")
    explicitlyMockPipelineVariable("push")
  }

  def "helm repo added if defined"(){
    setup: 
    // mock library configuration 
    deploy_to.getBinding().setVariable("config", [
      helm_configuration_repository: "my_chart_repo",
      helm_configuration_repository_credential: "github",
      helm_configuration_repository_branch: "main",
      helm_configuration_repository_start_path: ".",
      k8s_credential: "kubeconfig",
      k8s_context: "default-context",
      remote_chart_repository: "https://my-chart-repo.com",
      remote_chart_name: "chartName"
    ])
    // mock application environment object 
    def dev = [ short_name: "dev", long_name: "Development" ]
    // mock environment variables 
    deploy_to.getBinding().setVariable("env", [
      REPO_NAME: "a-b",
      GIT_SHA: "abc123"
    ])
    // mock files in workspace
    1 * getPipelineMock("readYaml")([file: "values.dev.yaml"]) >> [ image_shas: [:] ]

    when: 
    deploy_to(dev)
    then:
    1 * getPipelineMock("sh")({ it =~ /^helm repo add .* https:\/\/my-chart-repo/ })
  }



}