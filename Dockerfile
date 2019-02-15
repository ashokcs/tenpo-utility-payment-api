FROM openjdk:8-jre-alpine
COPY ./build/libs/multipay-utility-payments-api-0.0.1-SNAPSHOT.jar /usr/src/myapp/app.jar
COPY ./lib/elastic-apm-agent-1.3.0.jar /usr/src/myapp/agent.jar
WORKDIR /usr/src/myapp
CMD ["java", "-javaagent:agent.jar", "-jar", "-XX:+UseSerialGC", "-Xss512k", "-XX:MaxRAM=72m", "app.jar", "--spring.profiles.active=production"]