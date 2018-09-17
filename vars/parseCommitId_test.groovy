
def call() {
	node("aliyun327-test") {
		checkout scm

		def gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
		def shortCommit = gitCommit.take(6)
		def lastTagId = sh(returnStdout: true, script: 'git tag --sort=committerdate | tail -1').trim()

		env.COMMIT_ID = shortCommit;
		env.LAST_TAG_ID = lastTagId
	}
}
