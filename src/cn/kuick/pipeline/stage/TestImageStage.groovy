package cn.kuick.pipeline.stage;

import java.io.Serializable;
import cn.kuick.pipeline.plugin.dockercompose.DockerComposePlugin;

/**
 *	测试镜像
 */
class TestImageStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def dockerCompose;

	TestImageStage(script, config) {
		this.script = script;

		this.stageName = '测试镜像';
		this.serverName = config.name;
		this.version = config.version;

		this.dockerCompose = new DockerComposePlugin();
	}

	def start() {
		this.dockerCompose.apply(this.script);
		
		this.script.stage this.stageName

	    this.script.node('aliyun327-test') {
	    	this.script.checkout this.script.scm

	        this.run();
	    }
	}

	def testImage() {
		def name = this.serverName;
		def version = this.version;

		def docker = this.script.docker;
		def image = docker.image("registry.kuick.cn/cc/${name}:${version}");

		def cluster = this.dockerCompose.up("./src/integration_test/resources/docker-compose.yml");

		cluster.inside(":last") {
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

}