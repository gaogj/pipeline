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

	        this.script.dir("shared") {
	            this.script.git([
                    url: "https://git.oschina.net/kuick-cn/kuick-shared.git",
                    branch: "develop",
                    credentialsId: 'f1f2adb1-ccb4-4e29-bd61-7ea8eeba7770'
	            ]);
	            }

			this.script.sh "./release/docker/test/predeploy.sh";
            // 拉取子库
            this.script.sh "git submodule update --init --recursive";

			this.script.echo "pre-deploy test success!"
	    }

}

	def run345() {
		def version = this.version;

		this.script.node('aliyun345-test') {
			this.script.echo "login to aliyun345-test"

	    	this.script.checkout this.script.scm

	        this.script.dir("shared") {
	            this.script.git([
                    url: "https://git.oschina.net/kuick-cn/kuick-shared.git",
                    branch: "develop",
                    credentialsId: 'kuick_deploy'
	            ]);

            }
			this.script.sh "./release/docker/test/predeploy.sh";
            // 拉取子库
            this.script.sh "git submodule update --init --recursive";

			this.script.echo "pre-deploy test success!"
	    }

}

}