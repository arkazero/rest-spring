
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
		
		//Stage para obtener el código fuente del repositorio GIT
		stage('Checkout'){
			echo "Checkout Source"
			git credentialsId: 'githubWilmer', url: 'https://github.com/wilmeraguilera/rest-spring.git'

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

		//Stage for configuration of the pipeline
		stage('Preparing'){
			mvnHome = tool 'M2_3.6.2'

			// Define Maven Command. Make sure it points to the correct
			// settings for our Nexus installation (use the service to
			// bypass the router). The file settings.xml
			// needs to be in the Source Code repository.
			
			mvnCmd = "${mvnHome}/bin/mvn  -s ./settings.xml "
			
			 configFileProvider(
        	[configFile(fileId: 'MAVEN_SETTINGS_XML', variable: 'MAVEN_SETTINGS')]) {
        		sh 'mvn -s $MAVEN_SETTINGS clean package'
    		}
			
			
			env.JAVA_HOME=tool 'JDK18'
			env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
			sh 'java -version'
			
			
		}

		
		
		// Using Maven build the war file
		// Do not run tests in this step
		stage('Build') {
			echo "Init Building package"
			sh "${mvnCmd} clean package -DskipTests"
			echo "End Building package"
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
