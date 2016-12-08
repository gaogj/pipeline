package cn.kuick.pipeline.plugin;

import java.io.Serializable;

/**
 *	插件接口
 */
interface Plugin {
    void apply(project);
}