package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	自动更新changelog
 */
class PostDeployAutoChangeLogStage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def commitId;
	def branch;
	def changeLog;


	PostDeployAutoChangeLogStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.commitId = version[-6..-1];
		this.branch = config.branch;
		this.changeLog = config.changeLog;

	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run345();
		}
	}

	def run345() {
		def version = this.version;


		this.script.node('aliyun345-test') {
			this.script.echo "login to aliyun345-test"

	    	this.script.checkout this.script.scm

	        this.script.sh "git fetch"

	        this.script.sh "git checkout -B ${branch} --track origin/${branch} "

            // Update changelog
            if (changeLog == "Y") {
                this.script.sh "npm run chagelog"

                this.script.sh "git add ."

                this.script.sh "git commit -m 'Update Changelog commit ${commitId}'"

	            this.script.sh "git push origin ${branch}"

                this.script.echo "Success Update Changelog on ${branch}!!!"

                }
            else {
                this.script.echo "Skipped Update Changelog!!!"
                }


	    }

}

}