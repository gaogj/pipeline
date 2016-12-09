package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;
import java.io.File;

/**
 *	Docker Compose
 */
class DockerCompose implements Serializable {

	def script;

	public DockerCompose(script) {
		this.script = script;
    }

    def up(dockerfile, name, version) {
    	this.script.echo "compose dockerfile:" + dockerfile;
    	this.script.echo "compose name:" + name;
    	this.script.echo "compose version:" + version;

    	def file = new File(dockerfile);
    	def dir = file.getParent();
    	def uuid = java.util.UUID.randomUUID().toString();
    	def workspace = new File(dir, uuid);

    	this.script.sh "mkdir -p ${workspace}"
    	this.script.sh "cp ${dockerfile} ${workspace}"
    	def newDockerfile = new File(workspace, file.getName()).getPath();

    	this.script.withEnv(["TAG=${version}", "SERVER_NAME=${name}"]) {
	        this.script.sh "cd ${workspace} && docker-compose up -d"
	        return new Cluster(this, uuid, newDockerfile, name, version)
	    }
    }
}