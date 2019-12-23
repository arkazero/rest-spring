pipeline {
	agent any

	//general variables for the process
	def statusProcess = "Proceso Exitoso"
	def errorMessage = ""

	try {

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
	}catch(e){
		statusProcess = "Proceso con error"
		errorMessage = e.toString()
		throw e
	}finally{
		//emailext(mimeType: 'text/html', replyTo: 'waguilera@redhat.com', subject: statusProcess+" : " + env.JOB_NAME, to: 'waguilera@redhat.com', body: statusProcess + " : " + env.JOB_NAME+ " : "+errorMessage)

	}
}