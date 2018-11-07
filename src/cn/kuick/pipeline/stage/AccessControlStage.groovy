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
	def commitId;

	AccessControlStage(script, stageName, config) {
		this.script = script;
		this.config = config
		this.stageName = stageName;
		this.version = config.version;
		this.commitId = config.gitCommitId
	}

	def getUserId(){
		if (this.script.env.CHANGE_AUTHOR){
			return this.script.env.CHANGE_AUTHOR
		}
		return this.script.env.USER_ID
	}

	def getLatestCommit(){
		this.script.checkout this.script.scm
		def commitid = this.script.sh(returnStdout: true, script: "git rev-parse HEAD")
		return commitid
	}

	def start() {
		this.script.stage(this.stageName) {
			this.script.node('aliyun345-build') {
				this.run();
			}
		}
	}

	def run() {
		def userId = getUserId()
		def gitlabBranch = this.script.env.gitlabBranch
		def gitlabActionType = this.script.env.gitlabActionType

		// 权限白名单
		def whiteList = ['kuick-devops','kuick','Johny.Zheng','Administrator','Wu CongWen']

		// test
		// this.script.echo this.commitId
		// this.script.echo getLatestCommit()
		// if (this.commitId != getLatestCommit()){
		// 	this.script.input message: '当前上线版本非最新commitid,是否确认上线？'
		// }
		// if (whiteList.contains(userId)){
		// 	this.script.echo "userid: ${userId},权限检验成功,准备上线"
		// }else{
		// 	this.script.echo "userid: ${userId},账号权限校验失败，不允许上线"
		// 	this.script.sh 'exit 1' 
		// }

		// 非master和develop分支禁止上线 
		if (config.branch == 'develop'){
			this.script.sh 'echo true'
		}

		if (config.branch != 'master' && config.branch != 'develop' ){
			this.script.echo "当前分支: ${config.branch},非master和develop分支禁止上线"
			this.script.sh 'exit 1'
		}
		// 校验用户权限
		if (whiteList.contains(userId)){
			this.script.echo "userid: ${userId},权限检验成功,准备上线"
		}else{
			this.script.echo "userid: ${userId},账号权限校验失败，停止上线"
			this.script.sh 'exit 1' 
		}
		// 校验最新代码
		if (this.commitId != getLatestCommit()){
			this.script.input message: '当前上线版本非最新commitid,是否确认上线？'
		}
	}
}
