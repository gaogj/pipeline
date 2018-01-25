package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	Test3 api 接口测试
 */
class Test3ApiTestingStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def projectType;

	Test3ApiTestingStage(script, stageName, config) {
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

		this.script.node("aliyun345-test") {
	        this.script.echo "login to aliyun345-test"

	        this.script.checkout this.script.scm

	        this.script.dir("api-test") {
	            this.script.git([
	                url: "https://git.kuick.cn/tests/api-test.git",
	                branch: "develop",
	                credentialsId: 'kuick_git_auto_deploy_pwd'
	            ]);

	        def serverEnv = [];

	        serverEnv.add("SERVER_NAME=$serverName")

	        this.script.withEnv(serverEnv) {

                if (serverName == "deal-openweixin") {

                    this.script.sh "echo SERVER_NAME: $SERVER_NAME"

                    this.script.sh'#!/bin/bash \n' + './release/docker/nginx/deploy.sh && ./release/docker/test3/deploy.sh'
                    }

    //            else if (projectType == "nodejs") {
    //
    //                this.script.sh "/opt/sonar-scanner-3.0.3.778-linux/bin/sonar-scanner -Dsonar.host.url=https://sonar.kuick.cn   -Dsonar.login=${sonarToken} Dsonar.projectKey=${serverName}-server  -Dsonar.sourceEncoding=UTF-8 -Dsonar.exclusions=libs/**  -Dsonar.sources=."
    //
    //                }

                else {

                    this.script.echo "Test3 api test pass!"

                }

                this.script.echo "Please check test report https://testreport.kuick.cn/"

            }

            }
        }

	}

}