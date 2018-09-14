package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	生成镜像
 */
class BuildImageStage implements Serializable {
	def script;

	def stageName;

	def timeNum;
	// def timeUnit;

	def tips;

	BuildImageStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.timeout = config.timeout;
		// this.timeoutUnit = config.timeoutUnit;
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
		def timeout = this.timeout
		def timeoutUnit = 'MINUTES'
		def tips = this.tips

		if (config.timeoutUnit) {
			timeoutUnit = config.timeoutUnit
		}

		this.script.options {
			timeout(time: timeout, unit: timeoutUnit)
		}

		this.script.input message: tips
	}
}