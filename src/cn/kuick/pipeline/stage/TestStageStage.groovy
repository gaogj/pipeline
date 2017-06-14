package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	部署测试环境
 */

 // This is test module 
class TestStageStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def commitId;

	TestStageStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.commitId = version[-6..-1];
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run();
		}
	}

	def run() {
		def version = this.version;

		this.script.node('aliyun327-test') {
			this.script.echo "login to aliyun327-test"

	    	this.script.checkout this.script.scm

	    	this.script.sh "git reset --hard ${commitId}"

	    	this.script.sh "release/docker/pull.sh ${version}"
	    	
			this.script.sh "./release/docker/test/deploy.sh ${version}";

			this.script.sh "git tag v${version} ${commitId}"

			this.script.echo "deploy test success!"
	    }
	}
}