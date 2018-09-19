package cn.kuick.pipeline.stage;

import java.io.Serializable;

// 抽取任务
class tasks implements Serializable {
	def script;

	def stageName;
	def serverName;
	def version;
	def projectType;

	tasks (script, stageName, config) {
		this.script = script;

		this.stageName = stageName;
		this.serverName = config.name;
		this.version = config.version;
		this.projectType = config.type;
	}

	// 执行单元测试 代码分析并构建镜像
	def codeBuild(){

	}

	// 执行所有流程
	def wholeFlow(){

	}

	// 执行构建代码并更新Test3 and Prod，跳过test1-test2
	def fixFlow(){

	}

	// 构建基础docker 镜像
	def baseBuild(){

	}


	def start() {
		//TODO
	}