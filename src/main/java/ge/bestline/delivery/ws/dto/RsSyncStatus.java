package ge.bestline.delivery.ws.dto;

public enum RsSyncStatus {
    IsProcessing("გზავნილი მუშავდება"),
    NoBarCodeIntoComment("კომენტარში ბარკოდი ვერ მოიძებნა"),
    NoParcesFound("გზავნილი ვერ მოიძებნა");

    private String value;

    public String getValue() {
        return value;
    }

    RsSyncStatus(String value) {
        this.value = value;
    }
}
