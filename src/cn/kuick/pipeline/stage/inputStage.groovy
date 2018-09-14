package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	确认提示信息
 */
class BuildImageStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;

	BuildImageStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
	}

	def start() {
		this.script.stage(this.stageName) {
			this.run();
		    }
		}

	def run() {
		def stageName = this.stageName;
		def version = this.version;

		switch(stageName) {

			case "确认部署测试2":
				this.script.options {
					timeout(time: 1, unit: 'MINUTES')
				}
				this.script.input message: "该服务是否可以部署测试2?"
				break

			case "确认部署测试3":
				this.script.options {
					timeout(time: 1, unit: 'MINUTES')
				}
				this.script.input message: "该服务是否可以部署测试3?"
				break

			case "确认上线":
				this.script.options {
					timeout(time: 1, unit: 'MINUTES')
				}
				this.script.input message: "该服务是否可以上线?"
				break

			case "确认合并分支":
				this.script.options {
					timeout(time: 1, unit: 'MINUTES')
				}
				this.script.input message: "是否合并develop分支到master分支?"
				break

			case "QA测试":
				this.script.options {
					timeout(time: 1, unit: 'MINUTES')
				}
				this.script.input message: "QA测试是否通过?"
				break

			default:
				this.script.echo "ERROR: stageName is not correct"
				this.script.sh "exit 1"
		}

	}
}
