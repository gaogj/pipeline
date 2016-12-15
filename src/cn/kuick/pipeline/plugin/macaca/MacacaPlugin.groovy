package cn.kuick.pipeline.plugin.macaca;

import java.io.Serializable;
import java.io.File;

/**
 *	Macaca Plugin
 */
class MacacaPlugin implements Serializable {

    void apply(script) {
        println "script.steps:" + script.steps
        script.macaca = new Macaca(script);
    }

}