package correlation;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.math.BigDecimal;
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
            .exec(http("T10_LoggedIn").post("/login")
                    .formParam("username","testuser")
                    .formParam("password","password")
                    .check(
                            regex("<span class=\\\"(.*)\\\" id=\\\"current-balance\\\">((.|\\n)+?)<\\/span>").captureGroups(2).saveAs("beforeTransferBalance")
                    )
            )
            .pause(1);
    Random rdm = new Random();
    Integer amountToTransfer = rdm.nextInt(1000) + 1;

    ScenarioBuilder transferFunds = scenario("Transfer Funds")
            .exec(
                    session -> {
                        // Check the balance
                        String balanceCheck = session.getList("beforeTransferBalance").get(1).toString().trim();
                        balanceCheck = balanceCheck.substring(1);

                        BigDecimal balanceCheckFormat = new BigDecimal(balanceCheck.replaceAll(",", ""));
                        Integer res = balanceCheckFormat.compareTo(BigDecimal.valueOf(1000));

                        if (res <= 0) {
                            System.out.println("Not enough balance.");
                        }
                        else {
                            System.out.println("Proceeding to transfer.");
                        }
                        return session;
                    }
            )

            .exec(http("T20_TransferFunds").post("/payment")
                    .formParam("account_num","9879879870")
                    .formParam("contact_account_num","")
                    .formParam("contact_label","")
                    .formParam("amount", amountToTransfer)
                    .formParam("uuid",UUID.randomUUID())
                    .check(
                            status().is(200),
                            substring("Payment successful"),
                            regex("<span class=\\\"(.*)\\\" id=\\\"current-balance\\\">((.|\\n)+?)<\\/span>").captureGroups(2).saveAs("afterBalance")
                    ))
            .exec(session -> {
                String afterBalance = session.getList("afterBalance").get(1).toString().trim();
                afterBalance = afterBalance.substring(1);

                BigDecimal afterBalanceFormat = new BigDecimal(afterBalance.replaceAll(",", ""));

                String beforeTransfer = session.getList("beforeTransferBalance").get(1).toString().trim();
                beforeTransfer = beforeTransfer.substring(1);

                BigDecimal beforeTransferFormat = new BigDecimal(beforeTransfer.replaceAll(",", ""));

                // Finding whether the balance is matching or not, after transferring
                if (beforeTransferFormat.subtract(BigDecimal.valueOf(amountToTransfer)).equals(afterBalanceFormat)) {
                    System.out.println("Balance is matching.");
                }
                else {
                    System.out.println("Balance is not matching");
                }
                return session;

            })

            .pause(1);

    ScenarioBuilder logout = scenario("logout")
            .exec(http("T30_LogOut").post("/logout")
            )
            .pause(1);

    ScenarioBuilder allTransactions = scenario("BankOfAnthos")
            .exec(homePage, loginPage, transferFunds,logout);
    {
        setUp(
            allTransactions.injectOpen(atOnceUsers(1))
        ).protocols(req);
    }

}