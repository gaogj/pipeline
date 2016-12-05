package cn.kuick.pipeline.task;

/**
 *	构建镜像
 */
class BuildImage {
	String serverName;
	String version;

	BuildImage(scripts, serverName, version) {
		this.scripts = scripts;
		this.serverName = serverName;
		this.version = version;
	}

	def buildBase() {
		def docker = this.scripts.docker;
		def baseImage = docker.image("registry.kuick.cn/cc/${serverName}:base");

		if (baseImage == null) {
			baseImage = docker.build("registry.kuick.cn/cc/${serverName}:base", '.');
			baseImage.push();
		} else {
			baseImage.pull()
		}

		return baseImage;
	}

	def buildRelease() {
		def docker = this.scripts.docker;
		def releaseImage = docker.image("registry.kuick.cn/cc/${serverName}:${version}");

		if (releaseImage == null) {
			releaseImage = docker.build("registry.kuick.cn/cc/${serverName}:${version}", 'release/docker');
		}

		return releaseImage;
	}

	def execute() {
		def baseImage, releaseImage;

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		def docker = this.scripts.docker;
		docker.withRegistry('https://registry.kuick.cn', 'docker-registry-login') {
			// Build base image
			baseImage = this.buildBase();

			// Build release image
			releaseImage = this.buildRelease();
		}

		return releaseImage;
	}
}