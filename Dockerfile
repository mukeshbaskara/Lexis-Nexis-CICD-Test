FROM openjdk:8-jdk-alpine

# Set working directory
WORKDIR /app

# Copy application files
COPY target/app-1.0.jar /app/app.jar

# Expose the port
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "/app/app.jar"]