/**
 * 类型匹配
 */
def actionTypeMatch(type, rules) {
	if ("*" == rules || "all" == rules) {
		return true;
	}

	rules = rules.replace("*", "(.*)");
	rules = rules.replace("?", "(.?)");

	return type.matches(rules);
}

/**
 * 分支匹配
 */
def branchMatch(branch, rules) {
	if ("*" == rules || "all" == rules) {
		return true;
	}

	rules = rules.replace("*", "(.*)");
	rules = rules.replace("?", "(.?)");

	return branch.matches(rules);
}

/**
 * 配置分支规则
 */
def call(actionTypeRules, branchRules, body) {
	echo "----------------------------------------------"
	echo "-----------------configure-start--------------"

	def actionType = "${env.CHANGE_TYPE}";
	def branch = "${env.CHANGE_TARGET}";

	echo "current actionType: ${actionType}"
	echo "current Branch: ${branch}"

	echo "actionTypeRules: ${actionTypeRules}"
	echo "branchRules: ${branchRules}"

	if (actionTypeMatch(actionType, actionTypeRules) 
		&& branchMatch(branch, branchRules)) {
		body()
	}

	echo "----------------------------------------------"
	echo "-----------------configure-start--------------"
}