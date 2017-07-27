package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	上传镜像
 */
class UploadImageStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	UploadImageStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.script.node('aliyun345-test') {
		       	this.run();
		    }
		}
	}

	def run() {
		def version = this.version;
		def docker = this.script.docker;

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// 上传镜像
			this.script.sh "./release/docker/push.sh ${version}";
		}
	}
}