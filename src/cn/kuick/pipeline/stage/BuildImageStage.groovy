package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	生成镜像
 */
class BuildImageStage implements Serializable {
	def script;
	def config

	def stageName;
	def serverName;
	def version;

	BuildImageStage(script, stageName, config) {
		this.script = script;
		this.config = config;
		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
	}
	
	def start() {
		this.script.stage(this.stageName) {
		    this.script.node('aliyun345-build') {
		    	this.script.checkout this.script.scm
		    	this.config.gitCommitId = sh('git rev-parse HEAD').execute().text

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
			// 构建镜像
			this.script.sh "./release/docker/build.sh ${version}";
		}
	}
}