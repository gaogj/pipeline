package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	回滚服务到上一个镜像版本
 */
class RollbackStage implements Serializable {
	static String DEPLOY_TOKEN = "cc123456v5";
	
	def script;

	def stageName;
	def serverName;
	def lastVersion;
	def deployNode;
	def commitId;

	RollbackStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.lastVersion = config.lastVersion;
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
		def lastVersion = this.lastVersion;
		def deployNode = this.deployNode;

		// 回滚服务
		this.script.node("aliyun345-build") {
	        this.script.echo "login to aliyun345-build"

	        this.script.checkout this.script.scm

	        def serverEnv = [];

	        this.script.dir("deploy-config") {
	        	this.script.git([
	                url: "https://git.kuick.cn/deploys/deploy-config.git",
	                branch: "master",
	                credentialsId: 'kuick_git_auto_deploy_pwd'
	            ]);

	            // application.properties
	            def properties = this.readProperties("prod/aliyuncsvpc/application.properties");

	            for(def entry : properties) {
	            	def key = entry.key.trim().replace(".", "_").toUpperCase();
	            	def value = entry.value.trim();

	            	def item = "${key}=${value}";
	            	serverEnv.add(item)
	            }

	            // certs
	            def PGRDIR = this.script.pwd();

	           	serverEnv.add("DOCKER_TLS_VERIFY=1")
				serverEnv.add("DOCKER_HOST=tcp://master3g9.cs-cn-hangzhou.aliyun.com:20103")
				serverEnv.add("DOCKER_CERT_PATH=$PGRDIR/prod/aliyuncs/certs")
	        }

	        this.script.withEnv(serverEnv) {

	        	// 回滚prod
	            this.script.sh "release/docker/test/deploy.sh ${lastVersion}"
	            this.script.sh "echo 'rollback' >> backupVersion.txt"

	        }

	        this.script.echo "rollback prod success!"
	    }
	}
}
