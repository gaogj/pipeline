package cn.kuick.pipeline.plugin.dockercompose;

@GrabResolver(name='aliyun', root='http://maven.aliyun.com/nexus/content/groups/public')
@Grab('org.yaml:snakeyaml:1.16')
import org.yaml.snakeyaml.Yaml;

import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;

import hudson.FilePath;

import org.jenkinsci.plugins.workflow.cps.CpsScript;

/**
 *	Docker Compose Cluster
 */
class Cluster implements Serializable {
    def script;
    def dockerCompose;
    
    def id;
    def dockerfile;
    def containers;

    Cluster(dockerCompose, uuid, dockerfile) {
        this.script = dockerCompose.script;
        this.dockerCompose = dockerCompose;

        this.id = uuid;
        this.dockerfile = dockerfile;
        this.services = parseDockerfile(dockerfile);

        println "this.id:" + this.id
        println "this.dockerfile:" + this.dockerfile
        println "this.services:" + this.services
    }

    def parseDockerfile(dockerfile) {
        println "parseDockerfile:" + dockerfile

        try {
            def text = new FilePath(new File(dockerfile)).readToString()
            println "compose text:" + text

            def compose = (Map<String, Object>) (new Yaml().load(text))
            println "compose:" + compose

            // if there is 'version: 2' on top-level then information about services is in 'services' sub-tree
            return '2'.equals(compose.get('version')) ? ((Map) compose.get('services')).keySet() : compose.keySet()
        } catch(e) {
            println e
        }
    }

    def inside(match, body) {
        def container = findMatchContainer(match);
        container.inside body
    }

    def findMatchContainer(match) {
        return new Container(this.script, this.id + "_dealdemoserver_1");
    }

    def down() {
        def file = new File(this.dockerfile);
        def workspace = file.getParent();

        this.script.sh "cd ${workspace} && docker-compose down"
        this.script.sh "cd ${workspace} && docker-compose rm -f -a"
    }
}