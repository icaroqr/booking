# Booking Api - Icaro Rezende

A Java REST Api for booking a room
## Getting Started

### Pré-requirements to run it locally

* Java 8
* Maven
* MySQL Server 5.7
* Git

### MySQL configuration

Install MySQL server version 5.7 with the following user and password:
```
user:root password:123456
```
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
Open this URL in your browser to check if the API is online, if it does, it has already created the database tables, and you can proceed:
```
http://localhost:8080/swagger-ui.html#/
```

### Initial DataBase data

In order to play with the API you need to manually insert Room data, connect to your local database and run the following SQL script:
```
INSERT INTO booking.hotel (id, name) VALUES (1, 'Cancun Last Resort');
INSERT INTO booking.room_details (id, max_reserve_advance_days, max_reserve_days) VALUES (1, 30, 3);
INSERT INTO booking.room (id, hotel_id, room_details_id) VALUES (1, 1, 1);
```

### Usage

You can use the follow parameters on a GET request body to the endpoint "/reservation/list":
* page = Current page of a pagination
* size = Number of elements per page
* guestEmail = Filter by the user who created the Reservation
* startDate = Filter by the start date of reservation creation
* endDate = Filter by the end date of reservation creation

### cURLs example requests

Return the first page of latest 10 reservations
```
curl --location --request GET 'localhost:8080/reservation/list' \
--header 'Content-Type: application/json' \
--data-raw '{
    "page": 0,
	"size": 10,
	"guestEmail": null,
	"startDate": null,
	"endDate": null
}'
```
Return the first page of 5 reservations, filtering by guest
```
curl --location --request GET 'localhost:8080/reservation/list' \
--header 'Content-Type: application/json' \
--data-raw '{
    "page": 0,
	"size": 5,
	"guestEmail": "guest@gmail.com",
	"startDate": null,
	"endDate": null
}'
```