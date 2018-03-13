package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	冒烟测试 + 稳定标签
 */
class SmokeTestingStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def commitId;

	SmokeTestingStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.commitId = version[-6..-1];
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.script.node('aliyun345-test') {
		       	this.run();
		    }
		}
	}

	def run() {
		def stable_version = "stable";
		def version = this.version;
		def docker = this.script.docker;

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// 提供一个构建镜像稳定版本
			this.script.sh "git reset --hard ${commitId}"
			this.script.sh "./release/docker/build.sh ${stable_version}";
			this.script.sh "./release/docker/push.sh ${stable_version}";

		}
	}
}