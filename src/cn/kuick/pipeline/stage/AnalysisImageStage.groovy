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

    AnalysisImageStage(script, stageName, config) {
        this.script = script;

        this.stageName = stageName;
        this.serverName = config.name;
        this.version = config.version;
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
        def imageName = "registry.kuick.cn/cc/${name}-server:base"
        def clairServerIp = "10.0.9.195" // platform-node2-350
        def localHGostIp = "10.0.12.233" // build-345
        def reportPath = "./imageScannerReport/${name}-server.json"

        def parameter = "--ip='${localHGostIp}' --clair='${clairServerIp}' --report=${reportPath} ${imageName} "

        this.script.sh "clair-scanner ${parameter}"

    }
}

