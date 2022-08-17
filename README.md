# Booking Api - Icaro Rezende

A Java REST Api for booking a room
## Getting Started

### Pré-requirements to run it locally

* Java 8
* Maven
* MySQL Server 5.7
* Git

### MySQL Database configuration

Install MySQL server version 5.7 with the default user root and password: 123456, or change the application.properties at your convenience.

Connect into your local database and create an schema with the name: booking

### Running the API

Clone this repository with the following command:
```
git clone https://github.com/icaroqr/booking.git
```
Open the cloned folder with terminal and execute this command to build and run the application with Maven:
```
mvn spring-boot:run
```
Open this URL in your browser to check if the API is running, if it does, it has already created the database tables, and you can proceed:
```
http://localhost:8080/swagger-ui/#/
```

### Initial DataBase data

In order to play with the API you need to manually insert Room data, connect to your local database and run the following SQL script:
```
INSERT INTO booking.hotel (id, name) VALUES (1, 'Cancun Last Resort');
INSERT INTO booking.room_details (id, max_reserve_advance_days, max_reserve_days) VALUES (1, 30, 3);
INSERT INTO booking.room (id, hotel_id, room_details_id) VALUES (1, 1, 1);
```

## API Usage

This API is deployed on a limited free Heroku environment for testing pourpose, you can access it through this URL: https://alten-booking.herokuapp.com/swagger-ui/#/
The API use ISO local date format, and the accepted reservation status are: RESERVED and CANCELED. The API client should use the endpoints in this order to have a better booking experience:

### Get available dates for the room
```
GET endpoint: https://alten-booking.herokuapp.com/room/1/availableDates
```
### Check dates availability whenever you want
```
POST endpoint: https://alten-booking.herokuapp.com/room/1/available
Payload:
{
  "startDate": "2022-08-25",
  "endDate": "2022-08-28"
}
```
### Create a reservation
```
POST endpoint: https://alten-booking.herokuapp.com/reservation
Payload:
{
  "guestEmail":"guest@gmail.com",
  "startDate":"2022-08-25",
  "endDate":"2022-08-28",
  "roomId": 1
}
```
### List the guest reservations
```
POST endpoint: https://alten-booking.herokuapp.com/reservation/list
Payload:
{
  "page":0,
  "size":5,
  "guestEmail":"guest@gmail.com",
  "roomId": 1,
  "startDate":"2022-08-15",
  "endDate":"2022-08-30"
}
```
Fields description
* page = Current page of a pagination
* size = Number of elements per page
* guestEmail = Filter by the user who created the Reservation
* startDate = Filter from when the user have reservations starting
* endDate = Filter until when the user have reservations starting
### Get an specific reservation
```
GET endpoint: https://alten-booking.herokuapp.com/reservation/1
```
### Update a reservation
```
PUT endpoint: https://alten-booking.herokuapp.com/reservation/1
Payload:
{
  "guestEmail":"guest@gmail.com",
  "status":"RESERVED",
  "startDate":"2022-08-27",
  "endDate":"2022-08-30",
  "roomId": "1"
}
```
### Cancel a reservation
```
DELETE endpoint: https://alten-booking.herokuapp.com/reservation/1
Payload:
{
  "guestEmail":"guest@gmail.com",
}
```
## Next steps
In order to keep the quality of service with no downtime, we should queue the reservations requests through messaging systems like RabbitMQ or Kafka.
This could be done spliting this project into 3. The first with the common classes used between the services, the second with the ReservationRequest validation and queueing where we can create a message producer that will connect to a RabbitMQ instance and send messages to an specific queue, and the third one with a listener to this specific queue which will be responsible to persist the reservation and send some notification such as an email or receipt to the guest email. Separating the responsabilities in this way we will not lose any request and will be able to escalate resources on demand to each microservice.

After garantee the "no downtime" requirement, the next steps could be secure the API requests with authentication and authorization, using Spring Security and JWT, and finish the Unit tests to cover all validations and operations like updating, checking and canceling reservations. 
