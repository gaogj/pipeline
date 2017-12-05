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

	SonarQubeStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
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

		this.script.node("aliyun327-test") {
	        this.script.echo "login to aliyun327-test"

	        this.script.checkout this.script.scm

            this.script.sh "./gradlew sonarqube   -Dsonar.host.url=https://sonar.kuick.cn   -Dsonar.login=74a5055a367c4a64bcb5d1a136690126a78a1510"

            this.script.echo "Please login and check your code :https://sonar.kuick.cn/projects"
//            this.script.sh "./gradlew build"
//            this.script.sh "/opt/sonar-scanner-3.0.3.778-linux/bin/sonar-scanner"

        }

	}

}