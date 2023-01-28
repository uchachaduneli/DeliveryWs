package ge.bestline.delivery.ws.dto;

public enum InvoicePaymentStatus {
    UNPAYED("UNPAYED"),
    PARTIALLY_PAID("PARTIALLY PAID"),
    PAYED("PAYED");

    private String status;

    InvoicePaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
