package feeders;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.*;

public class FeedersDemo extends Simulation {
    HttpProtocolBuilder req = http
//            .proxy(Proxy("localhost", 8888))
            .baseUrl("https://onlineboutique.dev");

    ScenarioBuilder myscenario = scenario("Home Page")
            .exec(http("T00_Home").get("/"));

    ScenarioBuilder productPage = scenario("Product Page")
            .feed(csv("data/products.csv").eager().random())
                .exec(http("T10_Product").get("/product/#{productName}"))
            .pause(1);

    ScenarioBuilder addToCart = scenario("Add To Cart")
            .exec(http("T20_AddToCart_#{productName}").post("/cart")
                            .formParam("product_id","#{productName}")
                            .formParam("quantity","#{productQuantity}"))
            .pause(1);

    ScenarioBuilder checkOut = scenario("Checkout")
            .exec(http("T30_CheckOut_#{productName}").post("/cart/checkout")
                            .formParam("email", "someone@example.com")
                            .formParam("street_address", "1600 Amphitheatre Parkway")
                            .formParam("zip_code", "94043")
                            .formParam("city", "Mountain View")
                            .formParam("state", "CA")
                            .formParam("country", "United States")
                            .formParam("credit_card_number", "4432-8015-6152-0454")
                            .formParam("credit_card_expiration_month", "1")
                            .formParam("credit_card_expiration_year", "2023")
                            .formParam("credit_card_cvv", "672")
                    .check(
                            substring("Your order is complete!"),
                            status().is(200)
                    )
            )
            .pause(1);

    ScenarioBuilder allTransaction = scenario("Online")
            .exec(myscenario, productPage, addToCart, checkOut);

    {
        setUp(
//                myscenario.injectOpen(atOnceUsers(1)),
//                productPage.injectOpen(atOnceUsers(1)),
//                addToCart.injectOpen(atOnceUsers(1)),
//                checkOut.injectOpen(atOnceUsers(1)),
                  allTransaction.injectOpen(atOnceUsers(3))

        ).protocols(req);
    }

}

