package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;
import java.io.File;

/**
 *	Docker Compose Plugin
 */
class DockerComposePlugin implements Serializable {

	def project;
	def steps;

    void apply(project) {
    	this.project = project;
    	this.steps = project.steps;

        println "project.steps:" + project.steps
    }

    def up(config, version) {
    	println "compose config:" + config;

    	def file = new File(config);
    	def dir = file.getParent();

    	this.project.withEnv(["TAG=${version}"]) {
	        this.steps.sh "cd ${dir} && docker-compose up"
	    }
    }

    def down() {
    	println "compose up"
    }
}