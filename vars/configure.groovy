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

    def changeType = "${env.CHANGE_TYPE}"
    def branch = "${env.CHANGE_TARGET}"
    // 修复branch name错误，删除origin前缀
    if (branch.contains('origin/')){
        branch = branch[7..-1]
        config.branch = branch
    }
    
    def runTask = new Tasks(this,config,changeType)

    switch(changeType) {
    	// 匹配合并代码动作
    	case 'MERGE':
    		// 匹配分支
    		switch(branch) {
    			case 'master':
                    // MERGE到master分支场景 合并分支，并将build test结果同步到gitlab merge request
                    gitlabCommitStatus(name: "Merge Test") {
                        runTask.BuildTest()
                        addGitLabMRComment comment: '测试完成！'
                    }
    				break

    			case 'develop':
                    // MERGE 到develop分支场景 合并分支，并将build test结果同步到gitlab merge request
                    gitlabCommitStatus(name: "Merge Test") {
                        runTask.BuildTest()
                        addGitLabMRComment comment: '测试完成！'
                    }
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
                    runTask.DeployToTest1(true)
                    runTask.DeployToTest2(true)
	    			runTask.DeployToTest3()
	    			runTask.DeployToProd()
                    runTask.Follow()
    				break;

    			case 'develop':
                    // PUSH 到 develop分支场景 上线
	    			runTask.BuildTest()
	    			runTask.DeployToTest1(true)
                    runTask.DeployToTest2(true)
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
	    	runTask.DeployToTest1(true); 
            runTask.DeployToTest2(true); 
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
    		break

        case 'DEPLOY_TEST':
            runTask.BuildTest()
            runTask.DeployToTest1()
            break

        case 'DEPLOY_TEST2':
            runTask.BuildTest()
            runTask.DeployToTest1(true)
            runTask.DeployToTest2()
            break

        case 'DEPLOY_TEST3':
            runTask.BuildTest()
            runTask.DeployToTest1(true)
            runTask.DeployToTest2(true)
            runTask.DeployToTest3()
            break

        case 'DEPLOY_PROD':
            runTask.BuildTest()
            runTask.DeployToTest1(true)
            runTask.DeployToTest2(true)
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
