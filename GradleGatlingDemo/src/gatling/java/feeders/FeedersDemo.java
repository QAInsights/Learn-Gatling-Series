package feeders;

import io.gatling.core.config.HttpConfiguration;
import io.gatling.http.protocol.HttpProtocol;
import io.gatling.http.protocol.Proxy;
import io.gatling.http.protocol.ProxyType;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;

public class FeedersDemo extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .proxy(
                    new Proxy("localhost", 8888)
            )
            .noProxyFor("www.github.com")
            .baseUrl("https://onlineboutique.dev");

    ScenarioBuilder myscenario = scenario("OnlineBoutique Home Page")
            .exec(http("T00_Home").get("/"));

    ScenarioBuilder productPage = scenario("productPage")
            .feed(csv("data/products.csv").circular())
                .exec(http("T10_Product").get("/product/${productName}"))
            .pause(1);

    ScenarioBuilder addToCart = scenario("addToCart")
            .exec(http("T20_AddToCart").post("/cart")
                    .formParam("product_id","${productName}")
                    .formParam("quantity","${productQuantity}"))
            .pause(1);

    ScenarioBuilder checkOut = scenario("checkOut")
            .exec(http("T30_CheckOut").post("/cart/checkout")
                    .formParam("email", "someone@example.com")
                    .formParam("street_address", "1600 Amphitheatre Parkway")
                    .formParam("zip_code", "94043")
                    .formParam("city", "Mountain View")
                    .formParam("state", "CA")
                    .formParam("country", "United States")
                    .formParam("credit_card_number", "4432-8015-6152-0454")
                    .formParam("credit_card_expiration_month", "1")
                    .formParam("credit_card_expiration_year", "2023")
                    .formParam("credit_card_cvv", "672"))
            .pause(1);




    {
        setUp(
                myscenario.injectOpen(atOnceUsers(1)),
                productPage.injectOpen(atOnceUsers(1)),
                addToCart.injectOpen(atOnceUsers(1)),
                checkOut.injectOpen(atOnceUsers(1))
        ).protocols(req);
    }


}

