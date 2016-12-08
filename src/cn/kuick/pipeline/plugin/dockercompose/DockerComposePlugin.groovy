package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;

/**
 *	Docker Compose Plugin
 */
class DockerComposePlugin implements Serializable {

	DockerComposePlugin() {

	}

    void apply(project) {
        println "project.steps:" + project.steps
    }

    def up() {
    	println "compose up"
    }

    def down() {
    	println "compose up"
    }
}