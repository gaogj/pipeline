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
		    this.run();
		}
	}

	def readProperties(propFile) {
		return this.script.readProperties([file: propFile])
	}

	def run() {
		def lastVersion = this.lastVersion;
		def deployNode = this.deployNode;
		def docker = this.script.docker;
		def USER_ID = this.getId();

		// 回滚服务
		this.script.node("aliyun345-build") {
	        this.script.echo "login to aliyun345-build"

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
				serverEnv.add("DOCKER_CERT_PATH=$PGRDIR/prod/aliyuncs/certs")
	        }

	        //
		    this.script.withEnv(serverEnv) {

                // Fix: docker部署时变量代码的版本与镜像版本不一致的问题
	        	this.script.sh "git reset --hard ${commitId}"

	        	if (USER_ID == "kuick" || USER_ID == "kuick-devops") {
					// 回滚prod
					this.script.sh "release/docker/prod/deploy.sh ${lastVersion}"
					this.script.sh "echo 'rollback' >> backupVersion.txt"
				} else {
					this.script.echo "You have no authority to build production!!!"

					this.script.sh "echo 'You have no authority to build production!!!'; exit 1"
					}

	        }

	        this.script.echo "rollback prod success!"
	    }
	}
}
