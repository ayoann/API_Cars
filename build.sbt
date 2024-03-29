name := "API_Stampyt"
 
version := "1.0" 
      
lazy val `api_stampyt` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.11.4"

libraryDependencies ++= Seq(ehcache,
                            ws,
                            specs2 % Test,
                            guice,
                            "mysql" % "mysql-connector-java" % "5.1.34",
                            "org.postgresql" % "postgresql" % "9.4.1209",
                            "io.swagger" %% "swagger-play2" % "1.7.1",
                            "com.typesafe.play" %% "play-slick" % "4.0.2",
                            "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2")


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      