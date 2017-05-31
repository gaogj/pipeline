package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	预部署shared
 */
class PreDeployShareStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	PreDeployShareStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run345();
		}
	}

	def run327() {
		def version = this.version;

		this.script.node('aliyun327-test') {
			this.script.echo "login to aliyun327-test"

	    	this.script.checkout this.script.scm

			this.script.sh "./release/docker/test/predeploy.sh";

			this.script.echo "pre-deploy test success!"
	    }

}

	def run345() {
		def version = this.version;

		this.script.node('aliyun345-test') {
			this.script.echo "login to aliyun345-test"

	    	this.script.checkout this.script.scm

			this.script.sh "./release/docker/test/predeploy.sh";

			this.script.echo "pre-deploy test success!"
	    }

}

}