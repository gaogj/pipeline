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
            this.script.echo "parseDockerfile:" + dockerfile

            def text = this.script.readFile encoding: 'UTF-8', file: dockerfile
            this.script.echo "compose text:" + text

            def compose = (Map<String, Object>) (new Yaml().load(text))
            this.script.echo "compose:" + compose

            // if there is 'version: 2' on top-level then information about services is in 'services' sub-tree
            def services =  '2'.equals(compose.get('version')) ? ((Map) compose.get('services')).keySet() : compose.keySet()
            services.each {
                it -> this.services.add(it)
            }
        } catch(e) {
            this.script.echo "error in parseDockerfile:" + e
        }
    }

    def inside(match, body) {
        def container = findMatchContainer(match);
        this.script.echo "container:" + container.id

        this.script.sleep 5

        container.inside body
    }

    def waitReady(match, body) {
        def container = findMatchContainer(match);
        body(container)
    }

    def findMatchContainer(match) {
        return new Container(this.script, this.id + "_dealdemoserver_1");
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