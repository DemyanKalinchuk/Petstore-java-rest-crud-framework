package utils.enums;

import lombok.Getter;
import utils.request.path.IPath;

@Getter
public enum ApiPath implements IPath {
    PET("/pet", "Create/Update Pet"),
    PET_ID("/pet/%s", "Pet by ID"),
    PET_UPLOAD_IMAGE("/pet/%s/uploadImage", "Upload Pet Image"),
    STORE_ORDER("/store/order", "Place Order"),
    STORE_ORDER_ID("/store/order/%s", "Order by ID"),
    STORE_INVENTORY("/store/inventory", "Inventory"),
    USER("/user", "Create User"),
    USER_CREATE_WITH_ARRAY("/user/createWithArray", "Create Users (array)"),
    USER_CREATE_WITH_LIST("/user/createWithList", "Create Users (list)"),
    USER_USERNAME("/user/%s", "User by Username"),
    USER_LOGIN("/user/login", "User Login"),
    USER_LOGOUT("/user/logout", "User Logout");

    private final String url;
    private final String description;

    ApiPath(String url, String description) {
        this.url = url; this.description = description;
    }
    @Override public String url() { return url; }
    @Override public String getDescription() { return description; }
}
