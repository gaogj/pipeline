import cn.kuick.pipeline.plugin.dockercompose.DockerComposePlugin;

def dockerCompose = new DockerComposePlugin();

def composeUp() {
	dockerCompose.up();
}

def composeDown() {
	dockerCompose.down();
}