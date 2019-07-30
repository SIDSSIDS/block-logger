# Block-logger
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.sidssids/block-logger/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.sidssids/block-logger)
[![License](https://img.shields.io/github/license/sidssids/block-logger.svg)](https://opensource.org/licenses/MIT)

Block-logger is an extention for [logback](https://logback.qos.ch) logging framework that allows the developer to use blocks of logs. For example:
```
2018-04-25 11:24:01,173 [main      ] INFO  [+] Change name (personId=100 newName=Max)
2018-04-25 11:24:01,173 [main      ] INFO      Retrieve person from DB: {id=100, name=Alex}
2018-04-25 11:24:01,276 [main      ] INFO      Set new name: Max
2018-04-25 11:24:01,277 [main      ] INFO      [+] Store data ({id=100, name=Max})
2018-04-25 11:24:01,277 [main      ] DEBUG         Connect to DB
2018-04-25 11:24:01,277 [main      ] DEBUG         Updating entity
2018-04-25 11:24:01,384 [main      ] INFO      [-] Store data (PT-0.101S): saved
2018-04-25 11:24:01,384 [main      ] INFO  [-] Change name (PT-0.217S)
```
## Usage example
Example of using `LogBlock` in couple with standard `org.slf4j.Logger`:
```java
public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(Main.class);               // slf4 logger

    logger.info("outside block");

    try (LogBlock log = LogBlockFactory.info(Main.class, "block", "param=%s", "value")) { // start block
        logger.info("inside block");                                   // use slf4 logger inside the block
    }                                                                  // LogBlock is autoclosable
}
```
The output:
```
2018-04-25 13:04:54,631 [main      ] INFO  outside block
2018-04-25 13:04:54,682 [main      ] INFO  [+] block (param=value)
2018-04-25 13:04:54,682 [main      ] INFO      inside block
2018-04-25 13:04:54,686 [main      ] INFO  [-] block (PT0.004S)
```
Refer [wiki](https://github.com/SIDSSIDS/block-logger/wiki) for details and configuration

| Stable Release Version | JDK Version compatibility | Release Date |
| ------------- | ------------- | ------------|
| 1.1.0  | 1.8+ | 2018-07-30 |
| 1.0.0  | 1.8+ | 2018-06-27 |

## Maven Repository
You can pull block-logger from the central maven repository, just add these to your `pom.xml` file:
```xml
<dependency>
  <groupId>com.github.sidssids</groupId>
  <artifactId>block-logger</artifactId>
  <version>1.1.0</version>
</dependency>
```
