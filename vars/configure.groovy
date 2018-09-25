/**
 * 匹配Steps
 */

// See https://github.com/jenkinsci/workflow-cps-global-lib-plugin
import cn.kuick.pipeline.stage.PreDeployShareStage
import cn.kuick.pipeline.stage.UITestStage
import cn.kuick.pipeline.stage.SonarQubeStage
import cn.kuick.pipeline.stage.BuildImageStage
import cn.kuick.pipeline.stage.UploadImageStage
import cn.kuick.pipeline.stage.ConfirmMessgerStage
import cn.kuick.pipeline.stage.DeployTestStage
import cn.kuick.pipeline.stage.DeployTest2Stage
import cn.kuick.pipeline.stage.DeployTest3Stage
import cn.kuick.pipeline.stage.DeployProdStage
import cn.kuick.pipeline.stage.Test3ApiTestingStage
import cn.kuick.pipeline.stage.SmokeTestingStage
import cn.kuick.pipeline.stage.PostDeployAutoChangeLogStage
import cn.kuick.pipeline.stage.PostDeployAutoMergeStage
import cn.kuick.pipeline.stage.BuildBaseImageStage
import cn.kuick.pipeline.stage.UnitTestStage

class Tasks implements Serializable {

    def script;
    def config
    def changeType

    Tasks(script, config, changeType) {
        this.script = script;
        this.config = config
        this.changeType = changeType
        }

    // 代码测试构建
    def BuildTest() {
        //
        def PreDeployShare = new PreDeployShareStage(this.script,'预部署依赖仓库',this.config);
        PreDeployShare.start();
        //
        this.script.stage("生成基础镜像") {
            this.script.echo 'Skipped build base image! If you want rebase image, please run REBASE!'
        }
        //
        def UnitTest = new UnitTestStage(this.script,'单元测试',this.config);
        UnitTest.start();
        //
        def SonarQube = new SonarQubeStage(this.script,'代码分析',this.config);
        SonarQube.start();
        //
        def BuildImage = new BuildImageStage(this.script,'构建镜像',this.config);
        BuildImage.start()
        //
        def UploadImage = new UploadImageStage(this.script,'上传镜像',this.config);
        UploadImage.start();
        }

    def DeployToTest1() {
        //部署测试环境
        if (this.changeType == "DEPLOY_TEST3" || this.changeType == "FIX_FLOW" || this.changeType == "DEPLOY_TEST2") {
            // 跳过以下步骤，保持视图完整
            this.script.stage("部署测试服务器") {
                this.script.echo 'Skipped'
                }
        } else {
            //
            def DeployTest = new DeployTestStage(this.script,'部署测试服务器',this.config)
            DeployTest.start();
            }
    }


    def DeployToTest2() {
        // 部署测试2
        if (this.changeType == "DEPLOY_TEST3" || this.changeType == "FIX_FLOW" ) {
            //如果是测试3和FIX_FLOW 则跳过，保持视图完整
            this.script.stage("确认部署测试2") {
                this.script.echo 'Skipped'
            }

            this.script.stage("部署测试2服务器") {
                this.script.echo 'Skipped'
            }

            this.script.stage("测试2 API接口测试")  {
                this.script.echo 'Skipped'
            }

            this.script.stage("QA测试") {
                this.script.echo 'Skipped'
            }
        }else{
            this.config.tips = '该服务是否可以部署测试2?'
            if (this.config.timeout == '' || this.config.timeout == null) {
                this.config.timeout = 12
            }
            if (this.config.timeoutUnit == '' ||this.config.timeoutUnit == null){
                this.config.timeoutUnit = 'HOURS'
            }
            def DeployTest2Messger = new ConfirmMessgerStage(this.script,'确认部署测试2',this.config)
            DeployTest2Messger.start()

            def DeployTest2 = new DeployTest2Stage(this.script,'部署测试2服务器',this.config)
            DeployTest2.start()

            this.script.stage("测试2 API接口测试") {
                    this.script.echo 'Skipped'
            }

            this.config.tips = 'QA测试是否通过??'
            if (this.config.timeout == '' || this.config.timeout == null) {
                this.config.timeout = 24
            }
            if (this.config.timeoutUnit == '' ||this.config.timeoutUnit == null){
                this.config.timeoutUnit = 'HOURS'
            }
            def QATestMessger = new ConfirmMessgerStage(this.script,'QA测试',this.config)
            QATestMessger.start()

            def UploadImage = new UploadImageStage(this.script,'QA测试',this.config)
            UploadImage.start()
            }
        }


    def DeployToTest3() {
        // 部署测试3
        this.config.tips = '该服务是否可以部署测试3?'
        if (this.config.timeout == '' || this.config.timeout == null) {
            this.config.timeout = 24
        }
        if (this.config.timeoutUnit == '' ||this.config.timeoutUnit == null){
            this.config.timeoutUnit = 'HOURS'
        }
        def DeployToTest3Messger = new ConfirmMessgerStage(this.script,'确认部署测试3',this.config)
        DeployToTest3Messger.start()

        def DeployTest3 = new DeployTest3Stage(this.script,'部署测试3服务器',this.config)
        DeployTest3.start()

        def Test3ApiTesting = new Test3ApiTestingStage(this.script,'测试3 API接口测试',this.config)
        Test3ApiTesting.start()

        this.script.stage('回归测试') {
            this.script.echo "Skipped"
        }

        this.config.tips = '验收测试是否通过?'
        if (this.config.timeout == '' || this.config.timeout == null) {
            this.config.timeout = 24
        }
        if (this.config.timeoutUnit == '' ||this.config.timeoutUnit == null){
            this.config.timeoutUnit = 'HOURS'
        }

        def QATestMessger = new ConfirmMessgerStage(this.script,'验收测试',this.config)
        QATestMessger.start()

        }

    def DeployToProd() {
        // 部署生产环境
        this.config.tips = '该服务是否可以上线?'
        if (this.config.timeout == '' || this.config.timeout == null) {
            this.config.timeout = 24
        }
        if (this.config.timeoutUnit == '' ||this.config.timeoutUnit == null){
            this.config.timeoutUnit = 'HOURS'
        }

        def DeployProdMessger = new ConfirmMessgerStage(this.script,'确认上线',this.config)
        DeployProdMessger.start()

        def DeployProd = new DeployProdStage(this.script,'部署生产服务器',this.config)
        DeployProd.start()

        def SmokeTesting = new SmokeTestingStage(this.script,'冒烟测试',this.config)
        SmokeTesting.start()
        }

    def Follow(){
        // 部署后操作
        def AutoChangeLog = new PostDeployAutoChangeLogStage(this.script,'自动生成Changelog',this.config)
        AutoChangeLog.start()

        this.config.tips = '是否合并develop分支到master分支?'
        if (this.config.timeout == '' || this.config.timeout == null) {
            this.config.timeout = 24
        }
        if (this.config.timeoutUnit == '' ||this.config.timeoutUnit == null){
            this.config.timeoutUnit = 'HOURS'
        }

        def MergeMessger = new ConfirmMessgerStage(this.script,'确认合并分支',this.config)
        MergeMessger.start()

        def AutoMerge = new PostDeployAutoMergeStage(this.script,'自动生成Changelog',this.config)
        AutoMerge.start()
        }

    def Rebase(){
        // 重新构建基础镜像
        def PreDeployShare = new PreDeployShareStage(this.script,'预部署依赖仓库',this.config);
        PreDeployShare.start();

        def BuildBaseImage = new BuildBaseImageStage(this.script,'生成基础镜像',this.config)
        BuildBaseImage.start()
        }
    }


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
                    runTask.BuildTest()
    			    runTask.DeployToTest3()
	    			runTask.DeployToProd()
                    runTask.Follow()
    				break;

    			case 'develop':
                    runTask.BuildTest()
                    runTask.DeployToTest1()
                    runTask.DeployToTest2()
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
    			case 'master':
	    			runTask.BuildTest()
	    			runTask.DeployToTest3()
	    			runTask.DeployToProd()
                    runTask.Follow()
    				break;

    			case 'develop':
	    			runTask.BuildTest()
	    			runTask.DeployToTest1()
                    runTask.DeployToTest2()
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
	    	runTask.BuildTest()
	    	runTask.DeployToTest1(); 
            runTask.DeployToTest2(); 
	    	runTask.DeployToTest3()
	    	runTask.DeployToProd()
            runTask.Follow()
    		break;

   		case 'WHOLE_FLOW':
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
            runTask.DeployToTest1()
            runTask.DeployToTest2()
            break

        case 'DEPLOY_TEST3':
            runTask.BuildTest()
            runTask.DeployToTest1()
            runTask.DeployToTest2()
            runTask.DeployToTest3()
            break

        case 'DEPLOY_PROD':
            runTask.BuildTest()
            runTask.DeployToTest1()
            runTask.DeployToTest2()
            runTask.DeployToTest3()
            runTask.DeployToProd()
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
