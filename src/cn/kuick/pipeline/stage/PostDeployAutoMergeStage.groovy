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
	def tmpbranch;
	def branch;

	PostDeployAutoMergeStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.commitId = version[-6..-1];
		this.tmpbranch = config.branch;
		this.branch = tmpbranch[7..-1];
	    this.script.echo "branch is ${branch}"

	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run345();
		}
	}

	def run345() {
		def version = this.version;

		this.script.node('aliyun345-build') {
			this.script.echo "login to aliyun345-build"

	    	this.script.checkout this.script.scm

	        this.script.sh "git fetch"

	        this.script.echo "${branch}"

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

            // 确保shared 在并行构建存在
			this.script.sh "./release/docker/predeploy.sh";

		    // 清理已经合并的分支
		    this.script.sh "./shared/scripts/clean_merged_branch.sh";

			// this.script.sh "./release/docker/clean_merged_branch.sh";

		    this.script.echo "Clean branch which merged to develop success!"

	    }

}

}