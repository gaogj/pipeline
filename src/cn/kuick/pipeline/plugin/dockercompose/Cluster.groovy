package cn.kuick.pipeline.plugin.dockercompose;

@GrabResolver(name='aliyun', root='http://maven.aliyun.com/nexus/content/groups/public')
@Grab('org.yaml:snakeyaml:1.16')
import org.yaml.snakeyaml.Yaml;

import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import hudson.FilePath;
import hudson.Util;
import hudson.slaves.WorkspaceList;

import org.jenkinsci.plugins.workflow.cps.CpsScript;

/**
 *	Docker Compose Cluster
 */
class Cluster implements Serializable {
    def script;
    def dockerCompose;
    
    def id;
    def name;
    def version;

    def dockerfile;
    def containers;
    def services = [];

    Cluster(dockerCompose, uuid, dockerfile, name, version) {
        this.script = dockerCompose.script;
        this.dockerCompose = dockerCompose;

        this.id = uuid;
        this.name = name;
        this.version = version;

        this.dockerfile = dockerfile;

        this.script.echo "this.id:" + this.id
        this.script.echo "this.dockerfile:" + this.dockerfile
        this.script.echo "this.services:" + this.services
    }

    def parseDockerfile() {
        try {
            def dockerfile = this.dockerfile;

            def text = this.script.readFile encoding: 'UTF-8', file: dockerfile
            def compose = (Map<String, Object>) (new Yaml().load(text))

            // if there is 'version: 2' on top-level then information about services is in 'services' sub-tree
            def services =  '2'.equals(compose.get('version')) ? ((Map) compose.get('services')).keySet() : compose.keySet()
            
            this.script.echo "services set:" + services.toString()

            for(def it : services) {
                this.script.echo "services it:" + it
                this.services.add(it)
            }

            this.script.echo "services array:" + this.services.toString()
        } catch(e) {
            this.script.echo "error in parseDockerfile:" + e
        }
    }

    def inside(patten, body) {
        def container = findMatchContainer(patten);

        if (container != null) {
            this.script.echo "container:" + container.id
            this.script.sleep 40

            container.inside body
        } else {
            throw new RuntimeException("Not found container with patten:" + patten);
        }
    }

    def waitReady(patten, body) {
        def container = findMatchContainer(patten);

        if (container != null) {
            body(container)
        } else {
            throw new RuntimeException("Not found container with patten:" + patten);
        }
    }

    def findMatchContainer(patten) {
        this.script.echo "services:" + this.services.toString();

        def matchId = null;

        if (this.services.length > 0) {
            switch(patten) {
                case ":first":
                    matchId = this.services[0]
                case ":last":
                    matchId = this.services[his.services.length - 1]:
                default:
                    this.services.each { it.match(patten) && matchId = it}
            }
        }

        if (matchId != null) {
            return new Container(this.script, matchId + "_dealdemoserver_1");
        }

        return null;
    }

    def down() {
        def name = this.name;
        def version = this.version;
        def file = new File(this.dockerfile);
        def workspace = file.getParent();

        this.script.withEnv(["TAG=${version}", "SERVER_NAME=${name}"]) {
            this.script.sh "cd ${workspace} && docker-compose down"
        }
    }
}