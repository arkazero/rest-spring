pipeline {
	agent any

	//general variables for the process
	def statusProcess = "Proceso Exitoso"
	def errorMessage = ""

		stages {
			stage('Build') {
				steps {
					echo 'Building..'
				}
			}
			stage('Test') {
				steps {
					echo 'Testing..'
				}
			}
			stage('Deploy') {
				steps {
					echo 'Deploying....'
				}
			}
		}

}