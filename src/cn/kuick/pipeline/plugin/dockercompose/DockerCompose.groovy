package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;
import java.io.File;

import hudson.FilePath;

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

    	def uuid = java.util.UUID.randomUUID().toString().replaceAll("-", "");
    	def workspace = new File(file.getParent(), uuid);
    	def newDockerfile = new File(workspace, file.getName());

    	this.script.dir(workspace) {
			new FilePath(newDockerfile).copyFrom(new FilePath(file));

	    	def newDockerfilePath = newDockerfile.getPath();

	    	this.script.withEnv(["TAG=${version}", "SERVER_NAME=${name}"]) {
		        this.script.sh "cd ${workspace} && docker-compose up -d"
		        return new Cluster(this, uuid, newDockerfilePath, name, version)
		    }
    	}
    }
}