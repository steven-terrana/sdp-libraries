/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package libraries.sdp

import org.boozallen.plugins.jte.init.governance.config.dsl.PipelineConfigurationObject
import org.boozallen.plugins.jte.init.governance.config.dsl.PipelineConfigurationDsl

@Init
void call(){
    node{
        writeFile text: getPipelineConfig(), file: "pipeline_config.groovy"
        archiveArtifacts "pipeline_config.groovy"
    }
}

@NonCPS
def getPipelineConfig(){
  PipelineConfigurationObject aggregated = new PipelineConfigurationObject(null)
  aggregated.config = pipelineConfig
  return new PipelineConfigurationDsl(null).serialize(aggregated)
}
