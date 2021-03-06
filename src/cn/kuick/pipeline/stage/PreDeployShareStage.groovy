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
			this.run327();
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
                    url: "https://git.kuick.cn/servers/shared.git",
                    branch: "develop",
                    credentialsId: 'kuick_git_auto_deploy_pwd'
	            ]);
	            }

            // need mv it to ./release/docker/predeploy.sh
			this.script.sh "./release/docker/test/predeploy.sh";

            // clean old libs
            this.script.sh "rm -rf libs/*";

            // 拉取子库
            this.script.sh "git submodule update --init --recursive";

			this.script.echo "test1-node1-327 pre-deploy success!"
	    }

}

	def run345() {
		def version = this.version;

		this.script.node('aliyun345-build') {
			this.script.echo "login to aliyun345-build"

	    	this.script.checkout this.script.scm

	        this.script.dir("shared") {
	            this.script.git([
                    url: "https://git.kuick.cn/servers/shared.git",
                    branch: "develop",
                    credentialsId: 'kuick_git_auto_deploy_pwd'
	            ]);
            }

            // need mv it to ./release/docker/predeploy.sh
			this.script.sh "./release/docker/test/predeploy.sh";

            // clean old libs
            this.script.sh "rm -rf libs/*";

            // 拉取子库
            this.script.sh "git submodule update --init --recursive";

			this.script.echo "build-server-345 pre-deploy success!"
	    }

}

}