package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	Test3 api 接口测试
 */
class Test3ApiTestingStage implements Serializable {
	def script;
	def config;
	def stageName;
	def serverName;
	def version;
	def projectType;
	def lockFile;

	Test3ApiTestingStage(script, stageName, config) {
		this.script = script;
		this.config = config;
		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.projectType = config.type;
	}

	def start() {
		if (this.config.useApiTest) {
			this.script.stage(this.stageName) {
			    this.script.node('aliyun345-build') {
			    	this.script.echo "login to aliyun345-build"
			    	this.script.checkout this.script.scm

			       	this.run();
			    }
			}
		}else{
			this.script.stage(this.stageName) {
				this.script.echo "skip ApiTest"
			}
		}
	}

	def run() {
		def version = this.version;
		def docker = this.script.docker;
		def serverName = this.serverName;
		def projectType = this.projectType;

		this.script.checkout this.script.scm

		this.script.dir("api-test") {
	    	this.script.git([
	        	url: "https://git.kuick.cn/tests/api-test.git",
	            branch: "develop",
	            credentialsId: 'kuick_git_auto_deploy_pwd'
	        ]);

	    try {
	    	def lockFile = "/var/run/jenkisn_api_test_test3.lock" 
	    	// Create a lock
	    	this.script.sh "echo $BUILD_ID:$JOB_NAME > ${lockFile}"
	    	this.script.sh "cd api-test && sh run_test3.sh "
	    }
	    catch(Exception e) {
	    	this.script.echo "test3 api 测试失败: ${e}"
	    	this.script.sh 'exit 1'
	    }
	    finally {
	    	this.script.sh "rm ${lockFile}"
	    }
		}
	}
}