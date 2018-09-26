
def call() {
	node("aliyun345-test") {
		checkout scm

		def gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
		def shortCommit = gitCommit.take(6)

		// def lastTagId = sh(returnStdout: true, script: 'git tag --sort=committerdate | tail -1').trim()
		// jenkins 不支持 --sort=committerdate
		def lastTagId = sh(returnStdout: true, script: 'git describe --tags `git rev-list --tags --max-count=1`  2>/dev/null || echo noTags').trim()

		env.COMMIT_ID = shortCommit;
		env.LAST_TAG_ID = lastTagId
	}
}
