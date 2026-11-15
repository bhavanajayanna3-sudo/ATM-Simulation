FROM eclipse-temurin:21-jdk

WORKDIR /app

RUN apt-get update && apt-get install -y curl

RUN curl -L https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.5.0/mysql-connector-j-9.5.0.jar -o mysql-connector.jar

COPY . .

RUN javac -cp mysql-connector.jar -d . src/ATMSimulation.java

EXPOSE 8080

CMD ["java", "-cp", ".:mysql-connector.jar", "ATMSimulation"]
