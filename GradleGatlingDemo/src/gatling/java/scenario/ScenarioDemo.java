package scenario;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ScenarioDemo extends Simulation {

    HttpProtocolBuilder req = http
            .baseUrl("https://onlineboutique.dev");

    ScenarioBuilder myscenario = scenario("OnlineBoutique Home Page")        // Scenario Creation
            .exec(http("T00_Home").get("/"));                                // Attaching methods

    ScenarioBuilder multipleRequests = scenario("MultipleRequests")
            .exec(http("T10_Product").get("/product/OLJCESPC7Z"))            // Multiple methods
            .exec(http("T20_Checkout").get("/checkout"))
            .pause(3);

    ChainBuilder contact = exec(http("T30_Contact").get("/contact"))            // Composable requests
            .pause(3);

    ChainBuilder continueShopping = exec(http("T40_ContinueShopping").get("/"))    // Composable requests
            .pause(3);

    ScenarioBuilder contactAndContinueScenario = scenario("Contact and Continue Shopping") // Scenario  for composable reqs
            .exec(contact, continueShopping);

    {
        setUp(
                myscenario.injectOpen(atOnceUsers(1)),
                multipleRequests.injectOpen(atOnceUsers(1)),
                contactAndContinueScenario.injectOpen(atOnceUsers(1))
        ).protocols(req);
    }

}
