package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	构建镜像
 */
class BuildImageStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	BuildImageStage(script, config) {
		this.script = script;

		this.stageName = '生成镜像';
		this.serverName = config.name;
		this.version = config.version;
	}

	def buildBase() {
		def name = this.serverName;
		def docker = this.script.docker;
		def baseImage = docker.image("registry.kuick.cn/cc/${name}:base");

		try {
			baseImage.pull();
		} catch(e) {
			baseImage = docker.build("registry.kuick.cn/cc/${name}:base", '.');
			baseImage.push();
		}

		return baseImage;
	}

	def buildRelease() {
		def name = this.serverName;
		def version = this.version;

		def docker = this.script.docker;
		def releaseImage = docker.image("registry.kuick.cn/cc/${name}:${version}");

		// build
		releaseImage = docker.build("registry.kuick.cn/cc/${name}:${version}", '-f ./release/docker/Dockerfile');

		return releaseImage;
	}

	def run() {
		def baseImage, releaseImage;
		def docker = this.script.docker;

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// Build base image
			baseImage = this.buildBase();

			// Build release image
			releaseImage = this.buildRelease();
		}

		return releaseImage;
	}

	def start() {
		this.script.stage this.stageName

	    this.script.node('aliyun327-test') {
	    	this.script.checkout this.script.scm

	        this.run();
	    }
	}
}