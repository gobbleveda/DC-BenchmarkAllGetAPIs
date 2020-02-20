package test.scala

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import scala.concurrent.duration._



class BenchmarkAllGetAPIs extends Simulation {

  // 1 configure http base url

  val httpProtocol = http.baseUrl("http://host.docker.internal:5000")
    .header("Content-Type", "application/json")

  //val csvFeeder = csv("/Users/amanmisra/Projects/Tutorials/PerformanceEngineering/gatling/DCGetAPIs/src/test/resources/data/new-user-registration-devcamp.csv").circular
  val csvFeeder = csv("data/new-user-registration-devcamp.csv").circular
 // 2 define functions

  def authenticateUser(): ChainBuilder = {
      feed(csvFeeder)
        .exec(http("Authenticate User")
        .post("/api/v1/auth/login")
        .check(jsonPath("$.success").is("true"))
        .check(status.in(200, 210))
        .check(jsonPath("$.success").saveAs("token"))
//        .body(StringBody("""{"email": "${email}", "password": "${password}"}""")).asJson)
          .check(bodyString.saveAs("responseBody"))
          .body(ElFileBody("bodies/UserTemplate.json")).asJson)
//        .exec{session => println(session("responseBody").as[String]); session}
  }

  // get all users


  def getAllUsers() = {
 //   repeat(1) {
      exec(http("Get All Users")
        .get("/api/v1/users")
        .header("Cookie","token=${token}")
        .check(status.in(200, 210))
        .check(jsonPath("$.success").is("true"))
        .check(jsonPath("$.success").is("true"))
        .check(bodyString.saveAs("responseBody"))
      )
 //   }
  }

  // get all courses

  def getAllCourses() = {
      exec(http("Get All Courses")
      .get("/api/v1/courses")
      .header("Cookie","token=${token}")
      .check(status.in(200, 210))
      .check(jsonPath("$.success").is("true"))
      .check(jsonPath("$.success").is("true"))
  //    .check(bodyString.saveAs("responseBody"))
    )
  }

  // Get all bootcamps

  def getAllBootCamps() = {
      exec(http("Get All BootCamps")
      .get("/api/v1/bootcamps")
      .header("Cookie","token=${token}")
      .check(status.in(200, 210))
      .check(jsonPath("$.success").is("true"))
      .check(bodyString.saveAs("responseBody"))
    )
  }

  // Get All Users
  def getAllReviews() = {
     exec(http("Get All Reviews")
      .get("/api/v1/reviews/")
      .header("Cookie","token=${token}")
      .check(status.in(200, 210))
      .check(jsonPath("$.success").is("true"))
      .check(bodyString.saveAs("responseBody"))
    )
  }


  // 2 Define scenario with code reuse

  val scn = scenario("Benchmark all Get APIs")
    .exec(authenticateUser())
    .pause(1,3)
    .exec(getAllUsers())
    .pause(1,3)
    .exec(getAllCourses())
    .pause(1,3)
    .exec(getAllBootCamps())
    .pause(1,3)
    .exec(getAllReviews())
    .pause(1,3)

  // set up scenario execution
  setUp(
    scn.inject(
      nothingFor(2),
      atOnceUsers(5),
      rampUsers(1000) during (120 seconds)
    ).protocols(httpProtocol)
  )

}
