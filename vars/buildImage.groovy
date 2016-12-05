// See https://github.com/jenkinsci/workflow-cps-global-lib-plugin
import cn.kuick.pipeline.stage.BuildImageStage

// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "param name:${config.name}"
    echo "param version:${config.version}"

	stage "生成镜像"
    node('aliyun327-test') {
    	checkout scm

    	def stage = new BuildImageStage(this, config);
        stage.run();
    }
}