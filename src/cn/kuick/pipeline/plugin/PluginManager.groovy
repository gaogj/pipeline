package cn.kuick.pipeline.plugin;

import java.io.Serializable;

/**
 *	插件管理
 */
class PluginManager implements Serializable {

	def plugins = [:];

	PluginManager() {

	}

	def registerPlugin(name, plugin) {
		plugins[name] = plugin;
	}

    void applyPlugin(project, pluginName) {
        def plugin = plugins[pluginName];
        plugin.apply(project);
    } 
}