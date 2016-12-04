
/**
 * 分支匹配
 */
def branchMatch(branch, rules) {
	return branch == rules;
}

/**
 * 配置分支规则
 */
def configure(branchRules, body) {
	def currentBranch = "${env.BRANCH_NAME}";

	if (branchMatch(currentBranch, currentBranch)) {
		body()
	}
}