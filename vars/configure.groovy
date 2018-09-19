
/**
 * 匹配Steps
 */

// See https://github.com/jenkinsci/workflow-cps-global-lib-plugin
import cn.kuick.pipeline.tasks.Tasks

// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def actionType = "${env.CHANGE_TYPE}";
    def branch = "${env.CHANGE_TARGET}";

    switch(actionType) {
    	// 匹配合并代码动作
    	case 'MERGE':
    		// 匹配分支
    		switch(branch) {

    			case 'master':
	    			def runTask = new Tasks(this,config);
	    			runTask.buildTest()
    				break;

    			case 'develop':
    				def runTask = new Tasks(this,config);
	    			runTask.buildTest()
    				break;
    				break
    			default:
    				sh "echo 分支匹配失败"
    				sh "exit 1"
    				break
    		}
    		break;

    	// 匹配推送代码动作
    	case 'PUSH':
    		switch(branch) {
    			case 'master':
	    			def runTask = new Tasks(this,config);
	    			runTask.buildTest()
	    			runTask.DeployToTest(true);  //跳过部署测试环境
	    			runTask.DeployToTest3()
	    			runTask.DeployToProd()
    				break;

    			case 'develop':
    				def runTask = new Tasks(this,config);
	    			runTask.buildTest()
	    			runTask.DeployToTest()
	    			runTask.DeployToTest3()
	    			runTask.DeployToProd()
    				break;
    			default:
    				sh "echo 分支匹配失败"
    				sh "exit 1"
    				break
    			}

    	case 'FIX_FLOW':
    		def runTask = new Tasks(this,config);
	    	runTask.buildTest()
	    	runTask.DeployToTest(true);  //跳过部署测试环境
	    	runTask.DeployToTest3()
	    	runTask.DeployToProd()
    		break;

   		case 'WHOLE_FLOW':
   			def runTask = new Tasks(this,config);
	    	runTask.buildTest()
	    	runTask.DeployToTest()
	    	runTask.DeployToTest3()
	    	runTask.DeployToProd()
    		break;
    	default:
    		sh "echo 动作匹配失败"
    		sh "exit 1"
    		break
		}
	}
