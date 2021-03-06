package cn.kuick.pipeline.stage;

import java.io.Serializable;
import cn.kuick.pipeline.plugin.dockercompose.DockerComposePlugin;

/**
 *	集成镜像
 */
class IntegrationTestStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def path = "/resources/docker-compose.yml";

	IntegrationTestStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;

		if(config.path == null){
			this.path = "./src/integration_test" + this.path;
		} else {
			this.path = config.path + this.path;
		}
	}

	def start() {
		def dockerCompose = new DockerComposePlugin();
		dockerCompose.apply(this.script);

		this.script.stage(this.stageName) {
		    this.script.node('aliyun345-build') {
		    	this.script.checkout this.script.scm

		        this.run();
		    }
		}
	}

	def testImage() {
		def name = this.serverName;
		def version = this.version;
		def path = this.path;
		def cluster;

		try {
			def testerImage = this.script.docker.image("registry.kuick.cn/cc/${name}-tester:${version}");

			cluster = this.script.dockerCompose.up(path, name, version);
			cluster.parseDockerfile();

			this.script.echo "docker-compose up ok!";

			cluster.waitReady("server") { container ->
			   	container.sh "echo server ready!"
			}

			cluster.inside("tester") { container ->
				container.sh "env"
			   	container.sh "gradle integration_test --info --stacktrace"
			}
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