package utils.base;

import config.Config;
import io.qameta.allure.testng.AllureTestNg;
import io.restassured.RestAssured;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import static core.TestStepLogger.*;

@Listeners({AllureTestNg.class})
public abstract class BaseApiTest {

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        logPreConditionStep("Base issues validation");
        RestAssured.baseURI = Config.baseApiUrl();
        RestAssured.useRelaxedHTTPSValidation();
    }

    @AfterMethod(alwaysRun = true)
    public void resetSteps() {
        log("Reset step counters");
        resetCounters();
    }
}
