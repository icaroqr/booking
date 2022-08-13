# Alten Booking Api - Icaro Rezende

A Java REST Api project based on a job interview for Alten
## Getting Started

### Installing

* Install Docker: https://www.docker.com
* After setting up docker, get the database image with the command:
```
docker pull mysql:5.7
```
After pulling the image, it's time run the MySQL database in a container with the command:
```
docker run -d -p 3306:3306 --name mysqldb -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=booking mysql:5.7
```
Create a docker network to bind the application and database containers
```
docker network create booking-net
```
Connect the database to the network
```
docker network connect booking-net mysqldb
```
Now it's time to get the application image with the command:
```
docker pull icarorez/alten-booking
```
### Executing program

* After running the docker database container, run the app container redirecting to your localhost 8080 port:
```
docker run -p 8080:8080 --name booking --net booking-net icarorez/alten-booking
```

## Testing API

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