package cn.kuick.pipeline.plugin.dockercompose;

/**
 *  Docker Compose Container
 */
class Container implements Serializable {

    def script;
    def id;

    Container(script, id) {
        this.script = script;
        this.id = id;
    }

    def inside(body) {
        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()

        def containerId = this.id;

        this.script.sh "docker exec -it ${containerId} script /dev/null -c '${config.commandLine}'"
    }
}
