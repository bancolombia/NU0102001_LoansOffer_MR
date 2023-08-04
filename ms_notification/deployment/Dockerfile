FROM artifactory.apps.bancolombia.com/devops/adoptopenjdk/openjdk11-openj9:x86_64-alpine-jre-11.0.10_9_openj9-0.24.0
WORKDIR /app
COPY app-service.jar app.jar
ENV TZ America/Bogota

LABEL version="1.0.0" maintainer="sefranco@bancolombia.com.co" description="Send notifications microservice"
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -Dvertx.disableDnsResolver=true"
USER appuser
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar ./app.jar"]
