package smokeTests.users;

import api.steps.UserSteps;
import com.github.javafaker.Faker;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.BaseApiTest;
import utils.enums.HttpStatusCode;

import static core.TestStepLogger.logStep;

public class UserFlowTest extends BaseApiTest {
    private final UserSteps userSteps = new UserSteps();
    private final Faker faker = new Faker();

    @DataProvider(name = "userProfiles")
    public Object[][] userProfiles() {
        return new Object[][]{
                { "qa_engineer" },
                { "product_manager" },
                { "test_architect" }
        };
    }

    @DataProvider(name = "unknownUsernames")
    public Object[][] unknownUsernames() {
        long suffix = System.currentTimeMillis();
        return new Object[][] {
                { "nonexistent_" + suffix + "_a" },
                { "nonexistent_" + suffix + "_b" },
                { "nonexistent_" + suffix + "_c" }
        };
    }

    @Test(dataProvider = "userProfiles")
    public void userCrudFlowWithProfiles(String roleSlug) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String emailAddress = faker.internet().emailAddress();
        String usernameFromJob = (roleSlug + "_" + faker.number().randomDigitNotZero()).toLowerCase();


        logStep("Create user (initial create without password)");
        userSteps.createUser(firstName, lastName, emailAddress, usernameFromJob);

        Long userId = System.currentTimeMillis();
        String newPassword = faker.internet().password(8, 12);
        String phoneNumber = faker.phoneNumber().cellPhone();
        Integer userStatus = 1;

        logStep("Update user to set password and complete profile");
        userSteps.updateUser(usernameFromJob, userId, firstName + "_upd", lastName,
                emailAddress, newPassword, phoneNumber, userStatus);

        logStep("Get created user");
        userSteps.getUser(usernameFromJob);

        logStep("Delete created user");
        userSteps.deleteUser(usernameFromJob);
    }

    @Test(dataProvider = "unknownUsernames")
    public void userGetShouldBeNotFound(String unknownUsername) {
        userSteps.getUserExpectingStatus(unknownUsername, HttpStatusCode.NOT_FOUND);
    }

    @Test(dataProvider = "userProfiles")
    public void loginLogoutFlow(String roleSlug) {
        // generate test data
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String emailAddress = faker.internet().emailAddress();
        String usernameFromRole = (roleSlug + "_" + faker.number().numberBetween(1000, 9999)).toLowerCase();
        String password = faker.internet().password(8, 12);
        String phoneNumber = faker.phoneNumber().cellPhone();
        Long userId = System.currentTimeMillis();
        Integer userStatus = 1;

        logStep("Create user (initial create without password)");
        userSteps.createUser(firstName, lastName, emailAddress, usernameFromRole);

        logStep("Update user to set password and complete profile");
        userSteps.updateUser(usernameFromRole, userId, firstName, lastName, emailAddress, password, phoneNumber, userStatus);

        logStep("Login with username/password via query params");
        userSteps.login(usernameFromRole, password);

        logStep("Logout current session");
        userSteps.logout();

        logStep("Delete user at the end");
        userSteps.deleteUser(usernameFromRole);
    }

    @Test
    public void userCrudFlow() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String emailAddress = faker.internet().emailAddress();
        String usernameFromJob = faker.job().title().replace(' ', '_').toLowerCase();

        logStep("Create user (initial create without password)");
        userSteps.createUser(firstName, lastName, emailAddress, usernameFromJob);

        Long userId = System.currentTimeMillis();
        String newPassword = faker.internet().password(8, 12);
        String phoneNumber = faker.phoneNumber().cellPhone();
        Integer userStatus = 1;

        logStep("Update user to set password and complete profile");
        userSteps.updateUser(usernameFromJob, userId, firstName + "_upd", lastName, emailAddress, newPassword, phoneNumber, userStatus);

        logStep("Get current user");
        userSteps.getUser(usernameFromJob);

        logStep("Delete current user");
        userSteps.deleteUser(usernameFromJob);
    }
}
