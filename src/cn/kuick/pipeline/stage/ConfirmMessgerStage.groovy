package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	确认消息
 */
class ConfirmMessgerStage implements Serializable {
	def script
	def config

	def timeout
	def timeoutUnit

	def stageName
	def tips;

	ConfirmMessgerStage(script, stageName, tips, config) {
		this.config = config
		this.script = script

		this.stageName = stageName;
		this.tips = config.tips;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run();
		}
	}

	def run() {
		if (this.config.timeout == '' || this.config.timeout == null) {
        	this.timeout = 24
        }else{
        	this.timeout = this.config.timeout
        }

        if (this.config.timeoutUnit == '' ||this.config.timeoutUnit == null){
            this.timeoutUnit = 'HOURS'
        }else{
        	this.timeoutUnit = this.config.timeoutUnit
        }
        
		this.script.timeout(time: this.timeout, unit: this.timeoutUnit){
			this.script.input message: this.tips
		}
	}
}