FROM registry.gitlab.com/tenpo/docker_openjdk_8_jre_alpine_newrelic
COPY ./build/libs/tenpo-utility-payments-api-0.0.1-SNAPSHOT.jar /usr/src/myapp/app.jar
WORKDIR /usr/src/myapp
CMD ["java", "-jar", "-XX:+UseSerialGC", "-Xss512k", "-XX:MaxRAM=200m", "app.jar"]