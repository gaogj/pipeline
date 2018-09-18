package cn.kuick.pipeline.stage;

import java.io.Serializable;

/**
 *	备份服务的版本号
 */
class BackupVersionStage implements Serializable {	
	def script;

	def stageName;
	def serverName;
	def lastVersion;


	BackupVersionStage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.lastVersion = config.lastVersion;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run345();
		}
	}

	def run345() {

		this.script.node('aliyun327-test') {
			this.script.echo "login to aliyun327-test"
			this.script.checkout this.script.scm

		    // 备份
		    this.script.sh "./shared/scripts/backupVersion.sh ${lastVersion} ${serverName}";
		    this.script.echo "Backup version ${lastVersion} success!"
	    }
	}

}

