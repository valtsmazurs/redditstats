# Reddit statistics demo

## Instructions
Prerequisites:
* JDK 11
* Internet connection and some disk space

### Run the server
1. Download the pre-built JAR file from [GitHub](http://github.com)
1. Run the JAR file with: ```java -jar redditstats-1.0.jar```

OR

1. clone the Git repository
1. ```./gradlew bootRun```

The embedded HTTP server will be binded to localhost port 8080.

### REST API
Accepts HTTP GET requests. Returns JSON data if any available.

URIs:
* /activity - returns Reddit activity: how many new submissions and comments have been posted during the given time range 
* /mostActive/top100 - returns top 100 most active subreddits during the given time range; the activity is measured as sum of new submissions and
  comments together
* /mostActive/bySubmissions - returns a subreddit that has the greatest number of submissions during the given time range
* /mostActive/byComments - returns a subreddit that has the greatest number of submissions during the given time range

All REST endpoints accept parameter _timeRange_ with the available values:
* ONE_MINUTE
* FIVE_MINUTES
* ONE_HOUR
* ONE_DAY
* UNLIMITED

Default: ONE_MINUTE

E.g. ```http://localhost:8080/activity?timeRange=ONE_HOUR``` will return the total number of submissions and commends
that have been logged during the last hour.

## Architecture
The project consists of components that expose only their interfaces.
* _redditevent_ - the innermost component; deals with RedditEvent storage and retrieval; depends on
Mongodb as a storage engine and uses Mongodb for data aggregations
* _gathering_ - gathers the data from [stream.pushshift.io](http://stream.pushshift.io), uses _redditevent_ to store the received events
* _statistics_ - provides Java API for Reddit statistics, uses _redditevent_ for aggregated data retrieval
* _webservice_ - implements REST endpoints, uses _statistics_ to get the data

The architecture demonstrates dependency flow where the innermost component has the least amount of dependencies and is
not dependent on other components of this application.

### Used libraries and tools
* MongoDB - widely used, stable, well suited for this case where there is no need for transactions that would be provided
by RDBMS
* Embedded MongoDB - saves time to set up local MongoDB service 
* Spring Boot - web service creation, dependency injection, comfortable APIs for MongoDB   
* Immutables - reduces the amount of the boilerplate code, provides comfortable way how to work with immutable objects
* Apache CXF - handles stream.pushshift.io's Server Sent Events (SSE)
* For tests:
  * Junit5
  * Mockito
  * Assertj - better readable assertion statements
* Gradle - build tool of choice, supports Spring Boot plugin 

## Tests
Most of functionality (that does not involve parallelism or external resources) is covered by unit tests.