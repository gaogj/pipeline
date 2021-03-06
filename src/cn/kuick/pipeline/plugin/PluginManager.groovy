package cn.kuick.pipeline.plugin;

import java.io.Serializable;
import cn.kuick.pipeline.plugin.dockercompose.DockerComposePlugin;

/**
 *	插件管理
 */
class PluginManager implements Serializable {

	private static PluginManager pluginManager = null;
	public static PluginManager getInstance() {
		if (pluginManager == null) {
			pluginManager = new PluginManager();
		}

		return pluginManager;
	}

	def plugins = [:];

	private PluginManager() {
		init()
	}

	def init() {
		register("docker-compose", new DockerComposePlugin());
	}

	def register(name, plugin) {
		plugins[name] = plugin;
	}

    void apply(project, pluginName) {
        def plugin = plugins[pluginName];

        if (plugin != null) {
	        try {
		        plugin.apply(project);
	        } catch(e) {
	        	println "error in apply plugin:" + e
		    }
		} else {
			println "not found plugin with name:" + pluginName
		}
    } 
}