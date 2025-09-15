package api.builder.pet;

import api.pojo.pet.Category;
import api.pojo.pet.Pet;
import api.pojo.pet.Tag;

import java.util.List;

public final class PetBuilder {
    private PetBuilder(){}
    public static Pet buildNewPet(Long id, Category category, String name,
                                  List<String> photoUrls, List<Tag> tags, String status) {
        return Pet.builder()
                .id(id)
                .category(category)
                .name(name)
                .photoUrls(photoUrls)
                .tags(tags)
                .status(status)
                .build();
    }
}
