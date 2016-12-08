// See https://github.com/jenkinsci/workflow-cps-global-lib-plugin
import cn.kuick.pipeline.plugin.PluginManager
import cn.kuick.pipeline.plugin.dockercompose.DockerComposePlugin;

def pluginManager = new PluginManager();

pluginManager.register("docker-compose", new DockerComposePlugin());

// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call(pluginName) {
    pluginManager.applyPlugin(this, pluginName);
}