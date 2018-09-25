package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	确认消息
 */
class SendEmailStage implements Serializable {
	def script;
	def stageName;
	def config;

	SendEmailStage(script, stageName, config) {
		this.script = script;
		this.stageName = stageName;
		this.config = config
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run();
		}
	}

	def run() {
        mail([
            bcc: '',
            body: this.config.body,
            cc: 'devops@kuick.cn',
            from: 'jenkins2@kuick.cn',
            replyTo: '',
            subject: this.config.subject,
            to: this.config.toMail
        ]);

	}
}