package assertions;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class AssertionsDemo extends Simulation {

    HttpProtocolBuilder req = http
            .baseUrl("https://onlineboutique.dev");

    ScenarioBuilder myscenario = scenario("Online Boutique")
            .group("OnlineBoutique").on(
                    exec(http("T00_Home")
                            .get("/")
                            .check(
                                    responseTimeInMillis().lte(3000)
                    ))
                            .exec(http("T10_ProductPage")
                                    .get("/product/OLJCESPC7Z")
                                    .check(
                                            status().not(500)
                                    ))
                            .pause(3)
            );

    {
        setUp(myscenario.injectOpen(atOnceUsers(3))
                .protocols(req))
                .assertions(global().responseTime().percentile3().lte(1000))    // for all requests - percentile3 => 95 - ms
                .assertions(forAll().responseTime().max().lte(1500))            // stats for individual requests - max response time in ms
                .assertions(forAll().failedRequests().percent().lte(0.01))       // for all failed requests percentage < 0.01%
                .assertions(details("OnlineBoutique").responseTime().mean().lte(800)   // group - response time mean <800 ms
        );

    }
}
