package api.pojo.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
  private final Long id;
  private final String username;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String password;
  private final String phone;
  private final Integer userStatus;
}
