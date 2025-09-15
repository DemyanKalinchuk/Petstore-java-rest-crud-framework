package api.pojo.pet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pet {
  private Long id;
  private Category category;
  private String name;
  private List<String> photoUrls;
  private java.util.List<Tag> tags;
  private String status;
}
