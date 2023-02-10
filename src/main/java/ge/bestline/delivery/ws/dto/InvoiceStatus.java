package ge.bestline.delivery.ws.dto;

public enum InvoiceStatus {
    CREATED("CREATED"),
    SENT("SENT"),
    SENT_FAILED("SENT FAILED"),

    REMOVED("REMOVED");

    private String status;

    InvoiceStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
