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

		this.script.node('osx-kuick001') {
			this.script.echo "login to osx-kuick001"

			this.script.sh "env";
			this.script.sh "pwd";
			this.script.sh "macaca doctor";

			this.script.echo "test success!"
	    }
	}
}