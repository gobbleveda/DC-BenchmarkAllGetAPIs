package test.scala

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import scala.concurrent.duration._
import io.gatling.core.structure.ChainBuilder

class NewRegistrations extends Simulation {

  // http configuration

  val httpProtocol = http.baseUrl("http://nodejs:5000")
                         .header("Content-Type","application/json")

  val csvFeeder = csv("data/new-user-registration-devcamp.csv").queue


  // define function for new registration

  def registerNewUser() = {
    feed(csvFeeder)
      .exec(
        http("Register New User")
          .post("/api/v1/auth/register")
          .check(jsonPath("$.success").is("true"))
          .check(status.in(200,204))
          .check(jsonPath("$.token").saveAs("token"))
          .check(bodyString.saveAs("responseBody"))
          .body(ElFileBody("bodies/RegisterUser.json")).asJson)
      .exec{session => println(session("responseBody").as[String]); session}
  }

  // scenario defintion

  val scn = scenario ("Register New Users")
              .repeat(20)(
                    exec(registerNewUser())
                      .pause(3, 5))

  // Ramp-up defintion

  setUp(
    scn.inject(
      nothingFor(5),
    //  atOnceUsers(10),
      rampUsers(50) during(300 seconds)
    ).protocols(httpProtocol)
  )


}
