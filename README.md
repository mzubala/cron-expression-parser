# Requirements

Java Development Kit 11 or higher.

# How to build and run

```
./gradlew build
java -jar build/libs/cron-expressions-parser-1.0-SNAPSHOT.jar "MINUTE_EXPRESSION HOUR_EXPRESSION DAY_OF_MONTH_EXPRESSION MONTH_EXPRESSION DAY_OF_WEEK_EXPRESSION COMMAND"
```

For example:

```
java -jar build/libs/cron-expressions-parser-1.0-SNAPSHOT.jar "1/2 */2 3,4,5 4-8 1 /some/command"
```