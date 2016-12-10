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

	TestImageStage(script, config) {
		this.script = script;

		this.stageName = '集成测试';
		this.serverName = config.name;
		this.version = config.version;
	}

	def start() {
		def dockerCompose = new DockerComposePlugin();
		dockerCompose.apply(this.script);

		this.script.stage(this.stageName) {
		    this.script.node('aliyun345-test') {
		    	this.script.checkout this.script.scm

		        this.run();
		    }
		}
	}

	def testImage() {
		def name = this.serverName;
		def version = this.version;
		def cluster;

		try {
			cluster = this.script.dockerCompose.up("./src/integration_test/resources/docker-compose.yml", name, version);
			cluster.parseDockerfile();

			cluster.waitInside(":last") {
				commandLine = "gradle integration_test"
			}
		} catch(e) {
			this.script.echo e.message
		} finally {
			if (cluster != null) {
				cluster.down();
			}
		}
	}

	def imageRun(image, args, command) {
		def docker = this.script.docker;
		def id = image.id;

		docker.node {
            def container = docker.script.sh(script: "docker run ${args != '' ? ' ' + args : ''} ${id}${command != '' ? ' ' + command : ''}", returnStdout: true).trim()
            docker.script.dockerFingerprintRun containerId: container, toolName: docker.script.env.DOCKER_TOOL_NAME
        }
	}

	def run() {
		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		this.script.docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// Build base image
			this.testImage();
		}
	}

}