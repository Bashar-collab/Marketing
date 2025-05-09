# Use Java 21 base image
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file into the container
COPY target/marketing-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app uses (usually 8080)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
