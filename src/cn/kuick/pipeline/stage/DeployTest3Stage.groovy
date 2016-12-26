package cn.kuick.pipeline.stage;

import java.io.Serializable;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 *	部署测试3环境
 */
class DeployTest3Stage implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def deployNode;

	DeployTest3Stage(script, stageName, config) {
		this.script = script;

		this.stageName = stageName;

		this.serverName = config.name;
		this.version = config.version;
		this.deployNode = config.deployNode;
	}

	def start() {
		this.script.stage(this.stageName) {
		    this.run();
		}
	}

	def readYaml(yamlFile) {
		def text = this.script.readFile encoding: 'UTF-8', file: yamlFile
        return (Map<String, Object>) (new Yaml().load(text))
	}

	def readProperties(propFile) {
		return this.script.readProperties([file: propFile])
	}

	def run() {
		def version = this.version;
		def deployNode = this.deployNode;

		// 部署测试3环境
		this.script.node("aliyun345-test") {
	        this.script.echo "login to aliyun345-test"

	        this.script.checkout this.script.scm

	        def serverEnv = [];

	        this.script.dir("deploy-config") {
	            this.script.git([
	                url: "https://git.kuick.cn/deploys/deploy-config.git", 
	                branch: "master",
	                credentialsId: 'kuick_git_auto_deploy_pwd'
	            ]);

	            def properties = this.readProperties("test3/aliyuncs/application.properties");

	            for(def entry : properties) {
	            	def item = "${entry.key}=${entry.value}";

	            	this.script.echo "Item: ${item}"
	                serverEnv.add(item)
	            }
	        }

	        this.script.echo "serverEnv:" + serverEnv.toString()

	        this.script.withEnv(serverEnv) {
	            this.script.sh "release/docker/test3/deploy.sh ${version}"
	        }

	        this.script.echo "deploy test3 success!"
	    }
	}
}