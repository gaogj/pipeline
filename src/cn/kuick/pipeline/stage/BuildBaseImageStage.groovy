package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	生成基础镜像
 */
class BuildBaseImageStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	BuildBaseImageStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.script.node('aliyun345-test') {
		    	this.script.checkout this.script.scm

		        this.run();
		    }
		}
	}

	def buildBase() {
		def name = this.serverName;
		def docker = this.script.docker;

		def baseImage = docker.build("registry.kuick.cn/cc/${name}-server:base", '.');
		baseImage.push();

		return baseImage;
	}

	def buildTestBase() {
		def name = this.serverName;
		def docker = this.script.docker;

		def baseImage = docker.build("registry.kuick.cn/cc/${name}-tester:base", '-f ./release/docker/testBase.docker .');
		baseImage.push();

		return baseImage;
	}

	def run() {
		def docker = this.script.docker;
		def TestBaseImageExists = new File('release/docker/testBase.docker').exists()

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// pull submodule
			this.script.sh "git submodule update --init --recursive"

			// Build Base Image
			this.buildBase();

			// Build TestBase image
            this.script.sh "pwd"
//			if (TestBaseImageExists) {
			this.buildTestBase()
//			    }
//			else {
//                this.script.echo "Passed Build TestBase image!!!"
//                }
		}
	}
}