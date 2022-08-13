# Create a container with the following image that have Java installed
FROM openjdk
# Create a directory to deploy the application
WORKDIR /alten
# Copy the builded application to the directory
COPY target/booking-0.0.1-SNAPSHOT.jar /alten/booking.jar
# Run the application
ENTRYPOINT [ "java", "-jar", "booking.jar" ]