## Car advertisements service
Service exposes REST endpoint with CRUD operations on car advertisements.

**Structure of car advert JSON:**
 * **id** (_required_): **int**
 * **title** (_required_): **string**, e.g. _"Audi A4 Avant"_;
 * **fuel** (_required_): gasoline or diesel
 * **price** (_required_): **integer**;
 * **new** (_required_): **boolean**, indicates if car is new or
 used;
 * **mileage** (_only for used cars_): **integer**;
 * **first registration** (_only for used cars_): **date** without
 time.

## Api:
* GET _/adverts_ - list all available car adverts
* GET _/adverts/{id}_ - find car advert by id
* POST _/adverts_ - create car advert
* PUT _/adverts_ - replace existing car advert
* DELETE _/adverts/{id}_ - remove car advert

## Installation
Requirements:
* java 8
* sbt
* MongoDb server
* docker and docker-compose (if launched in docker container)

Configuration is done through environment variables.

For http endpoint:
* `HTTP_HOST` - default: 0.0.0.0
* `HTTP_PORT` - default: 8080

For MongoDb connection:
* `DB_HOST` - default: localhost
* `DB_PORT` - default: 27017
* `DB_NAME` - database name, default: db

To run service using sbt type:
```
sbt run
```
To start service and MongoDb in docker containers, build image with sbt and start containers with docker-compose:
```
sbt docker:publishLocal
cd docker
docker-compose up -d
```