package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;
import java.io.File;

import org.jenkinsci.plugins.workflow.cps.CpsScript;

/**
 *	Docker Compose Cluster
 */
class Cluster implements Serializable {
    def id;
    def dockerfile;
    def containers;

    Cluster(dockerfile) {
        
    }
}