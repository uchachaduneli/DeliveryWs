package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.Packages;
import ge.bestline.delivery.ws.entities.Parcel;
import lombok.Data;

import java.util.List;

@Data
public class ParcelWithPackagesDTO {
    private Parcel parcel;
    private List<Packages> packages;

    public ParcelWithPackagesDTO(Parcel parcel, List<Packages> packages) {
        this.parcel = parcel;
        this.packages = packages;
    }
}
