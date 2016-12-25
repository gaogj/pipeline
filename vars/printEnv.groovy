// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call() {
	echo "----------------------------------------------"
	echo "------------------printEnv-start--------------"

	echo "BRANCH_ID:${env.BRANCH_ID}"
	echo "BRANCH_NAME:${env.BRANCH_NAME}"

	echo "CHANGE_ID:${env.CHANGE_ID}"
	echo "CHANGE_URL:${env.CHANGE_URL}"
	echo "CHANGE_TITLE:${env.CHANGE_TITLE}"

	echo "CHANGE_AUTHOR:${env.CHANGE_AUTHOR}"
	echo "CHANGE_AUTHOR_DISPLAY_NAME:${env.CHANGE_AUTHOR_DISPLAY_NAME}"
	echo "CHANGE_AUTHOR_EMAIL:${env.CHANGE_AUTHOR_EMAIL}"
	echo "CHANGE_TARGET:${env.CHANGE_TARGET}"

	echo "gitlabBranch: ${env.gitlabBranch}"
	echo "gitlabSourceBranch: ${env.gitlabSourceBranch}"
	echo "gitlabActionType: ${env.gitlabActionType}"
	echo "gitlabUserName: ${env.gitlabUserName}"

	echo "gitlabUserEmail: ${env.gitlabUserEmail}"
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

	echo "------------------printEnv-end----------------"
	echo "----------------------------------------------"
}