package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	确认消息
 */
class ConfirmMessgerStage implements Serializable {
	def script;

	def stageName;
	def version;
	def config;

	def timeout;
	def timeoutUnit;
	def tips;

	ConfirmMessgerStage(script, stageName, config) {
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

		def tips = this.tips;

		assert tips : "ERROR：tips不可为空";

		// 超时时间默认24小时
		def timeout = 24;
		def timeoutUnit = 'HOURS';

		if (this.timeout) {
			timeout = this.timeout
		}

		if (this.timeoutUnit) {
			timeoutUnit = this.timeoutUnit
		}

		this.script.timeout(time: timeout, unit: timeoutUnit){
			this.script.input message: tips
		}
	}
}