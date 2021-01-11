'''Jenkins Pipeline Tutorial'''
------------------------------------------
pipeline
|- Main function of pipeline script.

agent
|- where target mahcine to run pipeline script.
|- what is jenkins use what "executor" to run this script
|- can be use in each stages but mostly use in top-level of stages (under pipeline block) or each stage block.
|- Example agent mostly use.
|--- none => if none you must be announce of each stage block
|--- any => can use any executor
|--- docker => use docker executor in this stages/global
|--- label '<hostname>' => use this hostname to be executor
stages
|- A group of stage.
|- Must be contain at least 1 stage in here.

stage
|- "stage" need to be inside "stages".
|- Define description of stage (build,deploy,waiting for approval...) for this job.
|- Name of stage will be display on the Jenkins Dashboard.

steps
|- "steps" need to be inside "stage".
|- Contain command/script that we use to build.
|- Script in here is up to batch/shell up to environment settings on your target machine.
|--- echo
|--- sh

post [optional]
|- What will do next after build finish.

Example:
------------------------------------------
/* Basic will be like this */
pipeline{                       // Pipeline is like main function in other language
    agent any                   // any = can use any executor
    stages{                     // 1 long pipe(stages) include many pipe(stage)
        stage("stage1"){        // name of stage = "stage1"
            steps{              // Script of this stage call "Step"
                echo "this is step1: echo1 in stage1."          //just echo
                echo "this is step1: echo2 in stage1."
            }
        }
        stage("stage2"){        // name of stage = "stage2"
            steps{
                echo "this is step2: echo1 in stage2."
                echo "this is step2: echo2 in stage2."
            }
        }
    }
}
------------------------------------------
Variable and Concatenation Operator 
|- Variable should be define on pipeline level or stage level like global variable or local variable in each language
|- If has a same name of variable jenkins will using the variable following this level:
|--- 1 script
|--- 2 stage
|--- 3 stages
|--- 4 pipeline (global level)

Example:
------------------------------------------
pipeline{
    agent any
    environment{                                //setting global variable (under pipeline)
        str1 = "String1"
        str2 = "String2"
    }
    stages{
        stage("String Concatenation"){
            steps{
                script{                         //setting local variable, "concat" can be use only this steps.
                    concat = str1 + " and " + str2
                }
                echo "str1 = ${str1}"           // ${var} is a show variable in .sh
                echo "str2 = ${str2}"           // %var% is a show variable in .bat
                echo "concat = ${concat}"
            }
        }
    }
}
------------------------------------------
Parameter 
|- is a Preparation of Variables ***before*** Build *
|- parameter "name" in each of parameter is a **title of parameter** and also **variable name** if you want to use variable in the future.
|- Parameter
|--- string ==> textbox
|--- text ==> long textbox like string param but longer
|--- boolean ==> like checkbox, Its "true" or "false"
|--- choice ==> use dropdown box to select value
|--- password ==> text will be text secret type
|--- file ==> File Selection

Example:
------------------------------------------
pipeline {
    agent any
    
    parameters {
        string (name: 'TOOL', defaultValue: 'Mr Jenkins ', description: 'Who should I say hello to ?')
        choice (name: 'STAGE', choices: 'Build\nTest\nDeploy\n', description: 'Delimiters within stage builder')
        booleanParam (name: 'SONAR', defaultValue: true, description: 'Toggle this value')
        text (name: 'DESC', defaultValue: '', description: 'Enter some information about the person.')
        password (name: 'PASSWORD', defaultValue: 'SECRET', description: 'Enter a password')
        file (name: 'FILE', description: 'Choose file')
    }
    
    stages {
        stage ("Tool") {
            steps {
                echo "$TOOL or ${params.TOOL}"
            }
        }
        
        stage ("choice") {
            steps {
                echo "$STAGE"
            }
        }
        
        stage ("sonar")  {
            steps {
                script {
                    if(params.SONAR) {
                        echo "Sonar steps is TRUE."
                    }
                    else{
                        echo "Sonar steps is FALSE."
                    }
                }
            }
        }
        
        stage ("Test") {
            steps {
                script {
                    if(params.STAGE=="Test") {
                        echo "This is test stage"
                    }
                }
            }
        }
        
    } // End of stages
} // End of pipeline
------------------------------------------
Option Section
|- Retry()
|--- if failing the job, will run the job again xxx time.
|
|- buildDiscarder logRotator()
|--- Delete old build logs.
|
|- disableConcurrentBuilds()
|--- used for disable concurrent build.
|
|- timeout()
|--- if this job working longer than xxx will be timeout and cancel this job.
|
|- timestamps()
|--- add time to the build process.

pipeline{
    ..
    option{
        retry(3)
        //retry 3times if job can't execute

        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')
        //keep latest 5 build success

        disableConcurrentBuilds()
        //used for disable concurrent build

        timeout(time: 1, unit: 'MILLISECONDS')
        //if this job run longer than 1ms this job will be timeout and cancel

        timestamps()
        //add timestamp of build process
        
    }
    ..
}
------------------------------------------
Build Trigger
|- Trigger jobs from pipeline script

pipeline{
    ..
    stage("Trigger Build1"){
        steps{
            build ('job1')          //build job1 using command
            build 'job2'            //job2 will be build after job1 complete
        }
    }
    stage("Trigger Build2"){
        steps{
            build ('job1', propagate:false)
            build 'job2'            //job2 will be build even job1 is failed
        }
    }
    ..
}

|
|--- call a job by passing parameters
|

pipeline {
    agent any
    parameters {
        string(name:'msg', defaultValue:"Hello")
        string(name:'buildnumber', defaultValue:"3")
    }
   
    stages {
        stage ("Downstream") {
            steps {
                echo "Msg : ${msg}"
                echo "Build number: ${buildnumber}"
                echo "current build number: ${BUILD_NUMBER}"
            }
        }
    }
}
------------------------------------------
Cronjob
|- just a normal cronjob

pipeline{
    ..
    triggers {
        cron('* * * * *')
        pollSCM('* * * * *') // "cron" and "pollSCM" are the same.
    }
    ..
}
------------------------------------------
Approval
|- waiting someone approval to continue this job
|- Use "Pipeline syntax" -> "Sample Step" -> "input: Wait for interactive input" to generate pipeline for approval step

pipeline{
    ..
    stages{
        ..
        stage("Approval: User1"){
                input id: 'User1', 
                message: 'Approval : User1', 
                ok: 'Approved', 
                submitter: 'User1', 
                submitterParameter: 'Approved'
        }
        ..
    }
    ..
}
------------------------------------------
checkout: Check out from version control
|- Git clone to Build Machine using "pipeline script project"
|- same as "New -> freestyle project" can managed from "Source Code Management"

pipeline{
    ..
    stages{
        stage("AAA"){
            agent{
                label "machineA"                    //Specific machine named "machineA" to build this script
            }
            steps{                                  //This step can use "Pipeline Syntax" to generate this steps
                checkout([$class: 'GitSCM',         
                branches: [[name: '*/master']],     //Branch to build
                doGenerateSubmoduleConfigurations: false, 
                extensions: [[$class: 'RelativeTargetDirectory', 
                relativeTargetDir: '/work/dir/path']], //custom work dir path, if not custom, It's will use default path of credential
                gitTool: 'machineA',                //Specific machine named "machineA" to build this script
                submoduleCfg: [], 
                userRemoteConfigs: [[credentialsId: 'AAAA-AAAA-1234-BBBB',     //Credential configuration that jenkins managed node
                url: 'ssh://git@gitlab.yourdomain.local/dev/your_project.git']]])   //your git project
            }
        }
    }
    ..
}
------------------------------------------
Line Notification
|- Just a pipeline script in Post-build action
|- In freestyle job can check at "Post-build action" section (in freestyle use other method)

def notifyLINE(status) {
    def token = "Line-Notification-Token-Here"
    def jobname = env.JOB_NAME
    def buildno = env.BUILD_NUMBER
    def timestamp = new Date().format("yyyy-MM-dd : ['T'HH:mm:ss.SSSXXX]")      //timestamp
    
    def url = 'https://notify-api.line.me/api/notify'
    def message = "Job_Name : ${jobname} \n Build_Number : #${buildno} \n Build_Status : ${status} \n Build_Time : ${timestamp} \r\n"

    sh "curl ${url} -H 'Authorization: Bearer ${token}' -F 'message= \n ${message}'"
}

pipeline{
    ..
    ..
    post {               //Post-build action section
        success {        //If success what will do next ?
            notifyLINE("SUCCESS")       //send "SUCCESS" to function "notifyLINE"
        }
        failure {       //If failed what will do next ?
            notifyLINE("FAILED")        //send "FAILED" to function "notifyLINE"
        }
        fixed {
            notifyLINE("FIXED")
        }
        aborted {
            notifyLINE("ABORTED")
        }
        always {        //Do whatever this job failed or not.
            //Send email to ....
        }
    }
}