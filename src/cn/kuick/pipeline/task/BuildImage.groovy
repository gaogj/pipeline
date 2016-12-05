package cn.kuick.pipeline.task;

import java.io.Serializable;

/**
 *	构建镜像
 */
class BuildImage implements Serializable {
	def script;
	def serverName;
	def version;

	BuildImage(script, serverName, version) {
		this.script = script;
		this.serverName = serverName;
		this.version = version;

		script.echo "serverName: ${serverName}, version: ${version}"
	}

	def existImage(baseImage) {
		try {
			baseImage.inside {
				this.script.sh "echo Hello"
			}

			return true;
		} catch(e) {
			return false;
		}
	}

	def buildBase() {
		def name = this.serverName;
		def docker = this.script.docker;
		def baseImage = docker.image("registry.kuick.cn/cc/${name}:base");

		if (!existImage(baseImage)) {
			baseImage = docker.build("registry.kuick.cn/cc/${name}:base", '.');
			baseImage.push();
		} else {
			baseImage.pull()
		}

		return baseImage;
	}

	def buildRelease() {
		def name = this.serverName;
		def version = this.version;

		def docker = this.script.docker;
		def releaseImage = docker.image("registry.kuick.cn/cc/${name}:${version}");

		if (releaseImage == null) {
			releaseImage = docker.build("registry.kuick.cn/cc/${name}:${version}", 'release/docker');
		}

		return releaseImage;
	}

	def execute() {
		def baseImage, releaseImage;

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		def docker = this.script.docker;

		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// Build base image
			baseImage = this.buildBase();

			// Build release image
			releaseImage = this.buildRelease();
		}

		return releaseImage;
	}
}