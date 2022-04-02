//package computerdatabase; // 1

// 2
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

class E1Gatling extends Simulation { // 3

    HttpProtocolBuilder httpProtocol = http // 4
            .baseUrl("http://computer-database.gatling.io") // 5
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // 6
            .doNotTrackHeader("1")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");

    ScenarioBuilder scn = scenario("BasicSimulation") // 7
            .exec(http("request_1") // 8
                    .get("/")) // 9
            .pause(5); // 10

    {
        setUp( // 11
                scn.injectOpen(atOnceUsers(1)) // 12
        ).protocols(httpProtocol); // 13
    }
}