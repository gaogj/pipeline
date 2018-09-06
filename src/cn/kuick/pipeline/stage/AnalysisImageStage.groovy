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

        this.script.sh "clair-scanner --ip='10.0.12.233' --report='/tmp/clair/${name}-server.json' registry.kuick.cn/cc/${name}-server:base"

        this.script.mail([
                bcc: '',
                body: "镜像漏洞扫描结果",
                cc: 'devops@kuick.cn',
                from: 'jenkins2@kuick.cn',
                replyTo: '',
                subject: "${env.gitlabSourceRepoName} 镜像漏洞扫描结果 " + buildId,
                to: toMail,
                attachmentsPattern: '/tmp/clair/${name}-server.json'
        ]);

    }
}

