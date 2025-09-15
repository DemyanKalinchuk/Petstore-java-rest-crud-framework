package utils;

import config.Config;
import io.qameta.allure.testng.AllureTestNg;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

@Listeners({AllureTestNg.class})
public abstract class BaseApiTest {

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        RestAssured.baseURI = Config.baseApiUrl();
        RestAssured.useRelaxedHTTPSValidation();
    }
}
