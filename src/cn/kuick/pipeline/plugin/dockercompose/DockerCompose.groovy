package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;
import java.io.File;

import hudson.FilePath;
import hudson.Util;
import hudson.slaves.WorkspaceList;

/**
 *	Docker Compose Cluster
 */
class DockerCompose implements Serializable {

	def script;

	public DockerCompose(script) {
		this.script = script;
    }

    def up(dockerfile, name, version) {
    	println "compose dockerfile:" + dockerfile;
    	println "compose name:" + name;
    	println "compose version:" + version;

    	def file = new File(dockerfile);
    	def dir = file.getParent();
    	def uuid = java.util.UUID.randomUUID().toString();
    	def workspace = new File(dir, uuid);

    	this.script.sh "cp ${dockerfile} ${workspace}"
    	def newDockerfile = new File(workspace, "Dockerfile").getPath();

    	this.script.withEnv(["TAG=${version}", "SERVER_NAME=${name}"]) {
	        this.script.sh "cd ${workspace} && docker-compose up -d"
	        return new Cluster(this, uuid, newDockerfile)
	    }
    }
}