package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	部署测试2环境
 */
class DeployTest2Stage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def deployNode;

	DeployTest2Stage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;

		this.serverName = config.name;
		this.version = config.version;
		this.deployNode = config.deployNode;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run();
		}
	}

	def run() {
		def version = this.version;
		def deployNode = this.deployNode;

		// 部署测试2环境
		this.script.node("${deployNode}-test2") {
	        this.script.echo "login to ${deployNode}-test2"

	        this.script.checkout this.script.scm

	        this.script.sh "release/docker/test2/deploy.sh ${version}"

	        this.script.echo "deploy test2 ok!"
	    }
	}
}