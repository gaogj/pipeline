/**
 * 匹配Steps
 */

// See https://github.com/jenkinsci/workflow-cps-global-lib-plugin
import cn.kuick.pipeline.tasks.Tasks

// jenkinsfile 默认调用
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def changeType = "${env.CHANGE_TYPE}";
    def branch = "${env.CHANGE_TARGET}";
    
    def runTask = new Tasks(this,config,changeType)

    switch(changeType) {
    	// 匹配合并代码动作
    	case 'MERGE':
    		// 匹配分支
    		switch(branch) {
    			case 'master':
                    // MERGE到master分支场景，上线后合并分支触发，仅进行构建测试
                    runTask.BuildTest()
    			 //    runTask.DeployToTest3()
	    			// runTask.DeployToProd()
        //             runTask.Follow()
    				break;

    			case 'develop':
                // MERGE 到develop分支场景，代码已通过测试test3测试，需要触发上线
                    runTask.BuildTest()
                    runTask.DeployToTest1(skip = true)
                    runTask.DeployToTest2(skip = true)
                    runTask.DeployToTest3()
                    runTask.DeployToProd()
                    runTask.Follow()
    				break;
    			default:
    				sh "echo 分支匹配失败"
    				sh "exit 1"
    				break
    		}
    		break;

    	// 匹配推送代码动作
    	case 'PUSH':
    		switch(branch) {
                // PUSH 到 master分支场景，紧急修复上线
    			case 'master':
	    			runTask.BuildTest()
                    runTask.DeployToTest1(skip = true)
                    runTask.DeployToTest2(skip = true)
	    			runTask.DeployToTest3()
	    			runTask.DeployToProd()
                    runTask.Follow()
    				break;

    			case 'develop':
                // PUSH 到 develop分支场景 简单BUG修复，紧急修复上线
	    			runTask.BuildTest()
	    			runTask.DeployToTest1(skip = true)
                    runTask.DeployToTest2(skip = true)
	    			runTask.DeployToTest3()
	    			runTask.DeployToProd()
                    runTask.Follow()
    				break;
    			default:
    				sh "echo 分支匹配失败"
    				sh "exit 1"
    				break
    			}

    	case 'FIX_FLOW':
        // 热修复
	    	runTask.BuildTest()
	    	runTask.DeployToTest1(skip = true); 
            runTask.DeployToTest2(skip = true); 
	    	runTask.DeployToTest3()
	    	runTask.DeployToProd()
            runTask.Follow()
    		break;

   		case 'WHOLE_FLOW':
        // 全部流程
	    	runTask.BuildTest()
	    	runTask.DeployToTest1()
            runTask.DeployToTest2()
	    	runTask.DeployToTest3()
	    	runTask.DeployToProd()
            runTask.Follow()
    		break;

        case 'DEPLOY_TEST':
            runTask.BuildTest()
            runTask.DeployToTest1()
            break

        case 'DEPLOY_TEST2':
            runTask.BuildTest()
            runTask.DeployToTest1(skip = true)
            runTask.DeployToTest2()
            break

        case 'DEPLOY_TEST3':
            runTask.BuildTest()
            runTask.DeployToTest1(skip = true)
            runTask.DeployToTest2(skip = true)
            runTask.DeployToTest3()
            break

        case 'DEPLOY_PROD':
            runTask.BuildTest()
            runTask.DeployToTest1(skip = true)
            runTask.DeployToTest2(skip = true)
            runTask.DeployToTest3()
            runTask.DeployToProd()
            break

        case 'DEPLOY_PTS':
            runTask.BuildTest()
            runTask.DeployToPts()
            break

        case 'REBASE':
            runTask.Rebase()
            break

    	default:
    		sh "echo 动作匹配失败"
    		sh "exit 1"
    		break
	}
}
