package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	生成镜像
 */
class confirmMessgerStage implements Serializable {
	def script;

	def stageName;
	def version;
	def config;

	def timeout;
	def timeoutUnit;
	def tips;

	confirmMessgerStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.version = config.version;
		this.timeout = config.timeout
		this.timeoutUnit = config.timeoutUnit
		this.tips = config.tips;

	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run();
		}
	}

	def run() {
		def version = this.version;
		def docker = this.script.docker;

		// 超时时间默认24小时
		def timeout = 24;
		def timeoutUnit = 'HOURS';

		def tips = this.tips;

		if (!tips) {
			println("ERROR：tips不可为空")
			this.script.sh "exit 1"
		}

		if (this.timeout) {
			timeout = this.timeout
		}

		if (this.timeoutUnit) {
			timeoutUnit = this.timeoutUnit
		}

		// this.script.options {
		// 	timeout(time: timeout, unit: timeoutUnit)
		// }

		this.script.input message: tips
	}
}