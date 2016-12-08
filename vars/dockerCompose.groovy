import cn.kuick.pipeline.plugin.dockercompose.DockerComposePlugin;

dockerCompose = new DockerComposePlugin();

def composeUp() {
	dockerCompose.up();
}

def composeDown() {
	dockerCompose.down();
}