/**
 * 类型匹配
 */
def actionTypeMatch(type, rules) {
	return type == rules;
}

/**
 * 分支匹配
 */
def branchMatch(branch, rules) {
	return branch == rules;
}

/**
 * 配置分支规则
 */
def call(actionTypeRules, branchRules, body) {
	def actionType = "${env.gitlabActionType}";
	def currentBranch = "${env.BRANCH_NAME}";

	if (currentBranch == null) {
		currentBranch = "${env.gitlabBranch}"
	}

	echo "actionType: ${actionType}"
	echo "currentBranch: ${currentBranch}"

	echo "actionTypeRules: ${actionTypeRules}"
	echo "branchRules: ${branchRules}"

	if (actionTypeMatch(actionType, actionTypeRules) 
		&& branchMatch(currentBranch, branchRules)) {
		body()
	}
}