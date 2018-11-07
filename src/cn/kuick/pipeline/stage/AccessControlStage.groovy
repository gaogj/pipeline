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
		// if (this.script.env.CHANGE_AUTHOR){
			// return this.script.env.CHANGE_AUTHOR
		// }
		return this.script.env.BUILD_USER_ID
	}

	def getLatestCommit(){
		this.script.checkout this.script.scm
		def commitid = this.script.sh 'git rev-parse HEAD'
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
		if (this.commitId != getLatestCommit()){
			this.script.input message: '当前上线版本非最新commitid,是否确认上线？'
		}
		if (whiteList.contains(userId)){
			this.script.echo "userid: ${userId},权限检验成功,准备上线"
		}else{
			this.script.echo "userid: ${userId},账号权限校验失败，不允许上线"
			this.script.sh 'exit 1' 
		}

		// 非master和develop分支禁止上线 并校验jenkins用户权限
		if (gitlabBranch == 'master' || gitlabBranch == 'develop' ){
			if (this.commitId != getLatestCommit()){
				this.script.input message: '当前上线版本非最新commitid,是否确认上线？'
			}
			if (whiteList.contains(userId)){
				this.script.echo "userid: ${userId},权限检验成功,准备上线"
			}else{
				this.script.echo "userid: ${userId},账号权限校验失败，停止上线"
				this.script.sh 'exit 1' 
			}
		}else{
			this.script.echo '非master和develop分支禁止上线'
			this.script.sh 'exit 1' 
		}
	}
}
