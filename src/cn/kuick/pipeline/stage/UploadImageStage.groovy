package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	上传镜像
 */
class UploadImageStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	UploadImageStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.script.node('aliyun345-test') {
		       	this.run();
		    }
		}
	}

	def run() {
		def version = this.version;

		// 上传镜像
		this.script.sh "./release/docker/push.sh ${version}";
	}
}