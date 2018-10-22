/**
 *	单元测试
 */

package cn.kuick.pipeline.stage;

import java.io.Serializable;

class UnitTestStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def config;


	UnitTestStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.config = config

	}

	def start() {
		if (this.config.useUnitTest) {
			this.script.stage(this.stageName) {
			    this.script.node('aliyun345-build') {
			    	this.script.checkout this.script.scm

			       	this.run();
			    }
			}
		}else{
			this.script.stage(this.stageName) {
				this.script.echo "skip UnitTest"
			}
		}
	}

	def run() {
		def version = this.version;
		def docker = this.script.docker;

		docker.withRegistry('https://registry.kuick.cn', 'kuick_docker_registry_login') {
			// 覆盖率测试
			this.script.sh "pwd && ./shared/scripts/unitTest.sh";
			this.script.junit 'build/test-results/test/*.xml'
			// 检查单元测试结果,不匹配则标记job不稳定
			this.script.jacoco changeBuildStatus: true, maximumLineCoverage: '80',minimumLineCoverage: '70'
			// 如果pipeline被标记不稳定则终止构建
			if (this.script.currentBuild.result != 'SUCCESS') {
				this.script.echo "单元测试未通过" 
				this.script.sh "exit 1"
			}
		}
	}
}