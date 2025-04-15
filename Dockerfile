FROM gradle:8.10.2-jdk21-alpine

WORKDIR /app

COPY . /app

RUN gradle build -x test

CMD ["java", "-jar", "build/libs/CRM-Module-1.0-SNAPSHOT.jar"]