package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	单元测试 + 生成镜像
 */
class UnitTestStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	UnitTestStage(script, stageName, config) {
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

	def buildTester() {
		def name = this.serverName;
		def version = this.version;

		this.script.docker.build("registry.kuick.cn/cc/${name}-tester:${version}", '-f ./release/docker/tester.docker .');
	}

	def buildRelease() {
		def name = this.serverName;
		def version = this.version;
		def workspace = this.script.pwd();

		def testerImage = this.script.docker.image("registry.kuick.cn/cc/${name}-tester:${version}")
		testerImage.run("-v ${workspace}/build/libs:/workspace/build/libs")

		this.script.docker.build("registry.kuick.cn/cc/${name}-server:${version}", '-f ./release/docker/Dockerfile .');
	}

	def run() {
		def docker = this.script.docker;

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// Build tester image
			this.buildTester();

			// Build release image
			this.buildRelease();
		}
	}
}