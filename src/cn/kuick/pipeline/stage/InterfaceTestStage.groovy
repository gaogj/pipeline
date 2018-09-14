package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	接口测试
 */
class InterfaceTestStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	InterfaceTestStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.script.node('aliyun345-build') {
		    	this.script.checkout this.script.scm

		       	this.run();
		    }
		}
	}

	def imageRun(image, args, command) {
		def docker = this.script.docker;
		def id = image.id;

		docker.node {
            docker.script.sh(script: "docker run ${args != '' ? ' ' + args : ''} ${id}${command != '' ? ' ' + command : ''}")
        }
	}

	def run() {
		def name = this.serverName;
		def version = this.version;
		def docker = this.script.docker;

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// 运行接口测试
			def testerImage = this.script.docker.image("registry.kuick.cn/cc/${name}-tester:${version}");
			this.imageRun(testerImage, "", "pact_test");
		}
	}
}