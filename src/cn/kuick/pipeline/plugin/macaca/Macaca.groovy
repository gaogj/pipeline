package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;
import java.io.File;

import hudson.FilePath;
import hudson.slaves.WorkspaceList;

/**
 *	Macaca
 */
class Macaca implements Serializable {

	def script;

	public Macaca(script) {
		this.script = script;
    }

    def run() {
        
    }
}