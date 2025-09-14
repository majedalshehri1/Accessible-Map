pipeline {
    agent any

    tools {
	maven 'M3'
        jdk 'JDK21'
    }

    stages {
	stage('Checkout') {
		steps {
			git branch: 'main', url: 'https://github.com/majedalshehri1/Accessible-Map.git'
            }
        }

        stage('Backend - Build & Test') {
		steps {
			dir('backend') {
				script {
					if (isUnix()) {
						sh 'mvn clean test'
                        } else {
						bat 'mvn clean test'
                        }
                    }
                }
            }
        }
    }

    post {
	always {
		junit 'backend/target/surefire-reports/*.xml'
        }
    }
}


