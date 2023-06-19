package ge.bestline.delivery.ws.dto;

public enum RsWaybillStatuses {
    SAVED(0),
    ACTIVATED(1),
    CLOSED(2),
    NNOTKNOWN(8),
    NNOTKNOWN2(-2);

    private Integer value;

    RsWaybillStatuses(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
