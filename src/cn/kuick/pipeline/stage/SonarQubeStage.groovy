package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	SonarQube analysis
 */
class SonarQubeStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def projectType;

	SonarQubeStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.projectType = config.type;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.script.node('aliyun345-test') {
		    	this.script.checkout this.script.scm

		       	this.run();
		    }
		}
	}

	def run() {
		def version = this.version;
		def docker = this.script.docker;
		def serverName = this.serverName;
		def projectType = this.projectType;
		def sonarToken = "74a5055a367c4a64bcb5d1a136690126a78a1510"

		this.script.node("aliyun327-test") {
	        this.script.echo "login to aliyun327-test"

	        this.script.checkout this.script.scm


//	        if (projectType == "java") {
//
//                this.script.sh "./gradlew sonarqube   -Dsonar.host.url=https://sonar.kuick.cn   -Dsonar.login=${sonarToken}"
//                }
//
//            else if (projectType == "nodejs") {
//
//                this.script.sh "/opt/sonar-scanner-3.0.3.778-linux/bin/sonar-scanner -Dsonar.host.url=https://sonar.kuick.cn   -Dsonar.login=${sonarToken} -Dsonar.projectKey=${serverName}-server  -Dsonar.sourceEncoding=UTF-8 -Dsonar.exclusions=libs/**  -Dsonar.sources=."
//
//                }
//
//            else {
//
//                this.script.echo "The project unsupport SonarQube!"
//
//            }

            this.script.echo "Please login and check your code :https://sonar.kuick.cn/projects"

        }

	}

}