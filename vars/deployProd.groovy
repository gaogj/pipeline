// See https://github.com/jenkinsci/workflow-cps-global-lib-plugin
import cn.kuick.pipeline.stage.DeployProdStage

// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call(stageName, body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def stage = new DeployProdStage(this, stageName, config);
    stage.start();
}

//// 迁移完成完成删除下面
//
//// See https://github.com/jenkinsci/workflow-cps-global-lib-plugin
//import cn.kuick.pipeline.stage.DeployProdVPCStage
//
//// The call(body) method in any file in workflowLibs.git/vars is exposed as a
//// method with the same name as the file.
//def callvpc(stageName, body) {
//    def config = [:]
//    body.resolveStrategy = Closure.DELEGATE_FIRST
//    body.delegate = config
//    body()
//
//    def stage = new DeployProdVPCStage(this, stageName, config);
//    stage.start();
//}