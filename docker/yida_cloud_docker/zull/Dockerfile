FROM java:8
VOLUME /tmp
ADD zuul-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Xmx60m","/app.jar"]
