package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	部署测试2环境
 */
class DeployTest2Stage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def deployNode;
	def commitId;

	DeployTest2Stage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;

		this.serverName = config.name;
		this.version = config.version;
		this.deployNode = config.deployNode;
		this.commitId = version[-6..-1];
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run();
		}
	}

	def run() {
		def version = this.version;
		def deployNode = this.deployNode;
		def docker = this.script.docker;
		def serverName = this.serverName;

		// 部署测试2环境
		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {

            this.script.node("${deployNode}-test2") {
                this.script.echo "login to ${deployNode}-test2"

                this.script.checkout this.script.scm
    
                this.script.sh "git reset --hard ${commitId}"

                if (serverName == "kafka") {

                this.script.sh "release/docker/${deployNode}-test2/deploy.sh ${version}"

                this.script.echo "deploy test2 success!"

                }

                else {
                    this.script.sh "release/docker/test2/deploy.sh ${version}"

                    this.script.echo "deploy test2 success!"
                }
	        }
	    }
	}
}