bayes-dota
==========

This is the [task](TASK.md).

I implemented scenarios requested within 2 hours.

Instructions
To compile and run this project you will need:

minimum Java 8 (JDK8)
Maven
To start the application use the command bellow

mvn spring-boot:run
To see paths within swagger you need to open after application startup: http://localhost:8080/swagger-ui.html

Application port :8080

To run all unit and integration tests use the command bellow
mvn test

Formatter
I used Google Format for code formatting and also used Sonarlint.

* I would implement file upload as additional request for adding log info into database using file.
Because for huge files would be better to have file upload because we can use file nio channels where we can create certain file descriptors and using these file descriptors we can split huge files into several parts then we can independently process those parts.
* I also implemented possibilty to process simutaneously with several threads, there is a check for existance of hero: if hero already registered in the system then don't use locking(Please see getHero(long matchId, String heroName) within ParserServiceImpl.java).
* I could implement aggregation logic within incoming part.The downside of this approach to limit our processing logic and this approach is not flexible for future changes. But I inserted info to database and all aggregation logic implemented on api query side.
* Having appropriate time I would also change some logging occurrence to throw UnexpectedLogEntryException