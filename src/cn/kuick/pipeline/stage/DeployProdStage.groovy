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

	def readProperties(propFile) {
		return this.script.readProperties([file: propFile])
	}

	def run() {
		def version = this.version;
		def deployNode = this.deployNode;

		// 部署正式环境
		this.script.node("aliyun345-test") {
	        this.script.echo "login to aliyun345-test"

	        this.script.checkout this.script.scm

	        def serverEnv = [];

	        this.script.dir("deploy-config") {
	            this.script.git([
	                url: "https://git.kuick.cn/deploys/deploy-config.git", 
	                branch: "master",
	                credentialsId: 'kuick_git_auto_deploy_pwd'
	            ]);

	            // application.properties
	            def properties = this.readProperties("prod/aliyuncs/application.properties");

	            for(def entry : properties) {
	            	def key = entry.key.trim().replace(".", "_").toUpperCase();
	            	def value = entry.value.trim();

	            	def item = "${key}=${value}";
	            	serverEnv.add(item)
	            }

	            // certs
	            def PGRDIR = this.script.pwd();

	           	serverEnv.add("DOCKER_TLS_VERIFY=1")
				serverEnv.add("DOCKER_HOST=tcp://master1g3.cs-cn-hangzhou.aliyun.com:16251")
				serverEnv.add("DOCKER_CERT_PATH=$PGRDIR/prod/aliyuncs/certs")
	        }

	        this.script.withEnv(serverEnv) {
	            this.script.sh "release/docker/prod/deploy.sh ${version}"
	        }

	        this.script.echo "deploy prod success!"
	    }
	}
}