package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	准备镜像
 */
class PrepareImageStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	PrepareImageStage(script, stageName, config) {
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
		def baseImage = docker.image("registry.kuick.cn/cc/${name}-server:base");

		try {
			baseImage.pull();
		} catch(e) {
			baseImage = docker.build("registry.kuick.cn/cc/${name}-server:base", '.');
			baseImage.push();
		}

		return baseImage;
	}

	def buildTestBase() {
		def name = this.serverName;
		def docker = this.script.docker;
		def baseImage = docker.image("registry.kuick.cn/cc/${name}-tester:base");

		try {
			baseImage.pull();
		} catch(e) {
			baseImage = docker.build("registry.kuick.cn/cc/${name}-tester:base", '-f ./release/docker/testBase.docker .');
			baseImage.push();
		}

		return baseImage;
	}

	def run() {
		def docker = this.script.docker;

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// Build base image
			this.buildBase();

			// Build TestBase image
			this.buildTestBase();
		}
	}
}