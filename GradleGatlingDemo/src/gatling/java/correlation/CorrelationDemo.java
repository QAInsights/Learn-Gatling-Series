package correlation;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CorrelationDemo extends Simulation {
    HttpProtocolBuilder req = http
//            .proxy(Proxy("localhost", 8888))
            .baseUrl("https://bank-of-anthos.xyz");

    ScenarioBuilder homePage = scenario("Home Page")
            .exec(http("T00_Home").get("/"));

    ScenarioBuilder loginPage = scenario("Login")
            .exec(http("T10_Product").post("/login")
                    .formParam("username","testuser")
                    .formParam("password","password")
            )
            .pause(1);

    Random rdm = new Random();
    Integer amountToTransfer = rdm.nextInt(1000) + 1;

    ScenarioBuilder transferFunds = scenario("Add To Cart")
            .exec(http("T20_TransferFunds").post("/payment")
                    .formParam("account_num","9879879870")
                    .formParam("contact_account_num","")
                    .formParam("contact_label","")
                    .formParam("amount",amountToTransfer)
                    .formParam("uuid",UUID.randomUUID())
                    .check(
                            status().is(200),
                            substring("Payment successful"),
                            regex("<span class=\\\"(.*)\\\" id=\\\"current-balance\\\">((.|\\n)+?)<\\/span>").captureGroups(2).saveAs("currentBalance")
                    )
            )
            .exec(session -> {
                String getBalance = session.getList("currentBalance").get(1).toString().trim();
                getBalance = getBalance.substring(1);

                BigDecimal money = new BigDecimal(getBalance.replaceAll(",", ""));
                System.out.println("Money" + money);

                return session;

            })
            .pause(1);



    ScenarioBuilder allTransaction = scenario("Online")
            .exec(homePage, loginPage, transferFunds);

    {
        setUp(
            allTransaction.injectOpen(atOnceUsers(1))
        ).protocols(req);
    }

}