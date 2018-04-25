Block-logger
============
Block-logger is an extention for [logback](https://logback.qos.ch) logging framework that allows the developer to use blocks of logs. For example:
```
2018-04-25 11:24:01,173 [main      ] INFO  [+] Change name (personId=100 newName=Max): Started...
2018-04-25 11:24:01,173 [main      ] INFO      Retrieve person from DB: {id=100, name=Alex}
2018-04-25 11:24:01,276 [main      ] INFO      Set new name: Max
2018-04-25 11:24:01,277 [main      ] INFO      [+] Store data ({id=100, name=Max}): Started...
2018-04-25 11:24:01,277 [main      ] DEBUG         Connect to DB
2018-04-25 11:24:01,277 [main      ] DEBUG         Updating entity
2018-04-25 11:24:01,384 [main      ] INFO      [-] Store data (PT-0.101S): Result - saved
2018-04-25 11:24:01,384 [main      ] INFO  [-] Change name (PT-0.217S)
```
See [wiki](https://github.com/SIDSSIDS/block-logger/wiki) for usage
