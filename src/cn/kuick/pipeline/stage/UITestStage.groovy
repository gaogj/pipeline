package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	UI测试环境
 */
class UITestStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	UITestStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run();
		}
	}

	def run() {
		def version = this.version;

		this.script.node('osx') {
			this.script.echo "login to osx"

			this.script.sh "env";
			this.script.sh "pwd";

			this.script.dir("ui-test") {
	            this.script.git([
	                url: "https://git.oschina.net/kuick-cn/kuick-deal-web-test.git", 
	                branch: "develop",
	                credentialsId: 'kuick_deploy'
	            ]);

//	            this.script.sh "make ui-test";
	            this.script.sh "make test-pc";
	        }

			this.script.echo "test success!"
	    }
	}
}