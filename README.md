# Alten Booking Api - Icaro Rezende

A Java REST Api project based on a job interview for Alten
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
git clone 
```
Open the cloned folder with terminal and execute this command to build the application with Maven:
```
mvn package -f "pom.xml" 
```
Open the cloned folder with terminal and execute this command to run the application over localhost port 8080:
```
java '-cp' '\target\booking-0.0.1-SNAPSHOT.jar' 'com.alten.booking.BookingApplication'
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