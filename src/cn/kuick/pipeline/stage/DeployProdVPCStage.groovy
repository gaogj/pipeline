package cn.kuick.pipeline.stage;

import java.io.Serializable;


/**
 *	部署正式环境 + 自动打tag
 */
class DeployProdVPCStage implements Serializable {
	static String DEPLOY_TOKEN = "cc123456v5";

	def script;

	def stageName;
	def serverName;
	def version;
	def deployNode;
	def commitId;


	DeployProdVPCStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;

		this.serverName = config.name;
		this.version = config.version;
		this.deployNode = config.deployNode;
		this.commitId = version[-6..-1];
	}


	@NonCPS
	def getBuildUser() {
		def cause = this.script.currentBuild.rawBuild.getCause(Cause.UserIdCause);

		if (cause != null) {
			return cause.getUserId()
		}

		return "gitlab"
	}

	def getId() {
		this.script.node{
			this.script.wrap([$class: 'BuildUser']) {
				def userId = this.script.env.BUILD_USER_ID;

					if (userId != null) {
						return  userId
					}

					return "kuick"
			}
		}
	}

	def start() {
		this.script.stage(this.stageName) {
			/*
			def token = this.script.input message: '请输入部署正式服务器授权码？', parameters: [
				[$class: 'PasswordParameterDefinition', defaultValue: '', description: '部署正式服务器授权码', name: '授权码']
			];

			if (DEPLOY_TOKEN.equals(token)) {
				this.script.echo "TOKEN:${token}"
		    	this.run();
		    } else {
				this.script.echo "TOKEN:${token}"
		    	throw new RuntimeException("部署正式服务器授权码不正确！");
		    }
		    */

		    this.run();
		}
	}

	def readProperties(propFile) {
		return this.script.readProperties([file: propFile])
	}

	def run() {
		def version = this.version;
		def deployNode = this.deployNode;
		def docker = this.script.docker;
		//def USER_ID = this.getBuildUser();
		def USER_ID = this.getId();


				// 部署正式环境
		this.script.node("aliyun345-test") {
	        this.script.echo "login to aliyun345-test"

			this.script.echo "'USER_ID':${USER_ID}"

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
				serverEnv.add("DOCKER_CERT_PATH=$PGRDIR/prod/aliyuncsvpc/certs")
	        }

	        this.script.withEnv(serverEnv) {

                // Fix: docker部署时变量代码的版本与镜像版本不一致的问题
	        	this.script.sh "git reset --hard ${commitId}"

	        	if (USER_ID == "kuick") {

					// 部署prod
					this.script.sh "release/docker/prod/deploy.sh ${version}"

					// 自动打tag
					this.script.sh "git tag -f v${version} ${commitId}"

					this.script.sh " git push origin v${version}"
					}
					else {
						this.script.echo "You have no authority to build production!!!"
						this.script.sh "echo 'You have no authority to build production!!!'; exit 1"
					}

	        }

	        this.script.echo "deploy prod success!"
	    }
	}
}