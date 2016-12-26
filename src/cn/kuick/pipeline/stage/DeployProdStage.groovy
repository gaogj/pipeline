package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	部署正式环境
 */
class DeployProdStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def deployNode;

	DeployProdStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;

		this.serverName = config.name;
		this.version = config.version;
		this.deployNode = config.deployNode;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run();
		}
	}

	def run() {
		def version = this.version;
		def deployNode = this.deployNode;

		// 部署正式环境
		this.script.node("aliyun345-test") {
	        this.script.echo "login to aliyun345-test"

	        this.script.checkout this.script.scm

	        this.script.dir("deploy-config") {
	            this.script.git([
	                url: "https://git.kuick.cn/deploys/deploy-config.git", 
	                branch: "master",
	                credentialsId: 'kuick_git_auto_deploy_pwd'
	            ]);

	            this.script.config = this.script.readYaml("prod/aliyuncs/application.yml");
	        }

	        def serverEnv = this.script.config;
	        
	        this.script.withEnv(serverEnv) {
	            this.script.sh "release/docker/prod/deploy.sh ${version}"
	        }

	        this.script.echo "deploy prod success!"
	    }
	}
}