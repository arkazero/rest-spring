# Start with a base image containing Java runtime
FROM 172.30.1.1:5000/openshift/openjdk-8-rhel8

# Add Maintainer Info
LABEL maintainer="wilmeraguilerab@gmail.com"

# Add the application's jar to the container
ADD target/rest-app-0.0.1-SNAPSHOT.jar /deployments/app.jar

EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/deployments/app.jar"]