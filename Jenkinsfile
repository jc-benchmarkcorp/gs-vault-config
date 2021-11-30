pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds()
    }
    
    environment {
        PIPELINE_ROLE_ID= 'bdf96b76-10d0-5212-a63b-c84eaf79228f'
        VAULT_ADDR = "http://vault-hbm-benchmark.ocp311-apps.ocp.mc1985.net"
        APP_SECRET_ID = ""
        
    }
    
    stages{   
    
stage("Jenkins Creates a Wrapped Secret ID for the Pipeline") {
    steps {
        withCredentials([
        [
            $class: 'VaultTokenCredentialBinding',
            credentialsId: 'jenkins-vault-approle',
            vaultAddr: 'http://vault-hbm-benchmark.ocp311-apps.ocp.mc1985.net'
        ]
    ]) {
        script {
        WRAPPED_SID = sh(script: 'curl -H "X-Vault-Token: $VAULT_TOKEN" -H "X-Vault-Wrap-TTL: 40s" -X POST $VAULT_ADDR/v1/auth/pipeline/role/jacapp-pipeline-approle/secret-id', returnStdout: true)
        WRAPPED_SID_JSON = readJSON text: WRAPPED_SID
        WRAPPED_SID_ISOLATE = WRAPPED_SID_JSON.wrap_info.token
        echo "WRAPPED PIPELINE SECRET TOKEN VALUE = ${WRAPPED_SID_ISOLATE}"
        }
    }
    }
}
stage("Unwrap Pipeline Secret ID") {
    steps {
        withCredentials([
        [
            $class: 'VaultTokenCredentialBinding',
            credentialsId: 'jenkins-vault-approle',
            vaultAddr: 'http://vault-hbm-benchmark.ocp311-apps.ocp.mc1985.net'
        ]
    ]) {
        script {
        UNWRAPPED_SID = sh(script: """curl --silent -H "X-Vault-Token: $VAULT_TOKEN" -X POST -d \'{\"token\": "'"${WRAPPED_SID_ISOLATE}"'"}\' $VAULT_ADDR/v1/sys/wrapping/unwrap""", returnStdout: true)
        UNWRAPPED_SID_JSON = readJSON text: UNWRAPPED_SID
        PIPELINE_SECRET_ID = UNWRAPPED_SID_JSON.data.secret_id
        echo "PIPELINE SECRET ID = ${PIPELINE_SECRET_ID}"
        }
    }
    }
}
stage("Pipeline gets login token with Role ID and unwrapped Secret ID") {
    steps {
        script {
        PIPELINE_TOKEN = sh(script: """curl --silent -X POST -d \'{\"role_id\": "'"${PIPELINE_ROLE_ID}"'", \"secret_id\": "'"${PIPELINE_SECRET_ID}"'"}\' $VAULT_ADDR/v1/auth/pipeline/login""", returnStdout: true)
        echo "PIPELINE TOKEN = ${PIPELINE_TOKEN}"
		PIPELINE_TOKEN_JSON = readJSON text: PIPELINE_TOKEN
        PIPELINE_LOGIN = PIPELINE_TOKEN_JSON.auth.client_token
        echo "PIPELINE TOKEN = ${PIPELINE_LOGIN}"
        }
    }
}
stage("Read OC Login Secret") {
    steps {
        script {
        OC_LOGIN = sh(script: """curl --silent --header "X-Vault-Token: $PIPELINE_LOGIN" -X GET $VAULT_ADDR/v1/secret/data/creds/ocp""", returnStdout: true)
        OC_LOGIN_JSON = readJSON text: OC_LOGIN
        OC_LOGIN_TOKEN = OC_LOGIN_JSON.data.data.token
        echo "OC LOGIN SECRET = ${OC_LOGIN_JSON.data.data.token}"
        }
    }
}
stage("Read Role ID and Generate Wrapped Secret ID for Application") {
    steps {
        script {
        APP_ROLE_ID = sh(script: """curl --silent --header "X-Vault-Token: $PIPELINE_LOGIN" -X GET $VAULT_ADDR/v1/auth/approle/role/jacapp-approle/role-id""", returnStdout: true)
        APP_ROLE_ID_JSON = readJSON text: APP_ROLE_ID
        APPROLE_ID_VALUE = APP_ROLE_ID_JSON.data.role_id
        APP_WRAPPED_TOKEN = sh(script: """curl --silent -H "X-Vault-Token: $PIPELINE_LOGIN" -H "X-Vault-Wrap-TTL: 200s" -X POST $VAULT_ADDR/v1/auth/approle/role/jacapp-approle/secret-id""", returnStdout: true)
        APP_WRAPPED_TOKEN_JSON = readJSON text: APP_WRAPPED_TOKEN
        APP_WRAPPED_TOKEN_ISOLATE = APP_WRAPPED_TOKEN_JSON.wrap_info.token
        echo "APPLICATION ROLE_ID = ${APPROLE_ID_VALUE}"
        echo "WRAPPED APPLICATION SECRET TOKEN VALUE = ${APP_WRAPPED_TOKEN_ISOLATE}"
        }
    }
}
stage("Unwrap App Secret ID") {
    steps {
        script {
        UNWRAPPED_APP_SID = sh(script: """curl --silent -H "X-Vault-Token: $PIPELINE_LOGIN" -X POST -d \'{\"token\": "'"${APP_WRAPPED_TOKEN_ISOLATE}"'"}\' $VAULT_ADDR/v1/sys/wrapping/unwrap""", returnStdout: true)
        UNWRAPPED_APP_TOKEN_JSON = readJSON text: UNWRAPPED_APP_SID
        APP_SECRET_ID = UNWRAPPED_APP_TOKEN_JSON.data.secret_id
        echo "APPLICATION SECRET ID = ${APP_SECRET_ID}"
        }
    }
    }

// stage("App gets login token with Role ID and unwrapped Secret ID") {
//     steps {
//         script {
//         APP_LOGIN_TOKEN = sh(script: """curl --silent -X POST -d \'{\"role_id\": "'"${APPROLE_ID_VALUE}"'", \"secret_id\": "'"${APP_SECRET_ID}"'"}\' $VAULT_ADDR/v1/auth/approle/login""", returnStdout: true)
//         APP_LOGIN_TOKEN_JSON = readJSON text: APP_LOGIN_TOKEN
//         APP_LOGIN = APP_LOGIN_TOKEN_JSON.auth.client_token
//         echo "APPLICATION TOKEN = ${APP_LOGIN}"
//         }
//     }
// }    

stage("test: baseline (jdk8)") {
			agent {
				any {
					image 'adoptopenjdk/openjdk8:latest'
					args '-v $HOME/.m2:/tmp/jenkins-home/.m2'
				}
			}
			options { timeout(time: 30, unit: 'MINUTES') }
			steps {
				echo "APPLICATION SECRET ID = ${APP_SECRET_ID}"
                sh 'test/run.sh'
			}
		}

	}
	
	
// stage("Read APP Secrets") {
//     steps {
//         script {
//         sleep 30
//         APP_SECRETS = sh(script: """curl --silent --header "X-Vault-Token: $APP_LOGIN" -X GET $VAULT_ADDR/v1/secret/data/myapp1""", returnStdout: true)
//         APP_SECRETS_JSON = readJSON text: APP_SECRETS
//         echo "USERNAME is ${APP_SECRETS_JSON.data.data.'username'}"
//         echo "PASSWORD is ${APP_SECRETS_JSON.data.data.'password'}"
//         }
//     }
// }  
// }  
    
 post {
        always {
            script {
                 echo "${BUILD_TAG} - ${BUILD_URL}"
            }
        }
    }
}