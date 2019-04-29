FROM openjdk:8-jre-alpine
COPY ./build/libs/tenpo-utility-payments-api-0.0.1-SNAPSHOT.jar /usr/src/myapp/app.jar
WORKDIR /usr/src/myapp
CMD ["java", "-jar", "-XX:+UseSerialGC", "-Xss512k", "-XX:MaxRAM=72m", "app.jar"]