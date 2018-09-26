package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	确认消息
 */
class SendEmailStage implements Serializable {
	def config;
	def body
	def subject
	def toMail

	SendEmailStage(body, subject, toMail) {
		this.config = config
		this.body = body
		this.subject = subject
		this.toMail = toMail
	}

	def start() {
        this.script.emailext 
        body: this.body, 
        recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']], 
        subject: this.subject, 
        to: this.toMail
	}
}
