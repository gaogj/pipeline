
/**
 * 分支匹配
 */
def branchMatch(branch, rules) {
	return branch == rules;
}

/**
 * 配置分支规则
 */
def configure(branchRules) {
	def currentBranch = "${env.BRANCH_NAME}";

	if (branchMatch(currentBranch, currentBranch)) {
		return {
			body -> body()
		}
	} else {
		return {
			body -> 
		}
	}
}