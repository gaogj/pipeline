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
		//def sonarqube = this.script.sonarqube;
	    def sonarqubeScannerHome = 'SonarQube_Scanner';

		this.script.node("aliyun327-test") {
	        this.script.echo "login to aliyun327-test"

	        this.script.checkout this.script.scm

        //withSonarQubeEnv('SonarQube') {
            //this.script.sh "${sonarqubeScannerHome}/bin/sonar-scanner"
            this.script.sh "./gradlew --info sonarqube"
            //}

        }

	}

}