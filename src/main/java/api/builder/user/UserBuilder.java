package api.builder.user;

import api.pojo.user.User;

public final class UserBuilder {
    private UserBuilder(){}
    private static String buildName(String firstName, String lastName) {
        return (firstName == null ? "" : firstName) + (lastName == null ? "" : (" " + lastName));
    }
    public static User buildNewUser(String firstName, String lastName, String emailAddress, String jobAsUsername) {
        String _ = buildName(firstName, lastName);
        return User.builder()
                .username(jobAsUsername)
                .firstName(firstName)
                .lastName(lastName)
                .email(emailAddress)
                .build();
    }
    public static User buildPetstoreUser(Long id, String username, String firstName, String lastName,
                                         String emailAddress, String password, String phoneNumber, Integer userStatus) {
        return User.builder()
                .id(id)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .email(emailAddress)
                .password(password)
                .phone(phoneNumber)
                .userStatus(userStatus)
                .build();
    }
}
