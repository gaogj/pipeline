package cn.kuick.pipeline.stage;

import java.io.Serializable;
import java.io.File;

/**
 * 	容器的漏洞静态分析
 */

class AnalysisImageStage implements Serializable {
    def script;

    def stageName;
    def serverName;
    def version;
    def clairUrl;
    def buildNodeIP;

    AnalysisImageStage(script, stageName, config) {
        this.script = script;

        this.stageName = stageName;
        this.serverName = config.name;
        this.version = config.version;
        this.buildNodeIP = config.buildNodeIP
        this.clairUrl = config.clairUrl
    }

    def start() {
        this.script.stage(this.stageName) {
            this.script.node('aliyun345-build') {
                this.script.checkout this.script.scm

                this.run()
            }
        }
    }

    def run() {
//        def docker = this.script.docker;
        def name = this.serverName;
        def clairUrl = this.clairUrl
        def buildNodeIP = this.buildNodeIP
        def imageName = "registry.kuick.cn/cc/${name}-server:base"
        def reportPath = "./imageScanner-Report-${name}-server.json"

        def parameter = "--ip='${buildNodeIP}' --clair='${clairUrl}' --report=${reportPath} ${imageName} "

        this.script.sh "clair-scanner ${parameter}"
    }
}

