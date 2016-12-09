package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;
import java.io.File;

import org.jenkinsci.plugins.workflow.cps.CpsScript;

/**
 *	Docker Compose Cluster
 */
class DockerCompose implements Serializable {

	private CpsScript script;

	public DockerCompose(CpsScript script) {
		this.script = script;
    }

    def inside(match, body) {
        body();
    }

    def up(dockerfile, version) {
    	println "compose dockerfile:" + dockerfile;

    	def file = new File(dockerfile);
    	def dir = file.getParent();

    	this.script.withEnv(["TAG=${version}"]) {
	        this.script.sh "cd ${dir} && docker-compose up -d"
	    }
    }

    def down(config, version) {
    	def file = new File(config);
    	def dir = file.getParent();

    	this.script.sh "cd ${dir} && docker-compose down"
    }
}