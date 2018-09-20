
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

class Tasks implements Serializable {

    def script;
    def config

    def stageName;
    def serverName;
    def version;
    def deployNode;
    def commitId;

    Tasks(script, config) {
        this.script = script;
        this.config = config
        }

    // 对代码进行测试并构建
    def buildTest(upload) {
        def PreDeployShare = new PreDeployShareStage(this.script,'预部署依赖仓库',this.config);
        PreDeployShare.start();
        //
        this.script.stage("生成基础镜像") {
            this.script.echo 'Skipped build base image! If you want rebase image, please run REBASE!'
        }
        //
        def UITest = new UITestStage(this.script,'单元测试',this.config);
        UITest.start();
        //
        def SonarQube = new SonarQubeStage(this.script,'代码分析',this.config);
        SonarQube.start();
        //
        def BuildImage = new BuildImageStage(this.script,'构建镜像',this.config);
        BuildImage.start()
        //
        if (upload) {
            def UploadImage = new UploadImageStage(this.script,'上传镜像',this.config);
            UploadImage.start()
        }else{
            this.script.stage("上传镜像") {
                this.script.echo 'Skipped'
            }
        }
        //
        this.script.addGitLabMRComment comment: '测试完成!'

        //TODO send email
    }

    def DeployToTest(skip) {

        if (skip == true) {
            // 跳过以下步骤，保持视图完整
            this.script.stage("部署测试服务器") {
                this.script.echo 'Skipped'
            }

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
        //
            def DeployTest = new DeployTestStage(this.script,'部署测试服务器',this.config)
            DeployTest.start()

            this.config.tips = '该服务是否可以部署测试2?'
            this.config.timeout = 12
            this.config.timeoutUnit = 'HOURS'
            def DeployTest2Messger = new ConfirmMessgerStage(this.script,'确认部署测试2',this.config)
            ConfirmMessger.start()

            def DeployTest2 = new DeployTest2Stage(this.script,'部署测试2服务器',this.config)
            DeployTest2.start()

            this.script.stage("测试2 API接口测试") {
                this.script.echo 'Skipped'
            }

            this.config.tips = 'QA测试是否通过??'
            this.config.timeout = 12
            this.config.timeoutUnit = 'HOURS'
            def QATestMessger = new ConfirmMessgerStage(this.script,'QA测试',this.config)
            ConfirmMessger.start()
        }

    }


    def DeployToTest3() {
        //
        this.config.tips = '该服务是否可以部署测试3?'
        this.config.timeout = 24
        this.config.timeoutUnit = 'HOURS'
        def DeployToTest3Messger = new ConfirmMessgerStage(this.script,'确认部署测试3',this.config)
        DeployToTest3Messger.start()
        //
        def DeployTest3 = new DeployTest3Stage(this.script,'部署测试3服务器',this.config)
        DeployTest3.start()
        // 
        def Test3ApiTesting = new Test3ApiTestingStage(this.script,'测试3 API接口测试',this.config)
        Test3ApiTesting.start()
        //
        this.script.stage('回归测试') {
            echo "Skipped"
        }
        //
        this.config.tips = '验收测试是否通过?'
        this.config.timeout = 12
        this.config.timeoutUnit = 'HOURS'
        def QATestMessger = new ConfirmMessgerStage(this.script,'验收测试',this.config)
        QATestMessger.start()
        //
    }
    //
    def DeployToProd() {
        //
        this.config.tips = '该服务是否可以上线?'
        this.config.timeout = 24
        this.config.timeoutUnit = 'HOURS'
        def DeployProdMessger = new ConfirmMessgerStage(this.script,'确认上线',this.config)
        DeployProdMessger.start()
        //
        def DeployProd = new DeployProdStage(this.script,'部署测试3服务器',this.config)
        DeployProd.start()
        //
        def SmokeTesting = new SmokeTestingStage(this.script,'冒烟测试',this.config)
        SmokeTesting.start()
        //
    }
}

// public class Person {
//     private String name;
//     Person(String name) {
//         this.name = name
//     }
//     public String getName() {
//         return name
//     }
//     public void setName(String name) {
//         this.name = name
//     }
// }

// In Groovy
class Person {
    String name
}


def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def actionType = "${env.CHANGE_TYPE}";
    def branch = "${env.CHANGE_TARGET}";

    // def person = new Person(name: 'test')
    // println(person.name)

    // def runTask = new Tasks(this,config)
    // switch(actionType){
    //     case 'WHOLE_FLOW':
    //         println(runTask.config)
    //         break
    //     default:
    //         sh "echo 分支匹配失败"
    //         sh "exit 1"
    //         break
    // }
    
    def runTask = new Tasks(this,config)
    
    switch(actionType) {
    	// 匹配合并代码动作
    	case 'MERGE':
    		// 匹配分支
    		switch(branch) {
    			case 'master':
	    			runTask.buildTest()
    				break;

    			case 'develop':
    				// def runTask = new Tasks(this,config);
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
	    			// def runTask = new Tasks(this,config);
	    			runTask.buildTest()
	    			runTask.DeployToTest(true);  //跳过部署测试环境
	    			runTask.DeployToTest3()
	    			runTask.DeployToProd()
    				break;

    			case 'develop':
    				// def runTask = new Tasks(this,config);
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
    		// def runTask = new Tasks(this,config);
	    	runTask.buildTest()
	    	runTask.DeployToTest(true);  //跳过部署测试环境
	    	runTask.DeployToTest3()
	    	runTask.DeployToProd()
    		break;

   		case 'WHOLE_FLOW':
   			// def runTask = new Tasks(this,config);
	    	runTask.buildTest(true)
	    	runTask.DeployToTest(true)
	    	runTask.DeployToTest3()
	    	runTask.DeployToProd()
    		break;
    	default:
    		sh "echo 动作匹配失败"
    		sh "exit 1"
    		break
	}
}
