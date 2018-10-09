package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	部署压测项目agent端环境
 */
class DeployPtsStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def commitId;
	def deployNode;
	def number;

	DeployPtsStage(script, stageName, config) {
		this.script = script;
		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.deployNode = config.deployNode;
		this.number = config.number;
		this.commitId = version[-6..-1];
	}

	def start() {
		this.script.stage(this.stageName) {
			this.script.echo this.deployNode
			this.script.echo this.version
			this.script.echo this.number
		    this.run();
		}
	}

	def run() {
		def version = this.version;
		def number = this.number
		if (deployNode == "aliyun311"){
			this.script.node("aliyun311-pts"){
				this.script.echo "login to aliyun311-pts"
				this.script.checkout this.script.scm
				this.script.sh "bash ./release/docker/agent/deploy.sh ${version} ${number}"
                this.script.echo "deploy agent success!"
			}
		}
	}
}