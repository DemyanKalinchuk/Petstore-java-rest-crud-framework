package Tests;

import api.steps.UserSteps;
import com.github.javafaker.Faker;
import org.testng.annotations.Test;
import utils.BaseApiTest;

public class UserFlowTest extends BaseApiTest {
    private final UserSteps userSteps = new UserSteps();
    private final Faker faker = new Faker();

    @Test
    public void userCrudFlow() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String emailAddress = faker.internet().emailAddress();
        String usernameFromJob = faker.job().title().replace(' ', '_').toLowerCase();

        userSteps.createUser(firstName, lastName, emailAddress, usernameFromJob);

        Long userId = System.currentTimeMillis();
        String newPassword = faker.internet().password(8, 12);
        String phoneNumber = faker.phoneNumber().cellPhone();
        Integer userStatus = 1;
        userSteps.updateUser(usernameFromJob, userId, firstName + "_upd", lastName, emailAddress, newPassword, phoneNumber, userStatus);

        userSteps.getUser(usernameFromJob);

        userSteps.deleteUser(usernameFromJob);
    }
}
