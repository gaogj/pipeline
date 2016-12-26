package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	部署测试3环境
 */
class DeployTest3Stage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def deployNode;

	DeployTest3Stage(script, stageName, config) {
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

		// 部署测试3环境
		this.script.node("aliyun345-test") {
	        this.script.echo "login to aliyun345-test"

	        this.script.checkout this.script.scm

	        this.script.dir("deploy-config") {
	            this.script.git([
	                url: "https://git.kuick.cn/deploys/deploy-config.git", 
	                branch: "master",
	                credentialsId: 'kuick_git_auto_deploy_pwd'
	            ]);

	            this.script.config = this.script.readYaml("test3/aliyuncs/application.yml");

	            def mysqlCfg = this.script.config.get('mysql');
	            this.script.echo "mysqlCfg:" + mysqlCfg.toString()
	        }

	        def serverEnv = this.script.config;
	        
	        this.script.withEnv(serverEnv) {
	            this.script.sh "release/docker/test3/deploy.sh ${version}"
	        }

	        this.script.echo "deploy test3 success!"
	    }
	}
}