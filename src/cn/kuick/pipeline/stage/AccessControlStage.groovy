package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	确认消息
 */
class AccessControlStage implements Serializable {
	def script;

	def stageName;
	def version;
	def config;

	AccessControlStage(script, stageName, config) {
		this.script = script;
		this.stageName = stageName;
		this.version = config.version;
	}

	def start() {
		this.script.stage(this.stageName) {
			this.run();
		}
	}

	def run() {
		def gitlabBranch = this.script.env.gitlabBranch
		def gitlabActionType = this.script.env.gitlabActionType

		def buildUserName = this.script.env.BUILD_USER_ID

		// 权限白名单
		def whiteList = ['kuick-devops','kuick']

		// 非master和develop分支禁止上线 并校验jenkins用户权限
		println(buildUserName)
		if (gitlabBranch == 'master' || gitlabBranch == 'develop' ){
			if (whiteList.contains(buildUserName)){
				this.script.echo '权限检验成功,准备上线'
			}else{
				this.script.echo '账号权限校验失败，停止上线'
				this.script.sh 'exit 1' 
			}
		}else{
			this.script.echo '非master和develop分支禁止上线'
			this.script.sh 'exit 1' 
		}

	}
}
