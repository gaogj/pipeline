package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	自动合并分支
 */
class PostDeployAutoMergeStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def commitId;
	def gitlabBranch;

	PostDeployAutoMergeStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.commitId = version[-6..-1];
		this.gitlabBranch = config.gitlabBranch;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run345();
		}
	}

	def run345() {
		def version = this.version;
		def branch = this.gitlabBranch;

		this.script.node('aliyun345-test') {
			this.script.echo "login to aliyun345-test"

	    	this.script.checkout this.script.scm

	        this.script.sh "git fetch"

            // 获取当前分支自动合并分支

            if (branch == "master") {

                this.script.sh "git checkout -B master --track origin/master"

	        	this.script.sh "git reset --hard ${commitId}"

                this.script.sh "git checkout -B develop --track origin/develop"

                this.script.sh "git merge master"

                this.script.sh "git push origin develop"

                this.script.echo "Merged branch master to develop success!"
                }

			else if (branch == "develop") {

                this.script.sh "git checkout -B develop --track origin/develop"

	        	this.script.sh "git reset --hard ${commitId}"

                this.script.sh "git checkout -B master --track origin/master"

                this.script.sh "git merge develop"

                this.script.sh "git push origin master"

                this.script.echo "Merged branch develop to master success!"
                }

			else {

                this.script.echo "Please make sure your branch is develop or master!"

		        }
	    }

}

}