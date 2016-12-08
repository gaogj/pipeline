package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;

/**
 *	Docker Compose Plugin
 */
class DockerComposePlugin {

	DockerComposePlugin() {

	}

    void apply(project) {
        println "project.steps:" + project.steps
    }
}