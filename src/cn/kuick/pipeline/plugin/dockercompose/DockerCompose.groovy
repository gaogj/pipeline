package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;
import java.io.File;

import hudson.FilePath;
import hudson.slaves.WorkspaceList;

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
    	this.script.echo "compose uuid:" + uuid;

    	def workspace = new File("./build/docker-compose/" + uuid);
    	this.script.echo "compose workspace:" + workspace.getPath();

    	def newDockerfile = new File(workspace, file.getName());
    	this.script.echo "compose newDockerfile:" + newDockerfile.getPath();

    	this.script.sh "whoami";
    	this.script.sh "ls -l .";
    	this.script.sh "mkdir -p ${workspace.getPath()}";

    	this.script.dir(workspace.getPath()) {
			new FilePath(newDockerfile).copyFrom(new FilePath(file));

	    	def newDockerfilePath = newDockerfile.getPath();

	    	this.script.withEnv(["TAG=${version}", "SERVER_NAME=${name}"]) {
		        this.script.sh "cd ${workspace} && docker-compose up -d"
		        return new Cluster(this, uuid, newDockerfilePath, name, version)
		    }
    	}
    }
}