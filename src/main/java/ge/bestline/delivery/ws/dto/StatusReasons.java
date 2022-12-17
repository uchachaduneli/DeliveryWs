package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.ParcelStatusReason;

public enum StatusReasons {
    PP(new ParcelStatusReason(1)), // globalidan axali damatebuli
    RG(new ParcelStatusReason(2)), // ofisma marshruti(kurieri) miaba
    SE(new ParcelStatusReason(4)), // kurierma naxa
    PU(new ParcelStatusReason(8)); // kurierma aigo an ofisshi mivida klienti da portalidan daamata ofisis tanamshromelma

    private ParcelStatusReason status;

    StatusReasons(ParcelStatusReason status) {
        this.status = status;
    }

    public ParcelStatusReason getStatus() {
        return status;
    }
}
