package ge.bestline.delivery.ws.dto;

public enum UserRoles {
    ADMIN("ADMIN"),
    COURIER("COURIER"),
    OFFICE("OFFICE"),
    OPERATOR("OPERATOR"),
    MANAGER("MANAGER"),
    DRIVER("DRIVER"),
    CUSTOMER("CUSTOMER");

    private String value;

    public String getValue() {
        return value;
    }

    UserRoles(String value) {
        this.value = value;
    }
}
