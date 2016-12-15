// See https://github.com/jenkinsci/workflow-cps-global-lib-plugin
import cn.kuick.pipeline.stage.UITestStage

// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call(stageName, body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def stage = new UITestStage(this, stageName, config);
    stage.start();
}