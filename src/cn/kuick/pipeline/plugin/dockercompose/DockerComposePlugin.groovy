package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;
import java.io.File;

/**
 *	Docker Compose Plugin
 */
class DockerComposePlugin implements Serializable {

    void apply(script) {
        println "script.steps:" + script.steps
        script.dockerCompose = new DockerCompose(script);
    }

}