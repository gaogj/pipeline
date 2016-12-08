package cn.kuick.pipeline.plugin;

import java.io.Serializable;

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

	}

	def register(name, plugin) {
		plugins[name] = plugin;
	}

    void apply(project, pluginName) {
        def plugin = plugins[pluginName];
        plugin.apply(project);
    } 
}