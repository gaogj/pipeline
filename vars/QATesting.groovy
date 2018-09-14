// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call(stageName, body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "stageName:${stageName}"
    echo "param name:${config.name}"
    echo "param version:${config.version}"
    
    stage(stageName) {
	    echo "${stageName} run ok!"

        def buildId = env.BUILD_ID;
        def toMail = config.toMail;

        def title = "${config.name}已经部署测试2!"
        def content = "${config.name}已经部署测试2!";

//        if (toMail != null && toMail != "zhuguoliang@kuick.cn" ) {
//            mail([
//                bcc: '',
//                body: content,
//                cc: '',
//                from: 'jenkins2@kuick.cn',
//                replyTo: '',
//                subject: title,
//                to: toMail
//            ]);
//
//            echo "mail send ok!"
//        }


        bearychatNotify(content);

        input message: "QA测试是否通过?"
	}
}