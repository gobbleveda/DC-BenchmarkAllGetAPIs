package test.scala

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import scala.concurrent.duration._
import io.gatling.core.structure.ChainBuilder

class GetSingleBootcamp extends Simulation {

  // http configuration

  val httpProtocol = http.baseUrl("http://host.docker.internal:5000")

  // define functions

  def getSingleBootcamp() = {
    exec(
      http("Get Single Bootcamp")
      .get("/api/v1/bootcamps/5d725a1b7b292f5f8ceff788")
      .check(jsonPath("$.success").is("true"))
      .check(status.is(200))
    )
  }

  // define scenario

  val scn = scenario("Get Single Bootcamp")
    .exec(getSingleBootcamp())
    .pause(3, 5)


  // define rampup scenario

  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers((5)),
      rampUsers(100) during( 120 seconds)
    ).protocols(httpProtocol)

  )

}
