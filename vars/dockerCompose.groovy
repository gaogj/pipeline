import cn.kuick.pipeline.plugin.dockercompose.DockerComposePlugin;

def compose = new DockerComposePlugin();

def composeUp() {
	compose.up();
}

def composeDown() {
	compose.down();
}