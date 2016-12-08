package cn.kuick.pipeline.plugin.dockercompose;

import java.io.Serializable;
import java.io.File;

/**
 *	Docker Compose Cluster
 */
class Cluster implements Serializable {

	Cluster() {

    }

    def inside(match, body) {
        body();
    }
}