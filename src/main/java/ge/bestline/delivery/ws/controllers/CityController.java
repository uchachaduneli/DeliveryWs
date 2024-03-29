package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.CityDao;
import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.repositories.CityRepository;
import ge.bestline.delivery.ws.repositories.ZoneRepository;
import ge.bestline.delivery.ws.services.CityService;
import ge.bestline.delivery.ws.util.ExcelHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/city")
public class CityController {

    private final CityRepository cityRepository;
    private final CityService cityService;
    private final ZoneRepository zoneRepository;
    private final ExcelHelper excelHelper;
    private final CityDao dao;

    public CityController(CityRepository cityRepository,
                          CityService cityService,
                          ZoneRepository zoneRepository,
                          ExcelHelper excelHelper, CityDao dao) {
        this.cityRepository = cityRepository;
        this.cityService = cityService;
        this.zoneRepository = zoneRepository;
        this.excelHelper = excelHelper;
        this.dao = dao;
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> downloadExcell(City searchParams) {
        log.info("Excel Generation & Download Started ");
        try {
            InputStreamResource file = new InputStreamResource(excelHelper.citiesToExcelFile(cityRepository.findAll()));
            log.info("Excel Generation Finished, Returning The File");
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cities.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
        } catch (Exception ex) {
            log.error("Error Occurred During Excel Generation", ex);
            return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @Transactional
    public City addNew(@RequestBody City obj) {
        log.info("Adding New City: " + obj.toString());
        return cityRepository.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<City> updateById(@PathVariable Integer id, @RequestBody City request) {
        log.info("Updating City");
        City existing = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        existing.setName(request.getName());
        existing.setCode(request.getCode());
        existing.setZone(zoneRepository.findById(request.getZone().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Zone Using This ID : " + request.getZone().getId())));
        City updatedObj = cityRepository.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        City existing = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting City: " + existing.toString());
        existing.setDeleted(1);
        cityRepository.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            City searchParams) {
        return new ResponseEntity<>(dao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public City getCitiesById(@PathVariable Integer id) {
        return cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

    @GetMapping(path = "/longestDestZone/{cityOneId}/{cityTwoId}")
    public ResponseEntity<City> getLongestDestZone(@PathVariable Integer cityOneId, @PathVariable Integer cityTwoId) {
        return ResponseEntity.ok(cityService.getLongestDestZone(cityOneId, cityTwoId));
    }

}
