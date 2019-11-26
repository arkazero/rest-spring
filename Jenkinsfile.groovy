
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
			
			mvnCmd = "${mvnHome}/bin/mvn "
			
			
			env.JAVA_HOME=tool 'JDK18'
			env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
			sh 'java -version'
			
			
		}

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
		
		// Using Maven build the war file
		// Do not run tests in this step
		stage('Build') {
			echo "Init Building package"
			
			configFileProvider([configFile(fileId: 'a90e6c1d-7e71-4c2b-b42f-b2e27ab6203c', variable: 'MAVEN_SETTINGS')]) {
				sh "${mvnCmd} clean package -DskipTests -s $MAVEN_SETTINGS"
			}
			echo "End Building package"
		}
		
		
		//Stage for execution Unit Test
		stage('Run Unit Test') {
			echo "Init Unit Test"
			
			configFileProvider([configFile(fileId: 'a90e6c1d-7e71-4c2b-b42f-b2e27ab6203c', variable: 'MAVEN_SETTINGS')]) {
				sh "${mvnCmd} test -s $MAVEN_SETTINGS"
			}
			echo "End Unit Test"
		}
		
			// Using Maven call SonarQube for Code Analysis
        stage('SonarQube Scan') {
			echo "Init Running Code Analysis"
              
  			withSonarQubeEnv('sonar') {
  			
  				configFileProvider([configFile(fileId: 'a90e6c1d-7e71-4c2b-b42f-b2e27ab6203c', variable: 'MAVEN_SETTINGS')]) {
  					sh "${mvnCmd} sonar:sonar " +
  					"-Dsonar.junit.reportsPath=target/surefire-reports -Dsonar.jacoco.reportPath=target/jacoco.exec -s $MAVEN_SETTINGS"
  				}
  			}
			
			sleep(10)
			
			timeout(time: 1, unit: 'MINUTES') {
                waitForQualityGate abortPipeline: true
            }
			
            echo "End Running Code Analysis"
        }
        
        //Public in repository
		stage('Publish to Nexus') {
			echo "Publish to Nexus"
			// TBD
			
			configFileProvider([configFile(fileId: 'a90e6c1d-7e71-4c2b-b42f-b2e27ab6203c', variable: 'MAVEN_SETTINGS')]) {
			
				sh "${mvnCmd} deploy -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus3-nexus.192.168.42.220.nip.io/repository/maven-releases/ "+
				"-DaltSnapshotDeploymentRepository=nexus::default::http://nexus3-nexus.192.168.42.220.nip.io/repository/maven-snapshots/ -s $MAVEN_SETTINGS"
			}

		}
        
        stage('Create Image'){
			echo "Inicia creación image"
			echo devTag
			echo prodTag
			
			openshift.withCluster() {
				openshift.withProject("spring-dev") {
				  openshift.selector("bc", "calculadora-spring").startBuild("--from-file=./target/rest-app-${version}.jar", "--wait=true")
		
				  openshift.tag("calculadora-spring:latest", "calculadora-spring:${devTag}")
				}
			  }
			echo "Termina creación image"
		}

			stage('Deploy to DEV'){
				echo "Inicia Deploy"
				openshift.withCluster() {
					openshift.withProject("spring-dev") {
						//openshift.set("image", "dc/eap-app", "eap-app=172.30.1.1:5000/pipeline-test-dev/eap-app:${devTag}")
						openshift.set("image", "dc/calculadora-spring", "calculadora-spring=172.30.1.1:5000/spring-dev/calculadora-spring:${devTag}")
						
						// Deploy the development application.
						openshift.selector("dc", "calculadora-spring").rollout().latest();
			  
						// Wait for application to be deployed
						def dc = openshift.selector("dc", "calculadora-spring").object()
						def dc_version = dc.status.latestVersion
						echo "La ultima version es: "+dc_version
						def rc = openshift.selector("rc", "calculadora-spring-${dc_version}").object()
			  
						echo "Waiting for ReplicationController calculadora-spring-${dc_version} to be ready"
						while (rc.spec.replicas != rc.status.readyReplicas) {
						  sleep 5
						  rc = openshift.selector("rc", "calculadora-spring-${dc_version}").object()
						}		
					}
				}
				echo "Termina Deploy"
			}
			
				//Deploy to QA
		stage('Deploy to QA') {
			input "Deploy to QA?"
			echo "Deployed qa"
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
