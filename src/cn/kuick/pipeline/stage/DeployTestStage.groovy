package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	部署测试环境
 */
class DeployTestStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	DeployTestStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.script.node('aliyun327-test') {
		    	this.script.checkout this.script.scm

		       	this.run();
		    }
		}
	}

	def run() {
		def version = this.version;

		// 部署测试环境
		this.script.sh "./release/docker/aliyun327-test/deploy.sh ${version}";
	}
}