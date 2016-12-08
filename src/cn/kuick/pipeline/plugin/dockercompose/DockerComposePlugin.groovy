package cn.kuick.pipeline.stage;

import java.io.Serializable;
import cn.kuick.pipeline.plugin.Plugin;

/**
 *	Docker Compose Plugin
 */
class DockerComposePlugin implements Plugin {

    void apply(project) {
        println "project.steps:" + project.steps
    }
}