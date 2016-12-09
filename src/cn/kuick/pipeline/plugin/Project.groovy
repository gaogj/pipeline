package cn.kuick.pipeline.plugin;

import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;

import org.jenkinsci.plugins.workflow.cps.CpsScript;

/**
 *	Project
 */
class Project implements Serializable {

    private List<Task> tasks;
	private CpsScript script;

	public Project(CpsScript script) {
		this.script = script;
        this.tasks = new ArrayList<Task>();
    }
}