
pipeline {
	agent any

	triggers {
		pollSCM 'H/10 * * * *'
	}

	options {
		disableConcurrentBuilds()
		buildDiscarder(logRotator(numToKeepStr: '14'))
	}

	stages {
		
		stage("props file"){
        steps{
            script {

                def props = """Url=https://#########/login.jsp
Username=########
Password=########"""
				
				sh 'pwd'
				sh 'cd complete/src/main/resources/'
				dir("$WORKSPACE/complete/src/main/resources") {
				sh 'pwd'
                def str =  readFile file: "bootstrap.properties"
                echo "$str"
				}
            }
         }
      }
		
		stage("test: baseline (jdk8)") {
			agent {
				any {
					image 'adoptopenjdk/openjdk8:latest'
					args '-v $HOME/.m2:/tmp/jenkins-home/.m2'
				}
			}
			options { timeout(time: 30, unit: 'MINUTES') }
			steps {
				sh 'test/run.sh'
			}
		}

	}

	post {
		changed {
			script {
				 echo "${BUILD_TAG} - ${BUILD_URL}"
			}
		}
	}
}
