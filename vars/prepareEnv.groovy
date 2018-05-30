// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def userId = build.getCause(Cause.UserIdCause).getUserId()
	echo "userId:${userId}"

echo "BUILD_USER_ID:${BUILD_USER_ID}"

def printPipelineEnv() {
	echo "----------------------------------------------"
	echo "------------------printPipelineEnv-start--------------"


	echo "BRANCH_NAME:${env.BRANCH_NAME}"

	echo "BUILD_ID:${env.BUILD_ID}"
	echo "BUILD_NUMBER:${env.BUILD_NUMBER}"
	echo "BUILD_DISPLAY_NAME:${env.BUILD_DISPLAY_NAME}"
	echo "BUILD_TAG:${env.BUILD_TAG}"
	echo "BUILD_URL:${env.BUILD_URL}"

	echo "CHANGE_ID:${env.CHANGE_ID}"
	echo "CHANGE_TYPE:${env.CHANGE_TYPE}"
	echo "CHANGE_URL:${env.CHANGE_URL}"
	echo "CHANGE_TITLE:${env.CHANGE_TITLE}"

	echo "CHANGE_AUTHOR:${env.CHANGE_AUTHOR}"
	echo "CHANGE_AUTHOR_DISPLAY_NAME:${env.CHANGE_AUTHOR_DISPLAY_NAME}"
	echo "CHANGE_AUTHOR_EMAIL:${env.CHANGE_AUTHOR_EMAIL}"
	echo "CHANGE_TARGET:${env.CHANGE_TARGET}"

	echo "------------------printPipelineEnv-end----------------"
	echo "----------------------------------------------"
}

def printGitLabEnv() {
	echo "----------------------------------------------"
	echo "------------------printGitLabEnv-start--------------"

	echo "gitlabBranch: ${env.gitlabBranch}"
	echo "gitlabActionType: ${env.gitlabActionType}"
	echo "gitlabUserName: ${env.gitlabUserName}"
	echo "gitlabUserEmail: ${env.gitlabUserEmail}"

	echo "gitlabSourceBranch: ${env.gitlabSourceBranch}"
	echo "gitlabSourceRepoHomepage: ${env.gitlabSourceRepoHomepage}"
	echo "gitlabSourceRepoName: ${env.gitlabSourceRepoName}"
	echo "gitlabSourceNamespace: ${env.gitlabSourceNamespace}"
	echo "gitlabSourceRepoURL: ${env.gitlabSourceRepoURL}"
	echo "gitlabSourceRepoSshUrl: ${env.gitlabSourceRepoSshUrl}"
	echo "gitlabSourceRepoHttpUrl: ${env.gitlabSourceRepoHttpUrl}"

	echo "gitlabMergeRequestTitle: ${env.gitlabMergeRequestTitle}"
	echo "gitlabMergeRequestDescription: ${env.gitlabMergeRequestDescription}"
	echo "gitlabMergeRequestId: ${env.gitlabMergeRequestId}"
	echo "gitlabMergeRequestIid: ${env.gitlabMergeRequestIid}"
	echo "gitlabMergeRequestLastCommit: ${env.gitlabMergeRequestLastCommit}"

	echo "gitlabTargetBranch: ${env.gitlabTargetBranch}"
	echo "gitlabTargetRepoName: ${env.gitlabTargetRepoName}"
	echo "gitlabTargetNamespace: ${env.gitlabTargetNamespace}"
	echo "gitlabTargetRepoSshUrl: ${env.gitlabTargetRepoSshUrl}"
	echo "gitlabTargetRepoHttpUrl: ${env.gitlabTargetRepoHttpUrl}"

	echo "gitlabBefore: ${env.gitlabBefore}"
	echo "gitlabAfter: ${env.gitlabAfter}"
	echo "gitlabTriggerPhrase: ${env.gitlabTriggerPhrase}"

	echo "------------------printGitLabEnv-end----------------"
	echo "----------------------------------------------"
}

def call(envCallback) {
	this.printPipelineEnv();
	this.printGitLabEnv();

	// BRANCH_NAME 
	if (env.gitlabSourceBranch != null) {
		env.BRANCH_NAME = env.gitlabSourceBranch;
		env.CHANGE_TARGET = env.gitlabSourceBranch;
	}

	// CHANGE_ID
	if (env.gitlabMergeRequestId != null) {
		env.CHANGE_ID = env.gitlabMergeRequestId;
	}

	// CHANGE_TYPE
	if (env.gitlabActionType != null) {
		env.CHANGE_TYPE = env.gitlabActionType;
	}

	// CHANGE_URL
	if (env.gitlabSourceRepoHttpUrl != null) {
		env.CHANGE_URL = env.gitlabSourceRepoHttpUrl;
	}

	// CHANGE_TITLE
	if (env.gitlabMergeRequestTitle != null) {
		env.CHANGE_TITLE = env.gitlabMergeRequestTitle;
	}

 	// CHANGE_AUTHOR
	if (env.gitlabUserName != null) {
		env.CHANGE_AUTHOR = env.gitlabUserName;
	}

 	// CHANGE_AUTHOR_DISPLAY_NAME
	if (env.gitlabUserName != null) {
		env.CHANGE_AUTHOR_DISPLAY_NAME = env.gitlabUserName;
	}

  	// CHANGE_AUTHOR_EMAIL
	if (env.gitlabUserEmail != null) {
		env.CHANGE_AUTHOR_EMAIL = env.gitlabUserEmail;
	}

	// CHANGE_TARGET
	if (env.gitlabTargetBranch != null) {
		env.CHANGE_TARGET = env.gitlabTargetBranch;
	}

	this.printPipelineEnv();
}