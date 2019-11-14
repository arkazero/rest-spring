
node{

	//general variables for the process
	def statusProcess = "Proceso Exitoso"
	def errorMessage = ""

	try {
		
		def mvnCmd
		def mvnHome
		def groupId
		def artifactId
		def version
		def devTag
		def prodTag

		//Stage for configuration of the pipeline
		stage('Preparing'){
			mvnHome = tool 'M2_3.6.2'

			// Define Maven Command. Make sure it points to the correct
			// settings for our Nexus installation (use the service to
			// bypass the router). The file settings.xml
			// needs to be in the Source Code repository.
			
			mvnCmd = "${mvnHome}/bin/mvn -s ./settings.xml"
			
			
			env.JAVA_HOME=tool 'JDK18'
			env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
			sh 'java -version'
			
			
		}

		//Stage para obtener el código fuente del repositorio GIT
		stage('Checkout'){
			echo "Checkout Source"
			git branch: 'develop', credentialsId: 'git-redhat', url: 'https://gitlab-devops-infra.cloudapps.terpel.com/waguiler/bootwildfly.git'

			groupId    = getGroupIdFromPom("pom.xml")
			artifactId = getArtifactIdFromPom("pom.xml")
			version    = getVersionFromPom("pom.xml")

			echo groupId
			echo artifactId
			echo version
			
			// Set the tag for the development image: version + build number
			devTag  = "${version}-" + currentBuild.number
			// Set the tag for the production image: version
			prodTag = "${version}"
		}
		
		// Using Maven build the war file
		// Do not run tests in this step
		stage('Build') {
			echo "Init Building package"
			sh "${mvnCmd} clean package -DskipTests"
			echo "End Building package"
		}
		
		
		//Stage for execution Unit Test
		stage('Run Unit Test') {
			echo "Init Unit Test"
			// TBD
			sh "${mvnCmd} test"
			echo "End Unit Test"
		}
		
			// Using Maven call SonarQube for Code Analysis
        stage('SonarQube Scan') {
			echo "Init Running Code Analysis"
              
  			withSonarQubeEnv('sonar') {
  				sh "${mvnCmd} sonar:sonar " +
  				"-Dsonar.junit.reportsPath=target/surefire-reports -Dsonar.jacoco.reportPath=target/jacoco.exec"
  			}
			
			sleep(10)
			
			timeout(time: 1, unit: 'MINUTES') {
                waitForQualityGate abortPipeline: true
            }
			
            echo "End Running Code Analysis"
        }
        
		
		
		}catch(e){
        statusProcess = "Proceso con error"
        errorMessage = e.toString()
        throw e
    }finally{
        //emailext(mimeType: 'text/html', replyTo: 'waguilera@redhat.com', subject: statusProcess+" : " + env.JOB_NAME, to: 'waguilera@redhat.com', body: statusProcess + " : " + env.JOB_NAME+ " : "+errorMessage)

    }


}

// Convenience Functions to read variables from the pom.xml
// Do not change anything below this line.
// --------------------------------------------------------
def getVersionFromPom(pom) {
    def matcher = readFile(pom) =~ '<version>(.+)</version>'
    matcher ? matcher[0][1] : null
}
def getGroupIdFromPom(pom) {
    def matcher = readFile(pom) =~ '<groupId>(.+)</groupId>'
    matcher ? matcher[0][1] : null
}
def getArtifactIdFromPom(pom) {
    def matcher = readFile(pom) =~ '<artifactId>(.+)</artifactId>'
    matcher ? matcher[0][1] : null
}
