FROM openjdk:8-jre-alpine as staging
COPY ./build/libs/multipay-utility-payments-api-0.0.1-SNAPSHOT.jar /usr/src/myapp/app.jar
WORKDIR /usr/src/myapp
CMD ["java", "-jar", "-XX:+UseSerialGC", "-Xss512k", "-XX:MaxRAM=72m", "app.jar", "--spring.profiles.active=staging"]

FROM openjdk:8-jre-alpine as sandbox
COPY ./build/libs/multipay-utility-payments-api-0.0.1-SNAPSHOT.jar /usr/src/myapp/app.jar
WORKDIR /usr/src/myapp
CMD ["java", "-jar", "-XX:+UseSerialGC", "-Xss512k", "-XX:MaxRAM=72m", "app.jar", "--spring.profiles.active=sandbox"]

FROM openjdk:8-jre-alpine as production
COPY ./build/libs/multipay-utility-payments-api-0.0.1-SNAPSHOT.jar /usr/src/myapp/app.jar
WORKDIR /usr/src/myapp
CMD ["java", "-jar", "-XX:+UseSerialGC", "-Xss512k", "-XX:MaxRAM=72m", "app.jar", "--spring.profiles.active=production"]