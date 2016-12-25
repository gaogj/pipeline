import groovy.json.JsonOutput;

// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call(text, channel) {
	echo "start bearychat notify"

	def slackURL = 'https://hook.bearychat.com/=bw9Bf/jenkins/ae0703c96e05dd7ffe4ae465c2081f18';

    def payload = JsonOutput.toJson([
    	text : text,
        channel : channel,
        username : "jenkins",
        icon_emoji : ":jenkins:"
    ])

    sh "curl -X POST --data-urlencode \'payload=${payload}\' ${slackURL}"

	echo "end bearychat notify"
}

