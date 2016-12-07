package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	测试镜像
 */
class TestImageStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	TestImageStage(script, config) {
		this.script = script;

		this.stageName = '测试镜像';
		this.serverName = config.name;
		this.version = config.version;
	}

	def testImage() {
		def name = this.serverName;
		def version = this.version;

		def docker = this.script.docker;
		def image = docker.image("registry.kuick.cn/cc/${name}:${version}");

		image.inside {
			this.script.sh "echo $PATH"
			this.script.sh "gradle integration_test"
		}
	}

	def run() {
		def docker = this.script.docker;

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// Build base image
			this.testImage();
		}
	}

	def start() {
		this.script.stage this.stageName

	    this.script.node('aliyun327-test') {
	    	this.script.checkout this.script.scm

	        this.run();
	    }
	}
}