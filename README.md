##API_Test_Stampyt
Repository Development test API for Stampyt
 
##Development process
###Build the project

In first if you don't have sbt please download here https://www.scala-sbt.org/

And after you have pull the projects. In your terminale you can run :
```
sbt compile
```
### Run application
```
sbt run
```
You can access to the application in your browser with the url : http://localhost:9000

###Swagger-Ui
You can access the swagger-ui with the route : http://localhost:9000/docs/

### Formatting
Run `sbt scalafmt test:scalafmt scalafmtSbt`

## Structure

* [app](https://github.com/ayoann/API_Test_Stampyt/tree/master/app) contains controllers, DAO use for the project.
* [project](https://github.com/ayoann/API_Test_Stampyt/tree/master/project) contains the project's sbt build definition.
* [conf](https://github.com/ayoann/API_Test_Stampyt/tree/master/conf) contains application.conf and routes for the application
* [public](https://github.com/ayoann/API_Test_Stampyt/tree/master/public/swagger) contains the swagger-ui