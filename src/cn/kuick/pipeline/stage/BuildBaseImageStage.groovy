package cn.kuick.pipeline.stage;

import java.io.Serializable;
import java.io.File;

/**
 *	生成基础镜像
 */
class BuildBaseImageStage implements Serializable {
	def script;
	def config;

	def stageName;
	def serverName;
	def version;
	def projectType;
	def clairUrl;
	def buildNodeIP;

	BuildBaseImageStage(script, stageName, config) {
		this.script = script;
		this.config = config

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.projectType = config.projectType;
		this.buildNodeIP = config.buildNodeIP;
		this.clairUrl = config.clairUrl;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.script.node('aliyun345-build') {
		    	this.script.checkout this.script.scm

		        this.run();
		    }
		}
	}

	def buildBase() {
		def name = this.serverName;
		def docker = this.script.docker;
        // 存储在本地是 cc/deal-rabbitpre-server-server 上传到 registry.kuick.cn server 会自动加上 https://registry.kuick.cn
		def baseImage = docker.build("cc/${name}-server:base", '.');
		baseImage.push();

		return baseImage;
	}

	def buildTestBase() {
		def name = this.serverName;
		def docker = this.script.docker;

		def baseImage = docker.build("cc/${name}-tester:base", '-f ./release/docker/testBase.docker .');
		baseImage.push();

		return baseImage;
	}


	def analysisImage() {

		def name = this.serverName
		def clairUrl = this.clairUrl
		def buildNodeIP = this.buildNodeIP
		def imageName = "registry.kuick.cn/cc/${name}-server:base"
	//	def reportPath = "./clair-report.json"
		def reportPath = 'clair-report.json'
		// def reportPath = "./imageScanner-Report-${name}-server.json"

		def buildId = this.script.env.BUILD_ID;
		def toMail = this.script.env.gitlabUserEmail;

		def parameter = "--ip='10.0.12.233' --clair='http://10.0.9.195:6060' --report=${reportPath} --threshold='Defcon1' ${imageName}"

		if (clairUrl && buildNodeIP) {
			parameter = "--ip='${buildNodeIP}' --clair='${clairUrl}' --report=${reportPath} --threshold='Defcon1' ${imageName}"
		}

		try {

			this.script.sh "clair-scanner ${parameter}"

			this.script.echo "start send success mail!"
			this.script.emailext body: "附件为镜像漏洞扫描结果 At ${imageName}",attachmentsPattern: reportPath, recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']], subject: "${name}-server 镜像扫描结果 At buildId(#${buildId})",to: 'devops@kuick.cn'
			// cc: 'devops@kuick.cn'

			this.script.echo "success mail send ok!"

		} catch(e){

			this.script.echo "start send fail mail!"

			this.script.emailext body: "${imageName} 镜像漏洞扫描失败", recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']], subject: "${name}-server 镜像漏洞扫描失败 At buildId(#${buildId})", to: toMail,cc: 'devops@kuick.cn'
	    
			this.script.echo "fail mail send ok!"

			throw e;
		}
	}

	def run() {
		def docker = this.script.docker;

		// We are pushing to a private secure Docker registry in this demo.
		// 'docker-registry-login' is the username/password credentials ID as defined in Jenkins Credentials.
		// This is used to authenticate the Docker client to the registry.
		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// pull submodule
			this.script.sh "git submodule update --init --recursive"

			// Build Base Image
			this.buildBase();
			this.analysisImage();


			// Build TestBase image
			if (this.config.projectType == "java") {
			    this.buildTestBase()
			    }
			else {
                this.script.echo "Skiped Build TestBase image!!!"
                }
		}
	}
}