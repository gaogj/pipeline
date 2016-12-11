package cn.kuick.pipeline.plugin.dockercompose;

/**
 *  Docker Compose Container
 */
class Container implements Serializable {

    def script;
    def id;

    def mappingPorts = []

    Container(script, id) {
        this.script = script;
        this.id = id;
    }

    def ports() {
        def containerId = this.id;
        def portInfo = this.script.sh script: "docker port ${containerId}", returnStdout:true

        portInfo.split("\n").each {
            mappingPorts.add(it);
        }
    }

    def sh(commandLine) {
        def containerId = this.id;
        this.script.sh "docker exec ${containerId} ${commandLine}"
    }

    def exec(commandLine) {
        try {
            this.sh commandLine
            return true;
        } catch(e) {
            this.script.echo e.message
            return false;
        }
    }

    def logs(follow = false, tailClount = 100) {
        def containerId = this.id;
        def opts = "";

        if (follow) {
            opts += "-f "
        }

        if (tailClount) {
            opts += "--tail  ${tailClount}"
        }

        this.script.sh "docker logs ${opts} ${containerId}"
    }
}
