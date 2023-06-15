package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.ParcelStatusReason;

import java.util.HashSet;
import java.util.Set;

public enum StatusReasons {
    PP(new ParcelStatusReason(1)), // globalidan axali damatebuli
    RG(new ParcelStatusReason(2)), // ofisma marshruti(kurieri) miaba
    SE(new ParcelStatusReason(4)), // kurierma naxa
    PU(new ParcelStatusReason(8)), // kurierma aigo an ofisshi mivida klienti da portalidan daamata ofisis tanamshromelma
    WC(new ParcelStatusReason(11)),
    CC(new ParcelStatusReason(167)),
    OK1(new ParcelStatusReason(13)),
    OK2(new ParcelStatusReason(14)),
    OK3(new ParcelStatusReason(15)),
    OK4(new ParcelStatusReason(16)),
    OK5(new ParcelStatusReason(17)),
    OK6(new ParcelStatusReason(18));

    private ParcelStatusReason status;

    StatusReasons(ParcelStatusReason status) {
        this.status = status;
    }

    public ParcelStatusReason getStatus() {
        return status;
    }

    public Set<Integer> getOkStatusIdes() {
        Set<Integer> ides = new HashSet<>();
        ides.add(OK1.status.getId());
        ides.add(OK2.status.getId());
        ides.add(OK3.status.getId());
        ides.add(OK4.status.getId());
        ides.add(OK5.status.getId());
        ides.add(OK6.status.getId());
        return ides;
    }
}
