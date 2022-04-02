package demo;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class HelloWorld extends Simulation {

    HttpProtocolBuilder req = http
            .baseUrl("https://example.com");

    ScenarioBuilder myscenario = scenario("HelloWorld")
            .exec(http("T00_Home")
                    .get("/")
                    .check(
                        status().is(200),
                        status().not(500)
                    ))
            .pause(3);

    {
        setUp(myscenario.injectOpen(atOnceUsers(1)).protocols(req));
    }

}
