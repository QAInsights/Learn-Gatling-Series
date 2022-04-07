package checks;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ChecksDemo extends Simulation {

    HttpProtocolBuilder req = http
            .baseUrl("https://onlineboutique.dev");

    ScenarioBuilder myscenario = scenario("Online Boutique")
            .exec(http("T00_Home")
                    .get("/")
                    .check(
                            status().is(200),
                            status().in(200, 202), // Gatling will perform implicit check for 2XX or 304
                            currentLocation().saveAs("url"),
                            responseTimeInMillis().lte(3000),
                            header("DNT").is("1"),
                            regex("<title>((\n|.)*?)<").saveAs("extract_title"),
                            regex("class=\\\"(.+?)\\\">(.*)<").captureGroups(2).findAll().saveAs("extract_products") // supports b/w 2 and 8
                    ))
            .exec(session -> {
                System.out.println(session.getString("url"));
                System.out.println(session.getString("extract_title"));
                System.out.println(session.getString("extract_products"));
                return session;
            })
            .pause(3);



    {
        setUp(myscenario.injectOpen(atOnceUsers(1)).protocols(req));
    }
}
