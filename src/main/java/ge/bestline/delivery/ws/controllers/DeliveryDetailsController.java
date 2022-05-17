package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.DeliveryDetailDao;
import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.entities.DeliveryDetail;
import ge.bestline.delivery.ws.entities.Parcel;
import ge.bestline.delivery.ws.repositories.*;
import ge.bestline.delivery.ws.services.BarCodeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/deliveryDetails")
public class DeliveryDetailsController {

    private final DeliveryDetailRepository repo;
    private final BarCodeService barCodeService;
    private final DeliveryDetailDao dao;
    private final RouteRepository routeRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final DeliveryDetailsRepository deliveryDetailsRepository;

    public DeliveryDetailsController(DeliveryDetailRepository repo,
                                     BarCodeService barCodeService,
                                     RouteRepository routeRepository,
                                     UserRepository userRepository,
                                     WarehouseRepository warehouseRepository,
                                     DeliveryDetailDao dao,
                                     DeliveryDetailsRepository deliveryDetailsRepository) {
        this.repo = repo;
        this.barCodeService = barCodeService;
        this.routeRepository = routeRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.dao = dao;
        this.deliveryDetailsRepository = deliveryDetailsRepository;
    }

    @PostMapping
    @Transactional
    public DeliveryDetail addNew(@RequestBody DeliveryDetail obj) {
        log.info("Adding New DeliveryDetail: " + obj.toString());
        routeRepository.findById(obj.getRoute().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Can't find Route Using This ID : " + obj.getRoute().getId()));
        userRepository.findById(obj.getUser().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Can't find User Using This ID : " + obj.getUser().getId()));
        warehouseRepository.findById(obj.getWarehouse().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Can't find Warehouse Using This ID : " + obj.getWarehouse().getId()));
        return repo.save(obj);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            DeliveryDetail searchParams) {
        log.info("Getting DeliveryDetails with params: " + searchParams);
        return new ResponseEntity<>(dao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }

    @GetMapping("barcode")
    public ResponseEntity<String> getBarCodeForNewDetails() {
        log.info("Geting Barcode For new Delivery Details Started");
        String barcode = barCodeService.getBarcodes(1).get(0);
        if (deliveryDetailsRepository.findByDetailBarCode(barcode).isPresent()) {
            // try to generate one more time to find not existing one in delivery details table
            barcode = barCodeService.getBarcodes(1).get(0);
        }
        return new ResponseEntity<>(barcode, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public DeliveryDetail getById(@PathVariable Integer id) {
        log.info("Getting DeliveryDetail With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        DeliveryDetail existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting DeliveryDetail: " + existing.toString());
        repo.delete(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }
}
