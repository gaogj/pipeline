package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	部署测试2环境
 */
class DeployTest2Stage implements Serializable {
	def script;

	def stageName;
	def version;
	def deployNode;
	def commitId;

	DeployTest2Stage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.version = config.version;
		this.deployNode = config.deployT2Node;
		this.commitId = version[-6..-1];
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run();
		}
	}

	def run() {
		def version = this.version;
		def deployNode = this.deployNode
		def docker = this.script.docker

		// 部署测试2环境
		println(deployNode.getClass().name)
		println(deployNode.getClass())
		println(deployNode)
		if (deployNode.getClass().name == java.util.ArrayList ) {
			for (node in deployNode) {
				this.script.node("${node}-test2") {
					docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
						this.script.echo "login to ${node}-test2"
						this.script.checkout this.script.scm
    					this.script.sh "git reset --hard ${commitId}"

    					this.script.sh "release/docker/${node}-test2/deploy.sh ${version}"
						this.script.echo "deploy test2 in ${node} success!"
					}
				}
			}
		}

		else if (deployNode.getClass().name == java.lang.String ) {
			this.script.node("${deployNode}-test2") {
				docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
					this.script.sh "release/docker/test2/deploy.sh ${version}"
					this.script.echo "deploy test2 success!"
				}
			}
		}
		else {
			this.script.echo "error: deployTest2Node parameter not found"
		}
	}
}