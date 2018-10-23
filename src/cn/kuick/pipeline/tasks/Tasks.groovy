package cn.kuick.pipeline.tasks;

import java.io.Serializable;

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
import cn.kuick.pipeline.stage.StableTagStage
import cn.kuick.pipeline.stage.DeployPtsStage
import cn.kuick.pipeline.stage.AccessControlStage

class Tasks implements Serializable {

    def script;
    def config
    def changeType

    Tasks(script, config, changeType) {
        this.script = script;
        this.config = config
        this.changeType = changeType
        }

    def SkippedStage(stageName){
        this.script.stage(stageName) {
            this.script.echo 'Skipped'
        }
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
        if (this.config.projectType == "java") {
            def UnitTest = new UnitTestStage(this.script,'单元测试',this.config);
            UnitTest.start();
        }else{
            this.script.stage("单元测试") {
            this.script.echo '非JAVA项目，跳过单元测试!'
            }
        }
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

    def DeployToTest1(skip) {
        //部署测试环境
        if (skip){
        // if (this.changeType == "DEPLOY_TEST3" || this.changeType == "FIX_FLOW" || this.changeType == "DEPLOY_TEST2") {
            // 跳过以下步骤，保持视图完整
            SkippedStage("部署测试服务器")
        } else {
            //
            def DeployTest = new DeployTestStage(this.script,'部署测试服务器',this.config)
            DeployTest.start();
            }
    }

    def DeployToTest2(skip) {
        // 部署测试2
        if (skip){
        // if (this.changeType == "DEPLOY_TEST3" || this.changeType == "FIX_FLOW" ) {
            //如果是测试3和FIX_FLOW 则跳过，保持视图完整
            SkippedStage("确认部署测试2")
            SkippedStage("部署测试2服务器")
            SkippedStage("测试2API接口测试")
            SkippedStage("QA测试")
        }else{

            if (this.changeType == "DEPLOY_TEST2") {
                SkippedStage("确认部署测试2")
            }else{
                def DeployTest2Messger = new ConfirmMessgerStage(this.script,'确认部署测试2','该服务是否可以部署测试3?',this.config)
                DeployTest2Messger.start()
            }

            def DeployTest2 = new DeployTest2Stage(this.script,'部署测试2服务器',this.config)
            DeployTest2.start()

            SkippedStage("测试2API接口测试")

            def QATestMessger = new ConfirmMessgerStage(this.script,'QA测试','QA测试是否通过?',this.config)
            QATestMessger.start()
            }
        }


    def DeployToTest3() {
        // 部署测试3
        if (this.changeType == "DEPLOY_TEST3") {
            SkippedStage("确认部署测试3")
	    }else{
	        def DeployToTest3Messger = new ConfirmMessgerStage(this.script,'确认部署测试3','该服务是否可以部署测试3?',this.config)
	        DeployToTest3Messger.start()
	    }

        def DeployTest3 = new DeployTest3Stage(this.script,'部署测试3服务器',this.config)
        DeployTest3.start()

        def Test3ApiTesting = new Test3ApiTestingStage(this.script,'测试3API接口测试',this.config)
        Test3ApiTesting.start()

        SkippedStage("回归测试")

        def QATestMessger = new ConfirmMessgerStage(this.script,'验收测试','验收测试是否通过?',this.config)
        QATestMessger.start()

        }

    def DeployToProd() {
        // 部署生产环境
        // def AccessControlProd = new AccessControlStage(this.script,'生产环境权限校验',this.config) 
        // AccessControlProd.start()

        def DeployProdMessger = new ConfirmMessgerStage(this.script,'确认上线','该服务是否可以上线?',this.config)
        DeployProdMessger.start()

        def DeployProd = new DeployProdStage(this.script,'部署生产服务器',this.config)
        DeployProd.start()

        SkippedStage("冒烟测试")

        def StableTag = new StableTagStage(this.script,'构建稳定版本镜像',this.config)
        StableTag.start()
        // def SmokeTesting = new SmokeTestingStage(this.script,'冒烟测试',this.config)
        // SmokeTesting.start()
        }

    def Follow(){
        // 部署后操作
        def AutoChangeLog = new PostDeployAutoChangeLogStage(this.script,'自动生成Changelog',this.config)
        AutoChangeLog.start()

        def MergeMessger = new ConfirmMessgerStage(this.script,'确认合并分支','是否合并develop分支到master分支?',this.config)
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

    def DeployToPts(){
        // 部署压测项目
        def DeployPts = new DeployPtsStage(this.script,'部署压测项目agent端',this.config);
        DeployPts.start();
        }
    }
