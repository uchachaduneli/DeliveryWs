package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Parcel;
import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import ge.bestline.delivery.ws.repositories.ParcelRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(path = "/check")
public class GlobalSiteUnAuthorizedController {

    private final ParcelRepository repo;

    public GlobalSiteUnAuthorizedController(ParcelRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = "/{barCode}")
    public ResponseEntity<ParcelStatusReason> getParcelStatusByBarCode(@PathVariable String barCode) {
        log.info("Checking Parcels Status By BarCode From Global Sites Login Page. BarCode:" + barCode);
        Parcel parcel = repo.findByBarCode(barCode).orElseThrow(() -> new ResourceNotFoundException("Can't find Parcel Using This BarCode : " + barCode));
        return ResponseEntity.ok(parcel.getStatus());
    }
}
