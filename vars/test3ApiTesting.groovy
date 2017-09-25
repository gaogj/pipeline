//// The call(body) method in any file in workflowLibs.git/vars is exposed as a
//// method with the same name as the file.
//def call(stageName, body) {
//    def config = [:]
//    body.resolveStrategy = Closure.DELEGATE_FIRST
//    body.delegate = config
//    body()
//
//    echo "stageName:${stageName}"
//    echo "param name:${config.name}"
//    echo "param version:${config.version}"
//
//    stage(stageName) {
//	    echo "${stageName} run ok!"
//	}
//}

// 迁移完成完成启用接口测试

// See https://github.com/jenkinsci/workflow-cps-global-lib-plugin
import cn.kuick.pipeline.stage.DeployProdVPCStage

// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call(stageName, body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def stage = new DeployProdVPCStage(this, stageName, config);
    stage.start();
}