
def call() {
	node("aliyun345-build") {
		def gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
		def shortCommit = gitCommit.take(6)

		env.COMMIT_ID = shortCommit;
	}
}
