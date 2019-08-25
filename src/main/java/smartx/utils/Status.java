package smartx.utils;


public enum Status {
    AVAILABLE("AVAILABLE"), UNAVAILABLE("UNAVAILABLE"), CONFIGURATION_PENDING("CONFIGURATION PENDING"), 
    NOT_REGISTERED("NOT REGISTERED"), VIN_MISMATCH("VIN MISMATCH");
    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
