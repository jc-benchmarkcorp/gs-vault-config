def VAULT_ADDR = "http://vault-hbm-benchmark.ocp311-apps.ocp.mc1985.net"

pipeline {
	agent any

	triggers {
		pollSCM 'H/10 * * * *'
	}

	options {
		disableConcurrentBuilds()
		buildDiscarder(logRotator(numToKeepStr: '14'))
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



	post {
		changed {
			script {
				 echo "${BUILD_TAG} - ${BUILD_URL}"
			}
		}
	}
}